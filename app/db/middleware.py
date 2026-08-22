import re
import logging
from typing import Optional
from .base import set_current_tenant_id, get_current_tenant_id

logger = logging.getLogger("NeamaAI.TenantMiddleware")

def resolve_tenant_from_request(headers: dict, params: dict, cookies: dict = None) -> str:
    """
    Extract tenant_id from incoming request by priority:
    1. Header: 'X-Tenant-ID' or 'x-tenant-id'
    2. Bearer token claims (if decoded or prefixed)
    3. Query parameter: 'tenant_id'
    4. Subdomain matching (e.g. tenant1.neama.ai)
    5. Default tenant fallback: 'tenant_default'
    """
    # 1. Direct Header
    tenant_header = headers.get("x-tenant-id") or headers.get("X-Tenant-ID") or headers.get("x-workspace-id")
    if tenant_header and re.match(r"^[a-zA-Z0-9_-]{1,64}$", tenant_header):
        return tenant_header.strip()

    # 2. Authorization Header Token parsing
    auth_header = headers.get("authorization") or headers.get("Authorization") or ""
    if auth_header.startswith("Bearer "):
        token = auth_header.split(" ", 1)[1]
        # In custom tokens format 'tenant_id:signature' or standard JWT
        if ":" in token:
            t_part = token.split(":", 1)[0]
            if re.match(r"^[a-zA-Z0-9_-]{1,64}$", t_part):
                return t_part

    # 3. Query Param
    tenant_param = params.get("tenant_id") or params.get("workspace")
    if tenant_param and re.match(r"^[a-zA-Z0-9_-]{1,64}$", tenant_param):
        return tenant_param.strip()

    # 4. Host Header Subdomain
    host = headers.get("host") or ""
    parts = host.split(".")
    if len(parts) >= 3 and parts[0] not in ["www", "api", "app", "localhost"]:
        subdomain = parts[0]
        if re.match(r"^[a-zA-Z0-9_-]{1,64}$", subdomain):
            return subdomain

    return "tenant_default"

try:
    from starlette.middleware.base import BaseHTTPMiddleware
    from starlette.requests import Request
    from starlette.responses import Response

    class TenantContextMiddleware(BaseHTTPMiddleware):
        """
        FastAPI / Starlette Middleware that intercepts all requests,
        extracts the tenant identity, and attaches it to the current execution context.
        """
        async def dispatch(self, request: Request, call_next):
            headers = dict(request.headers)
            params = dict(request.query_params)
            tenant_id = resolve_tenant_from_request(headers, params)
            
            # Set context variable for downstream DB operations & logging
            set_current_tenant_id(tenant_id)
            
            # Add tenant header to response
            response: Response = await call_next(request)
            response.headers["X-Tenant-ID"] = tenant_id
            return response

except ImportError:
    class TenantContextMiddleware:
        pass
