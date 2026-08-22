import contextvars
from datetime import datetime, timezone

# Context variable to hold the current tenant ID per request/async context
current_tenant_id: contextvars.ContextVar[str] = contextvars.ContextVar("current_tenant_id", default="tenant_default")

HAS_SQLALCHEMY = False
Base = None
TenantMixin = None

try:
    from sqlalchemy import Column, String, DateTime
    from sqlalchemy.orm import declarative_base, declared_attr
    
    Base = declarative_base()
    
    class _TenantMixin:
        @declared_attr
        def tenant_id(cls):
            return Column(String(64), nullable=False, index=True, default=lambda: current_tenant_id.get())

        created_at = Column(DateTime, default=lambda: datetime.now(timezone.utc), nullable=False)
        updated_at = Column(DateTime, default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc), nullable=False)

    TenantMixin = _TenantMixin
    HAS_SQLALCHEMY = True
except ImportError:
    class _DummyBase:
        pass
    Base = _DummyBase
    class _DummyTenantMixin:
        pass
    TenantMixin = _DummyTenantMixin
    HAS_SQLALCHEMY = False

def get_current_tenant_id() -> str:
    """Return the tenant_id from the current execution context."""
    return current_tenant_id.get()

def set_current_tenant_id(tenant_id: str) -> None:
    """Set the tenant_id in the current execution context."""
    current_tenant_id.set(tenant_id)
