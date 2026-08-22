import os
import sqlite3
import logging
from urllib.parse import urlparse
from contextlib import contextmanager

from .base import HAS_SQLALCHEMY, Base, current_tenant_id, set_current_tenant_id

logger = logging.getLogger("NeamaAI.Database")

DEFAULT_POSTGRES_URL = "postgresql://sasa:mE82jUP81UCiswCx0el53ObD76z6Qjht@dpg-d9ukajlbedkc73ae85vg-a/sasa_4hfv"
RAW_DATABASE_URL = os.environ.get("DATABASE_URL") or os.environ.get("INTERNAL_DATABASE_URL") or DEFAULT_POSTGRES_URL

def normalize_database_url(url: str) -> str:
    if not url:
        return "sqlite:///./local_saas.db"
    if url.startswith("postgres://"):
        return url.replace("postgres://", "postgresql+psycopg2://", 1)
    if url.startswith("postgresql://") and not url.startswith("postgresql+"):
        return url.replace("postgresql://", "postgresql+psycopg2://", 1)
    return url

DATABASE_URL = normalize_database_url(RAW_DATABASE_URL)

engine = None
SessionLocal = None
is_db_connected = False
db_connection_info = {
    "dialect": "PostgreSQL" if "postgresql" in DATABASE_URL else "SQLite",
    "host": urlparse(RAW_DATABASE_URL).hostname or "dpg-d9ukajlbedkc73ae85vg-a",
    "database": urlparse(RAW_DATABASE_URL).path.lstrip("/") or "sasa_4hfv",
    "user": urlparse(RAW_DATABASE_URL).username or "sasa",
    "status": "ready",
    "is_internal": "dpg-" in (urlparse(RAW_DATABASE_URL).hostname or ""),
    "has_orm": HAS_SQLALCHEMY,
    "connection_masked": f"postgresql://sasa:****@{urlparse(RAW_DATABASE_URL).hostname or 'dpg-d9ukajlbedkc73ae85vg-a'}/{urlparse(RAW_DATABASE_URL).path.lstrip('/') or 'sasa_4hfv'}"
}

if HAS_SQLALCHEMY:
    from sqlalchemy import create_engine, text
    from sqlalchemy.orm import sessionmaker

    def init_engine():
        global engine, SessionLocal, is_db_connected
        try:
            if "postgresql" in DATABASE_URL:
                engine = create_engine(
                    DATABASE_URL,
                    pool_size=10,
                    max_overflow=20,
                    pool_timeout=15,
                    pool_recycle=300,
                    pool_pre_ping=True
                )
            else:
                engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
            
            SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
            is_db_connected = True
            db_connection_info["status"] = "connected"
        except Exception as e:
            logger.warning(f"Could not connect to external PostgreSQL ({e}). Using local engine fallback.")
            engine = create_engine("sqlite:///./local_saas.db", connect_args={"check_same_thread": False})
            SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
            is_db_connected = True
            db_connection_info["status"] = "local_sqlite_fallback"

    try:
        init_engine()
    except Exception as err:
        logger.error(f"Engine init failed: {err}")

    @contextmanager
    def get_db_context():
        if SessionLocal is None:
            init_engine()
        db = SessionLocal()
        try:
            yield db
            db.commit()
        except Exception:
            db.rollback()
            raise
        finally:
            db.close()

    def get_db():
        if SessionLocal is None:
            init_engine()
        db = SessionLocal()
        try:
            yield db
        finally:
            db.close()

else:
    # Zero-dependency Built-in SQLite session manager
    SQLITE_PATH = os.path.join(os.getcwd(), "local_saas.db")
    
    def get_sqlite_conn():
        conn = sqlite3.connect(SQLITE_PATH)
        conn.row_factory = sqlite3.Row
        return conn

    @contextmanager
    def get_db_context():
        conn = get_sqlite_conn()
        try:
            yield conn
            conn.commit()
        except Exception:
            conn.rollback()
            raise
        finally:
            conn.close()

    def get_db():
        conn = get_sqlite_conn()
        try:
            yield conn
        finally:
            conn.close()
