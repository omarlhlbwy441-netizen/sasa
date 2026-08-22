import uuid
import json
from datetime import datetime, timezone
from .base import Base, TenantMixin, HAS_SQLALCHEMY

def generate_uuid() -> str:
    return str(uuid.uuid4())

if HAS_SQLALCHEMY:
    from sqlalchemy import (
        Column, String, Text, Integer, Boolean, DateTime, Index, BigInteger
    )

    class Tenant(Base):
        __tablename__ = "saas_tenants"
        id = Column(String(64), primary_key=True, default=generate_uuid)
        name = Column(String(128), nullable=False)
        slug = Column(String(64), unique=True, nullable=False, index=True)
        plan = Column(String(32), default="pro", nullable=False)
        is_active = Column(Boolean, default=True, nullable=False)
        max_tokens_monthly = Column(BigInteger, default=5000000)
        max_projects = Column(Integer, default=50)
        max_members = Column(Integer, default=10)
        config_json = Column(Text, default="{}")
        created_at = Column(DateTime, default=lambda: datetime.now(timezone.utc), nullable=False)
        updated_at = Column(DateTime, default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc), nullable=False)

        def to_dict(self):
            return {
                "id": self.id,
                "name": self.name,
                "slug": self.slug,
                "plan": self.plan,
                "is_active": self.is_active,
                "max_tokens_monthly": self.max_tokens_monthly,
                "max_projects": self.max_projects,
                "max_members": self.max_members,
                "created_at": self.created_at.isoformat() if hasattr(self.created_at, "isoformat") else str(self.created_at),
                "updated_at": self.updated_at.isoformat() if hasattr(self.updated_at, "isoformat") else str(self.updated_at)
            }

    class Project(Base, TenantMixin):
        __tablename__ = "saas_projects"
        id = Column(String(64), primary_key=True, default=generate_uuid)
        user_id = Column(String(64), nullable=True)
        name = Column(String(128), nullable=False)
        description = Column(Text, default="")
        repository_url = Column(String(256), default="")
        status = Column(String(32), default="active")
        config_json = Column(Text, default="{}")

        def to_dict(self):
            return {
                "id": self.id,
                "tenant_id": self.tenant_id,
                "name": self.name,
                "description": self.description,
                "repository_url": self.repository_url,
                "status": self.status,
                "created_at": self.created_at.isoformat() if hasattr(self.created_at, "isoformat") else str(self.created_at),
                "updated_at": self.updated_at.isoformat() if hasattr(self.updated_at, "isoformat") else str(self.updated_at)
            }

    class ChatSession(Base, TenantMixin):
        __tablename__ = "saas_chat_sessions"
        id = Column(String(64), primary_key=True, default=generate_uuid)
        user_id = Column(String(64), nullable=True)
        title = Column(String(256), default="محادثة جديدة")
        model_name = Column(String(64), default="models/gemini-2.5-flash")
        is_pinned = Column(Boolean, default=False)
        metadata_json = Column(Text, default="{}")

        def to_dict(self):
            return {
                "id": self.id,
                "tenant_id": self.tenant_id,
                "title": self.title,
                "model_name": self.model_name,
                "is_pinned": self.is_pinned,
                "created_at": self.created_at.isoformat() if hasattr(self.created_at, "isoformat") else str(self.created_at),
                "updated_at": self.updated_at.isoformat() if hasattr(self.updated_at, "isoformat") else str(self.updated_at)
            }

    class ChatMessage(Base, TenantMixin):
        __tablename__ = "saas_chat_messages"
        id = Column(String(64), primary_key=True, default=generate_uuid)
        session_id = Column(String(64), nullable=False, index=True)
        role = Column(String(32), nullable=False)
        content = Column(Text, nullable=False)
        attachments_json = Column(Text, default="[]")
        tokens_used = Column(Integer, default=0)

        def to_dict(self):
            return {
                "id": self.id,
                "tenant_id": self.tenant_id,
                "session_id": self.session_id,
                "role": self.role,
                "content": self.content,
                "attachments": json.loads(self.attachments_json or "[]"),
                "tokens_used": self.tokens_used,
                "created_at": self.created_at.isoformat() if hasattr(self.created_at, "isoformat") else str(self.created_at)
            }

    class AIAgentTask(Base, TenantMixin):
        __tablename__ = "saas_agent_tasks"
        id = Column(String(64), primary_key=True, default=generate_uuid)
        goal = Column(Text, nullable=False)
        status = Column(String(32), default="pending")
        tools_used_json = Column(Text, default="[]")
        result_json = Column(Text, default="{}")
        error_message = Column(Text, nullable=True)
        duration_ms = Column(Integer, default=0)

        def to_dict(self):
            return {
                "id": self.id,
                "tenant_id": self.tenant_id,
                "goal": self.goal,
                "status": self.status,
                "tools_used": json.loads(self.tools_used_json or "[]"),
                "result": json.loads(self.result_json or "{}"),
                "error_message": self.error_message,
                "duration_ms": self.duration_ms,
                "created_at": self.created_at.isoformat() if hasattr(self.created_at, "isoformat") else str(self.created_at)
            }

    class TenantResourceUsage(Base, TenantMixin):
        __tablename__ = "saas_tenant_usage"
        id = Column(String(64), primary_key=True, default=generate_uuid)
        month_year = Column(String(7), nullable=False)
        tokens_consumed = Column(BigInteger, default=0)
        api_calls_count = Column(Integer, default=0)
        agent_tasks_count = Column(Integer, default=0)
        storage_bytes_used = Column(BigInteger, default=0)

        def to_dict(self):
            return {
                "id": self.id,
                "tenant_id": self.tenant_id,
                "month_year": self.month_year,
                "tokens_consumed": self.tokens_consumed,
                "api_calls_count": self.api_calls_count,
                "agent_tasks_count": self.agent_tasks_count,
                "storage_bytes_used": self.storage_bytes_used,
                "updated_at": self.updated_at.isoformat() if hasattr(self.updated_at, "isoformat") else str(self.updated_at)
            }

else:
    # Python pure object fallback for environments without SQLAlchemy installed yet
    class Tenant:
        def __init__(self, id=None, name="", slug="", plan="pro", is_active=True, max_tokens_monthly=5000000, max_projects=50, max_members=10, config_json="{}", created_at=None, updated_at=None):
            self.id = id or generate_uuid()
            self.name = name
            self.slug = slug
            self.plan = plan
            self.is_active = is_active
            self.max_tokens_monthly = max_tokens_monthly
            self.max_projects = max_projects
            self.max_members = max_members
            self.config_json = config_json
            self.created_at = created_at or datetime.now(timezone.utc).isoformat()
            self.updated_at = updated_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "name": self.name, "slug": self.slug, "plan": self.plan,
                "is_active": self.is_active, "max_tokens_monthly": self.max_tokens_monthly,
                "max_projects": self.max_projects, "max_members": self.max_members,
                "created_at": str(self.created_at), "updated_at": str(self.updated_at)
            }

    class Project:
        def __init__(self, id=None, tenant_id="tenant_default", name="", description="", repository_url="", status="active", config_json="{}", created_at=None, updated_at=None):
            self.id = id or generate_uuid()
            self.tenant_id = tenant_id
            self.name = name
            self.description = description
            self.repository_url = repository_url
            self.status = status
            self.config_json = config_json
            self.created_at = created_at or datetime.now(timezone.utc).isoformat()
            self.updated_at = updated_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "tenant_id": self.tenant_id, "name": self.name,
                "description": self.description, "repository_url": self.repository_url,
                "status": self.status, "created_at": str(self.created_at), "updated_at": str(self.updated_at)
            }

    class ChatSession:
        def __init__(self, id=None, tenant_id="tenant_default", title="محادثة جديدة", model_name="models/gemini-2.5-flash", is_pinned=False, metadata_json="{}", created_at=None, updated_at=None):
            self.id = id or generate_uuid()
            self.tenant_id = tenant_id
            self.title = title
            self.model_name = model_name
            self.is_pinned = is_pinned
            self.metadata_json = metadata_json
            self.created_at = created_at or datetime.now(timezone.utc).isoformat()
            self.updated_at = updated_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "tenant_id": self.tenant_id, "title": self.title,
                "model_name": self.model_name, "is_pinned": self.is_pinned,
                "created_at": str(self.created_at), "updated_at": str(self.updated_at)
            }

    class ChatMessage:
        def __init__(self, id=None, tenant_id="tenant_default", session_id="", role="user", content="", attachments_json="[]", tokens_used=0, created_at=None):
            self.id = id or generate_uuid()
            self.tenant_id = tenant_id
            self.session_id = session_id
            self.role = role
            self.content = content
            self.attachments_json = attachments_json
            self.tokens_used = tokens_used
            self.created_at = created_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "tenant_id": self.tenant_id, "session_id": self.session_id,
                "role": self.role, "content": self.content,
                "attachments": json.loads(self.attachments_json or "[]") if isinstance(self.attachments_json, str) else self.attachments_json,
                "tokens_used": self.tokens_used, "created_at": str(self.created_at)
            }

    class AIAgentTask:
        def __init__(self, id=None, tenant_id="tenant_default", goal="", status="pending", tools_used_json="[]", result_json="{}", error_message=None, duration_ms=0, created_at=None):
            self.id = id or generate_uuid()
            self.tenant_id = tenant_id
            self.goal = goal
            self.status = status
            self.tools_used_json = tools_used_json
            self.result_json = result_json
            self.error_message = error_message
            self.duration_ms = duration_ms
            self.created_at = created_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "tenant_id": self.tenant_id, "goal": self.goal, "status": self.status,
                "tools_used": json.loads(self.tools_used_json or "[]") if isinstance(self.tools_used_json, str) else self.tools_used_json,
                "result": json.loads(self.result_json or "{}") if isinstance(self.result_json, str) else self.result_json,
                "error_message": self.error_message, "duration_ms": self.duration_ms, "created_at": str(self.created_at)
            }

    class TenantResourceUsage:
        def __init__(self, id=None, tenant_id="tenant_default", month_year="", tokens_consumed=0, api_calls_count=0, agent_tasks_count=0, storage_bytes_used=0, updated_at=None):
            self.id = id or generate_uuid()
            self.tenant_id = tenant_id
            self.month_year = month_year
            self.tokens_consumed = tokens_consumed
            self.api_calls_count = api_calls_count
            self.agent_tasks_count = agent_tasks_count
            self.storage_bytes_used = storage_bytes_used
            self.updated_at = updated_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "tenant_id": self.tenant_id, "month_year": self.month_year,
                "tokens_consumed": self.tokens_consumed, "api_calls_count": self.api_calls_count,
                "agent_tasks_count": self.agent_tasks_count, "storage_bytes_used": self.storage_bytes_used,
                "updated_at": str(self.updated_at)
            }

    class User:
        def __init__(self, id=None, tenant_id="tenant_default", email="", full_name="", role="developer", is_active=True, created_at=None):
            self.id = id or generate_uuid()
            self.tenant_id = tenant_id
            self.email = email
            self.full_name = full_name
            self.role = role
            self.is_active = is_active
            self.created_at = created_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "tenant_id": self.tenant_id, "email": self.email,
                "full_name": self.full_name, "role": self.role, "is_active": self.is_active,
                "created_at": str(self.created_at)
            }

    class ApiKey:
        def __init__(self, id=None, tenant_id="tenant_default", name="Default Key", key_prefix="", scope="full_access", is_active=True, created_at=None):
            self.id = id or generate_uuid()
            self.tenant_id = tenant_id
            self.name = name
            self.key_prefix = key_prefix
            self.scope = scope
            self.is_active = is_active
            self.created_at = created_at or datetime.now(timezone.utc).isoformat()

        def to_dict(self):
            return {
                "id": self.id, "tenant_id": self.tenant_id, "name": self.name,
                "key_prefix": self.key_prefix, "scope": self.scope, "is_active": self.is_active,
                "created_at": str(self.created_at)
            }
