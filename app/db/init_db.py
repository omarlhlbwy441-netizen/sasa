import logging
from .base import HAS_SQLALCHEMY, Base
from .session import engine, SessionLocal, db_connection_info, get_db_context
from .models import Tenant, Project, ChatSession, TenantResourceUsage
from .repository import TenantRepository

logger = logging.getLogger("NeamaAI.InitDB")

def init_database_tables():
    """
    Executes schema creation and initial seeds.
    Safe and idempotent.
    """
    try:
        if HAS_SQLALCHEMY and engine is not None:
            logger.info(f"Initializing database schemas via SQLAlchemy for dialect: {db_connection_info['dialect']}...")
            Base.metadata.create_all(bind=engine)
        else:
            logger.info("Initializing database schemas via SQLite DDL...")
            with get_db_context() as db:
                cur = db.cursor()
                cur.execute("""
                CREATE TABLE IF NOT EXISTS saas_tenants (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    slug TEXT UNIQUE NOT NULL,
                    plan TEXT DEFAULT 'pro',
                    is_active INTEGER DEFAULT 1,
                    max_tokens_monthly INTEGER DEFAULT 5000000,
                    max_projects INTEGER DEFAULT 50,
                    max_members INTEGER DEFAULT 10,
                    config_json TEXT DEFAULT '{}',
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                );
                """)
                cur.execute("""
                CREATE TABLE IF NOT EXISTS saas_users (
                    id TEXT PRIMARY KEY,
                    tenant_id TEXT NOT NULL,
                    email TEXT NOT NULL,
                    full_name TEXT DEFAULT '',
                    role TEXT DEFAULT 'developer',
                    is_active INTEGER DEFAULT 1,
                    password_hash TEXT,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                );
                """)
                cur.execute("""
                CREATE TABLE IF NOT EXISTS saas_projects (
                    id TEXT PRIMARY KEY,
                    tenant_id TEXT NOT NULL,
                    user_id TEXT,
                    name TEXT NOT NULL,
                    description TEXT DEFAULT '',
                    repository_url TEXT DEFAULT '',
                    status TEXT DEFAULT 'active',
                    config_json TEXT DEFAULT '{}',
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                );
                """)
                cur.execute("""
                CREATE TABLE IF NOT EXISTS saas_chat_sessions (
                    id TEXT PRIMARY KEY,
                    tenant_id TEXT NOT NULL,
                    user_id TEXT,
                    title TEXT DEFAULT 'محادثة جديدة',
                    model_name TEXT DEFAULT 'models/gemini-2.5-flash',
                    is_pinned INTEGER DEFAULT 0,
                    metadata_json TEXT DEFAULT '{}',
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                );
                """)
                cur.execute("""
                CREATE TABLE IF NOT EXISTS saas_chat_messages (
                    id TEXT PRIMARY KEY,
                    tenant_id TEXT NOT NULL,
                    session_id TEXT NOT NULL,
                    role TEXT NOT NULL,
                    content TEXT NOT NULL,
                    attachments_json TEXT DEFAULT '[]',
                    tokens_used INTEGER DEFAULT 0,
                    created_at TEXT NOT NULL
                );
                """)
                cur.execute("""
                CREATE TABLE IF NOT EXISTS saas_agent_tasks (
                    id TEXT PRIMARY KEY,
                    tenant_id TEXT NOT NULL,
                    goal TEXT NOT NULL,
                    status TEXT DEFAULT 'pending',
                    tools_used_json TEXT DEFAULT '[]',
                    result_json TEXT DEFAULT '{}',
                    error_message TEXT,
                    duration_ms INTEGER DEFAULT 0,
                    created_at TEXT NOT NULL
                );
                """)
                cur.execute("""
                CREATE TABLE IF NOT EXISTS saas_tenant_usage (
                    id TEXT PRIMARY KEY,
                    tenant_id TEXT NOT NULL,
                    month_year TEXT NOT NULL,
                    tokens_consumed INTEGER DEFAULT 0,
                    api_calls_count INTEGER DEFAULT 0,
                    agent_tasks_count INTEGER DEFAULT 0,
                    storage_bytes_used INTEGER DEFAULT 0,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                );
                """)
                db.commit()

        # Seed default tenants and demo projects
        with get_db_context() as db:
            TenantRepository.get_or_create_tenant(
                db, 
                tenant_id="tenant_neama_main",
                name="مؤسسة نعمه أي للذكاء الاصطناعي",
                plan="enterprise"
            )
            TenantRepository.get_or_create_tenant(
                db,
                tenant_id="tenant_default",
                name="مساحة العمل الافتراضية (Workspace)",
                plan="pro"
            )
            projects = TenantRepository.list_projects(db, "tenant_neama_main")
            if not projects:
                TenantRepository.create_project(
                    db,
                    name="منصة نعمه أي - SaaS Multi-Tenant Engine",
                    description="المشروع المركزي متعدد المستأجرين مع قاعدة بيانات PostgreSQL سحابية",
                    repo_url="https://github.com/omarlhlbwy441-netizen/sasa",
                    tenant_id="tenant_neama_main"
                )

        logger.info("Database schemas and seed data ready.")
        return {"success": True, "message": "Database initialized successfully", "info": db_connection_info}
    except Exception as e:
        logger.error(f"Database initialization error: {e}")
        return {"success": False, "error": str(e), "info": db_connection_info}

if __name__ == "__main__":
    init_database_tables()
