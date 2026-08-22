import json
import logging
from datetime import datetime, timezone
from typing import List, Optional, Dict, Any

from .base import HAS_SQLALCHEMY, get_current_tenant_id
from .models import (
    Tenant, Project, ChatSession, ChatMessage,
    AIAgentTask, TenantResourceUsage
)

logger = logging.getLogger("NeamaAI.Repository")

class TenantRepository:
    """
    Data Access Layer strictly enforcing multi-tenant isolation.
    Operates identically on PostgreSQL/SQLAlchemy and Built-in SQLite.
    """

    @staticmethod
    def get_or_create_tenant(db, tenant_id: str, name: str = "مؤسسة نعمه أي", plan: str = "pro") -> Dict[str, Any]:
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                tenant = db.query(Tenant).filter(Tenant.id == tenant_id).first()
                if not tenant:
                    tenant = Tenant(
                        id=tenant_id,
                        name=name,
                        slug=tenant_id.lower().replace("_", "-"),
                        plan=plan,
                        is_active=True
                    )
                    db.add(tenant)
                    db.commit()
                    db.refresh(tenant)
                return tenant.to_dict()

        # SQLite Fallback
        cur = db.cursor()
        cur.execute("SELECT * FROM saas_tenants WHERE id = ?", (tenant_id,))
        row = cur.fetchone()
        if row:
            return dict(row)
        
        now = datetime.now(timezone.utc).isoformat()
        cur.execute(
            """INSERT INTO saas_tenants (id, name, slug, plan, is_active, max_tokens_monthly, max_projects, max_members, config_json, created_at, updated_at)
               VALUES (?, ?, ?, ?, 1, 5000000, 50, 10, '{}', ?, ?)""",
            (tenant_id, name, tenant_id.lower().replace("_", "-"), plan, now, now)
        )
        db.commit()
        return {
            "id": tenant_id, "name": name, "slug": tenant_id.lower().replace("_", "-"),
            "plan": plan, "is_active": True, "max_tokens_monthly": 5000000, "max_projects": 50, "max_members": 10
        }

    @staticmethod
    def list_tenants(db, limit: int = 50) -> List[Dict[str, Any]]:
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                tenants = db.query(Tenant).filter(Tenant.is_active == True).limit(limit).all()
                return [t.to_dict() for t in tenants]

        cur = db.cursor()
        cur.execute("SELECT * FROM saas_tenants WHERE is_active = 1 LIMIT ?", (limit,))
        return [dict(row) for row in cur.fetchall()]

    @staticmethod
    def create_project(db, name: str, description: str = "", repo_url: str = "", tenant_id: Optional[str] = None) -> Dict[str, Any]:
        t_id = tenant_id or get_current_tenant_id()
        now = datetime.now(timezone.utc).isoformat()
        
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                proj = Project(
                    tenant_id=t_id,
                    name=name,
                    description=description,
                    repository_url=repo_url,
                    status="active"
                )
                db.add(proj)
                db.commit()
                db.refresh(proj)
                return proj.to_dict()

        import uuid
        p_id = str(uuid.uuid4())
        cur = db.cursor()
        cur.execute(
            """INSERT INTO saas_projects (id, tenant_id, name, description, repository_url, status, config_json, created_at, updated_at)
               VALUES (?, ?, ?, ?, ?, 'active', '{}', ?, ?)""",
            (p_id, t_id, name, description, repo_url, now, now)
        )
        db.commit()
        return {
            "id": p_id, "tenant_id": t_id, "name": name, "description": description,
            "repository_url": repo_url, "status": "active", "created_at": now, "updated_at": now
        }

    @staticmethod
    def list_projects(db, tenant_id: Optional[str] = None) -> List[Dict[str, Any]]:
        t_id = tenant_id or get_current_tenant_id()
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                projects = db.query(Project).filter(Project.tenant_id == t_id).order_by(Project.created_at.desc()).all()
                return [p.to_dict() for p in projects]

        cur = db.cursor()
        cur.execute("SELECT * FROM saas_projects WHERE tenant_id = ? ORDER BY created_at DESC", (t_id,))
        return [dict(row) for row in cur.fetchall()]

    @staticmethod
    def delete_project(db, project_id: str, tenant_id: Optional[str] = None) -> bool:
        t_id = tenant_id or get_current_tenant_id()
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                proj = db.query(Project).filter(Project.id == project_id, Project.tenant_id == t_id).first()
                if proj:
                    db.delete(proj)
                    db.commit()
                    return True
                return False

        cur = db.cursor()
        cur.execute("DELETE FROM saas_projects WHERE id = ? AND tenant_id = ?", (project_id, t_id))
        db.commit()
        return cur.rowcount > 0

    @staticmethod
    def save_chat_message(db, session_id: str, role: str, content: str, attachments: list = None, tokens_used: int = 0, tenant_id: Optional[str] = None) -> Dict[str, Any]:
        t_id = tenant_id or get_current_tenant_id()
        now = datetime.now(timezone.utc).isoformat()
        
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                sess = db.query(ChatSession).filter(ChatSession.id == session_id, ChatSession.tenant_id == t_id).first()
                if not sess:
                    sess = ChatSession(id=session_id, tenant_id=t_id, title=content[:40] if role == "user" else "محادثة نعمه أي")
                    db.add(sess)
                    db.flush()
                msg = ChatMessage(
                    tenant_id=t_id,
                    session_id=session_id,
                    role=role,
                    content=content,
                    attachments_json=json.dumps(attachments or []),
                    tokens_used=tokens_used
                )
                db.add(msg)
                TenantRepository.record_usage(db, tokens_consumed=tokens_used, api_calls=1, tenant_id=t_id)
                db.commit()
                db.refresh(msg)
                return msg.to_dict()

        # SQLite
        import uuid
        msg_id = str(uuid.uuid4())
        cur = db.cursor()
        # Ensure session exists
        cur.execute("SELECT id FROM saas_chat_sessions WHERE id = ? AND tenant_id = ?", (session_id, t_id))
        if not cur.fetchone():
            cur.execute(
                """INSERT INTO saas_chat_sessions (id, tenant_id, title, model_name, is_pinned, metadata_json, created_at, updated_at)
                   VALUES (?, ?, ?, 'models/gemini-2.5-flash', 0, '{}', ?, ?)""",
                (session_id, t_id, content[:40] if role == "user" else "محادثة نعمه أي", now, now)
            )
        
        cur.execute(
            """INSERT INTO saas_chat_messages (id, tenant_id, session_id, role, content, attachments_json, tokens_used, created_at)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?)""",
            (msg_id, t_id, session_id, role, content, json.dumps(attachments or []), tokens_used, now)
        )
        TenantRepository.record_usage(db, tokens_consumed=tokens_used, api_calls=1, tenant_id=t_id)
        db.commit()
        return {
            "id": msg_id, "tenant_id": t_id, "session_id": session_id,
            "role": role, "content": content, "tokens_used": tokens_used, "created_at": now
        }

    @staticmethod
    def list_chat_sessions(db, tenant_id: Optional[str] = None) -> List[Dict[str, Any]]:
        t_id = tenant_id or get_current_tenant_id()
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                sessions = db.query(ChatSession).filter(ChatSession.tenant_id == t_id).order_by(ChatSession.updated_at.desc()).all()
                return [s.to_dict() for s in sessions]

        cur = db.cursor()
        cur.execute("SELECT * FROM saas_chat_sessions WHERE tenant_id = ? ORDER BY updated_at DESC", (t_id,))
        return [dict(row) for row in cur.fetchall()]

    @staticmethod
    def log_agent_task(db, goal: str, status: str, tools_used: list, result: dict, duration_ms: int = 0, tenant_id: Optional[str] = None) -> Dict[str, Any]:
        t_id = tenant_id or get_current_tenant_id()
        now = datetime.now(timezone.utc).isoformat()
        
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                task = AIAgentTask(
                    tenant_id=t_id,
                    goal=goal,
                    status=status,
                    tools_used_json=json.dumps(tools_used or []),
                    result_json=json.dumps(result or {}),
                    duration_ms=duration_ms
                )
                db.add(task)
                TenantRepository.record_usage(db, agent_tasks=1, tenant_id=t_id)
                db.commit()
                db.refresh(task)
                return task.to_dict()

        import uuid
        task_id = str(uuid.uuid4())
        cur = db.cursor()
        cur.execute(
            """INSERT INTO saas_agent_tasks (id, tenant_id, goal, status, tools_used_json, result_json, duration_ms, created_at)
               VALUES (?, ?, ?, ?, ?, ?, ?, ?)""",
            (task_id, t_id, goal, status, json.dumps(tools_used or []), json.dumps(result or {}), duration_ms, now)
        )
        TenantRepository.record_usage(db, agent_tasks=1, tenant_id=t_id)
        db.commit()
        return {
            "id": task_id, "tenant_id": t_id, "goal": goal, "status": status,
            "duration_ms": duration_ms, "created_at": now
        }

    @staticmethod
    def record_usage(db, tokens_consumed: int = 0, api_calls: int = 0, agent_tasks: int = 0, tenant_id: Optional[str] = None):
        t_id = tenant_id or get_current_tenant_id()
        current_month = datetime.now(timezone.utc).strftime("%Y-%m")
        now = datetime.now(timezone.utc).isoformat()

        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            if isinstance(db, Session):
                usage = db.query(TenantResourceUsage).filter(
                    TenantResourceUsage.tenant_id == t_id,
                    TenantResourceUsage.month_year == current_month
                ).first()
                if not usage:
                    usage = TenantResourceUsage(
                        tenant_id=t_id,
                        month_year=current_month,
                        tokens_consumed=tokens_consumed,
                        api_calls_count=api_calls,
                        agent_tasks_count=agent_tasks
                    )
                    db.add(usage)
                else:
                    usage.tokens_consumed += tokens_consumed
                    usage.api_calls_count += api_calls
                    usage.agent_tasks_count += agent_tasks
                return

        # SQLite
        cur = db.cursor()
        cur.execute("SELECT id, tokens_consumed, api_calls_count, agent_tasks_count FROM saas_tenant_usage WHERE tenant_id = ? AND month_year = ?", (t_id, current_month))
        row = cur.fetchone()
        if not row:
            import uuid
            u_id = str(uuid.uuid4())
            cur.execute(
                """INSERT INTO saas_tenant_usage (id, tenant_id, month_year, tokens_consumed, api_calls_count, agent_tasks_count, storage_bytes_used, created_at, updated_at)
                   VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?)""",
                (u_id, t_id, current_month, tokens_consumed, api_calls, agent_tasks, now, now)
            )
        else:
            cur.execute(
                """UPDATE saas_tenant_usage SET tokens_consumed = tokens_consumed + ?, api_calls_count = api_calls_count + ?, agent_tasks_count = agent_tasks_count + ?, updated_at = ?
                   WHERE tenant_id = ? AND month_year = ?""",
                (tokens_consumed, api_calls, agent_tasks, now, t_id, current_month)
            )
        db.commit()

    @staticmethod
    def get_tenant_stats(db, tenant_id: Optional[str] = None) -> Dict[str, Any]:
        t_id = tenant_id or get_current_tenant_id()
        current_month = datetime.now(timezone.utc).strftime("%Y-%m")
        tenant = TenantRepository.get_or_create_tenant(db, t_id)
        
        if HAS_SQLALCHEMY:
            from sqlalchemy.orm import Session
            from sqlalchemy import func
            if isinstance(db, Session):
                usage = db.query(TenantResourceUsage).filter(
                    TenantResourceUsage.tenant_id == t_id,
                    TenantResourceUsage.month_year == current_month
                ).first()
                proj_count = db.query(func.count(Project.id)).filter(Project.tenant_id == t_id).scalar() or 0
                chat_count = db.query(func.count(ChatSession.id)).filter(ChatSession.tenant_id == t_id).scalar() or 0
                task_count = db.query(func.count(AIAgentTask.id)).filter(AIAgentTask.tenant_id == t_id).scalar() or 0
                tokens = usage.tokens_consumed if usage else 0
                max_tokens = tenant.get("max_tokens_monthly", 5000000) if isinstance(tenant, dict) else tenant.max_tokens_monthly
                return {
                    "tenant": tenant if isinstance(tenant, dict) else tenant.to_dict(),
                    "month": current_month,
                    "project_count": proj_count,
                    "chat_sessions_count": chat_count,
                    "agent_tasks_count": task_count,
                    "tokens_consumed": tokens,
                    "tokens_limit": max_tokens,
                    "tokens_percentage": round((tokens / max(1, max_tokens)) * 100, 2),
                    "api_calls_count": usage.api_calls_count if usage else 0
                }

        # SQLite
        cur = db.cursor()
        cur.execute("SELECT COUNT(*) FROM saas_projects WHERE tenant_id = ?", (t_id,))
        proj_count = cur.fetchone()[0]
        cur.execute("SELECT COUNT(*) FROM saas_chat_sessions WHERE tenant_id = ?", (t_id,))
        chat_count = cur.fetchone()[0]
        cur.execute("SELECT COUNT(*) FROM saas_agent_tasks WHERE tenant_id = ?", (t_id,))
        task_count = cur.fetchone()[0]
        cur.execute("SELECT tokens_consumed, api_calls_count FROM saas_tenant_usage WHERE tenant_id = ? AND month_year = ?", (t_id, current_month))
        usage_row = cur.fetchone()
        tokens = usage_row["tokens_consumed"] if usage_row else 0
        api_calls = usage_row["api_calls_count"] if usage_row else 0
        max_tokens = tenant.get("max_tokens_monthly", 5000000)

        return {
            "tenant": tenant,
            "month": current_month,
            "project_count": proj_count,
            "chat_sessions_count": chat_count,
            "agent_tasks_count": task_count,
            "tokens_consumed": tokens,
            "tokens_limit": max_tokens,
            "tokens_percentage": round((tokens / max(1, max_tokens)) * 100, 2),
            "api_calls_count": api_calls
        }
