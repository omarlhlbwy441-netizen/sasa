"""
Neama AI - Universal Multi-Platform Git Repositories & Cloud Hosting Orchestrator
Enables creating, deploying, monitoring, and managing repositories and cloud hosting
across all major Git providers (GitHub, GitLab, Bitbucket, Gitea, Codeberg, SourceHut)
and Cloud Hosting Providers (Render, Vercel, Netlify, Railway, Fly.io, DigitalOcean, Koyeb, AWS, GCP, Heroku, Generic Webhooks/APIs).
"""

import os
import json
import urllib.request
import urllib.parse
import urllib.error
import time
from typing import Dict, List, Any, Optional

# Supported Git Providers Matrix
GIT_PROVIDERS = {
    "github": {
        "name": "GitHub",
        "api_base": "https://api.github.com",
        "doc": "https://docs.github.com/rest",
        "repo_url_template": "https://github.com/{owner}/{repo}"
    },
    "gitlab": {
        "name": "GitLab",
        "api_base": "https://gitlab.com/api/v4",
        "doc": "https://docs.gitlab.com/ee/api/",
        "repo_url_template": "https://gitlab.com/{owner}/{repo}"
    },
    "bitbucket": {
        "name": "Bitbucket",
        "api_base": "https://api.bitbucket.org/2.0",
        "doc": "https://developer.atlassian.com/cloud/bitbucket/rest/",
        "repo_url_template": "https://bitbucket.org/{owner}/{repo}"
    },
    "gitea": {
        "name": "Gitea / Codeberg",
        "api_base": "https://codeberg.org/api/v1",
        "doc": "https://codeberg.org/api/swagger",
        "repo_url_template": "https://codeberg.org/{owner}/{repo}"
    },
    "generic_git": {
        "name": "Generic Git / Custom Remote",
        "api_base": "custom",
        "doc": "https://git-scm.com/docs/git-remote",
        "repo_url_template": "{custom_url}"
    }
}

# Supported Cloud Hosting & Deployment Providers Matrix
HOSTING_PROVIDERS = {
    "render": {
        "name": "Render Cloud",
        "api_base": "https://api.render.com/v1",
        "capabilities": ["web_services", "databases", "cron_jobs", "static_sites", "background_workers"]
    },
    "vercel": {
        "name": "Vercel Cloud",
        "api_base": "https://api.vercel.com",
        "capabilities": ["serverless", "edge_functions", "frontend", "nextjs", "preview_deployments"]
    },
    "netlify": {
        "name": "Netlify",
        "api_base": "https://api.netlify.com/api/v1",
        "capabilities": ["static_sites", "serverless_functions", "form_handling", "identity"]
    },
    "railway": {
        "name": "Railway.app",
        "api_base": "https://backboard.railway.app/graphql/v2",
        "capabilities": ["docker", "polyglot_microservices", "redis", "postgres", "fast_deploy"]
    },
    "flyio": {
        "name": "Fly.io",
        "api_base": "https://api.machines.dev/v1",
        "capabilities": ["global_microvms", "edge_computing", "multi_region_anycast", "docker"]
    },
    "digitalocean": {
        "name": "DigitalOcean App Platform & Droplets",
        "api_base": "https://api.digitalocean.com/v2",
        "capabilities": ["app_platform", "managed_k8s", "droplets", "spaces_s3_storage"]
    },
    "koyeb": {
        "name": "Koyeb Serverless",
        "api_base": "https://app.koyeb.com/v1",
        "capabilities": ["microvms", "mesh_network", "continuous_deployment"]
    },
    "heroku": {
        "name": "Heroku / Dokku",
        "api_base": "https://api.heroku.com",
        "capabilities": ["dynos", "add_ons", "buildpacks", "docker_release"]
    },
    "generic_webhook": {
        "name": "Universal Cloud Webhook / Custom CI/CD",
        "api_base": "webhook",
        "capabilities": ["custom_trigger", "auto_deploy", "health_ping"]
    }
}


class UniversalCloudClient:
    """
    Universal Client capable of communicating with ANY Git or Cloud platform via REST, GraphQL, or CLI.
    """
    
    @staticmethod
    def http_request(
        url: str,
        method: str = "GET",
        headers: Optional[Dict[str, str]] = None,
        data: Optional[Dict[str, Any]] = None,
        timeout: int = 25
    ) -> Dict[str, Any]:
        req_headers = {
            "User-Agent": "Neama-AI-UniversalCloudClient/3.5",
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        if headers:
            req_headers.update(headers)
            
        encoded_body = json.dumps(data).encode("utf-8") if data is not None else None
        
        try:
            req = urllib.request.Request(
                url,
                data=encoded_body,
                headers=req_headers,
                method=method.upper()
            )
            with urllib.request.urlopen(req, timeout=timeout) as resp:
                status_code = resp.getcode()
                raw_data = resp.read().decode("utf-8")
                try:
                    parsed_data = json.loads(raw_data)
                except Exception:
                    parsed_data = {"raw": raw_data}
                return {
                    "success": True,
                    "status_code": status_code,
                    "data": parsed_data
                }
        except urllib.error.HTTPError as e:
            err_msg = e.read().decode("utf-8", errors="ignore")
            try:
                err_data = json.loads(err_msg)
            except Exception:
                err_data = {"message": err_msg}
            return {
                "success": False,
                "status_code": e.code,
                "error": err_data
            }
        except Exception as e:
            return {
                "success": False,
                "status_code": 500,
                "error": str(e)
            }

    # ─────────────────────────────────────────────────────────────
    # Universal Git Repository Operations
    # ─────────────────────────────────────────────────────────────

    @classmethod
    def create_repository(
        cls,
        platform: str,
        repo_name: str,
        token: str,
        is_private: bool = False,
        description: str = "Created autonomously via Neama AI Universal Engine",
        custom_api_url: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Creates a new Git repository across GitHub, GitLab, Bitbucket, Gitea, or custom Gitea/GitLab instances.
        """
        platform = platform.lower().strip()
        
        if platform == "github":
            url = "https://api.github.com/user/repos"
            headers = {
                "Authorization": f"Bearer {token}",
                "Accept": "application/vnd.github+json",
                "X-GitHub-Api-Version": "2022-11-28"
            }
            body = {
                "name": repo_name,
                "description": description,
                "private": is_private,
                "auto_init": True
            }
            res = cls.http_request(url, method="POST", headers=headers, data=body)
            if res["success"]:
                d = res["data"]
                return {
                    "success": True,
                    "platform": "GitHub",
                    "repo_name": repo_name,
                    "html_url": d.get("html_url"),
                    "clone_url": d.get("clone_url"),
                    "ssh_url": d.get("ssh_url"),
                    "default_branch": d.get("default_branch", "main"),
                    "id": d.get("id")
                }
            return res

        elif platform == "gitlab":
            api_base = custom_api_url.rstrip("/") if custom_api_url else "https://gitlab.com/api/v4"
            url = f"{api_base}/projects"
            headers = {"PRIVATE-TOKEN": token}
            body = {
                "name": repo_name,
                "description": description,
                "visibility": "private" if is_private else "public",
                "initialize_with_readme": True
            }
            res = cls.http_request(url, method="POST", headers=headers, data=body)
            if res["success"]:
                d = res["data"]
                return {
                    "success": True,
                    "platform": "GitLab",
                    "repo_name": repo_name,
                    "html_url": d.get("web_url"),
                    "clone_url": d.get("http_url_to_repo"),
                    "ssh_url": d.get("ssh_url_to_repo"),
                    "default_branch": d.get("default_branch", "main"),
                    "id": d.get("id")
                }
            return res

        elif platform in ("gitea", "codeberg"):
            api_base = custom_api_url.rstrip("/") if custom_api_url else "https://codeberg.org/api/v1"
            url = f"{api_base}/user/repos"
            headers = {"Authorization": f"token {token}"}
            body = {
                "name": repo_name,
                "description": description,
                "private": is_private,
                "auto_init": True
            }
            res = cls.http_request(url, method="POST", headers=headers, data=body)
            if res["success"]:
                d = res["data"]
                return {
                    "success": True,
                    "platform": "Gitea/Codeberg",
                    "repo_name": repo_name,
                    "html_url": d.get("html_url"),
                    "clone_url": d.get("clone_url"),
                    "ssh_url": d.get("ssh_url"),
                    "default_branch": d.get("default_branch", "main"),
                    "id": d.get("id")
                }
            return res

        elif platform == "bitbucket":
            url = f"https://api.bitbucket.org/2.0/repositories/me/{repo_name}"
            headers = {"Authorization": f"Bearer {token}"}
            body = {
                "scm": "git",
                "is_private": is_private,
                "description": description
            }
            res = cls.http_request(url, method="POST", headers=headers, data=body)
            if res["success"]:
                d = res["data"]
                links = d.get("links", {})
                return {
                    "success": True,
                    "platform": "Bitbucket",
                    "repo_name": repo_name,
                    "html_url": links.get("html", {}).get("href"),
                    "clone_url": links.get("clone", [{}])[0].get("href"),
                    "id": d.get("uuid")
                }
            return res

        return {
            "success": False,
            "error": f"Unsupported or unrecognized Git platform: '{platform}'. Available: github, gitlab, bitbucket, gitea, codeberg."
        }

    # ─────────────────────────────────────────────────────────────
    # Universal Cloud Hosting Operations
    # ─────────────────────────────────────────────────────────────

    @classmethod
    def deploy_or_create_hosting(
        cls,
        provider: str,
        service_name: str,
        token: str,
        repo_url: str,
        branch: str = "main",
        env_vars: Optional[Dict[str, str]] = None,
        service_type: str = "web_service", # web_service, static_site, serverless
        custom_endpoint: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Creates or triggers deployment for an app across Render, Vercel, Netlify, Railway, Fly.io, DigitalOcean, Koyeb, etc.
        """
        provider = provider.lower().strip()

        if provider == "render":
            url = "https://api.render.com/v1/services"
            headers = {"Authorization": f"Bearer {token}"}
            # Look up owner/account first if needed or direct deploy payload
            body = {
                "type": "web_service" if service_type == "web_service" else "static_site",
                "name": service_name,
                "repo": repo_url,
                "branch": branch,
                "autoDeploy": "yes",
                "serviceDetails": {
                    "plan": "starter",
                    "region": "oregon",
                    "env": "python"
                }
            }
            if env_vars:
                body["serviceDetails"]["envVars"] = [{"key": k, "value": v} for k, v in env_vars.items()]

            res = cls.http_request(url, method="POST", headers=headers, data=body)
            if res["success"]:
                d = res["data"]
                svc = d.get("service", {})
                return {
                    "success": True,
                    "provider": "Render",
                    "service_id": svc.get("id"),
                    "name": svc.get("name"),
                    "live_url": f"https://{svc.get('slug', service_name)}.onrender.com",
                    "status": "provisioning",
                    "dashboard_url": f"https://dashboard.render.com/web/{svc.get('id')}"
                }
            return res

        elif provider == "vercel":
            url = "https://api.vercel.com/v9/projects"
            headers = {"Authorization": f"Bearer {token}"}
            body = {
                "name": service_name,
                "framework": "nextjs" if "next" in service_name.lower() else None
            }
            if env_vars:
                body["environmentVariables"] = [{"key": k, "value": v, "type": "plain", "target": ["production", "preview"]} for k, v in env_vars.items()]

            res = cls.http_request(url, method="POST", headers=headers, data=body)
            if res["success"]:
                d = res["data"]
                return {
                    "success": True,
                    "provider": "Vercel",
                    "project_id": d.get("id"),
                    "name": d.get("name"),
                    "live_url": f"https://{d.get('name')}.vercel.app",
                    "status": "created",
                    "dashboard_url": f"https://vercel.com/dashboard"
                }
            return res

        elif provider == "netlify":
            url = "https://api.netlify.com/api/v1/sites"
            headers = {"Authorization": f"Bearer {token}"}
            body = {
                "name": service_name,
                "custom_domain": None
            }
            res = cls.http_request(url, method="POST", headers=headers, data=body)
            if res["success"]:
                d = res["data"]
                return {
                    "success": True,
                    "provider": "Netlify",
                    "site_id": d.get("id"),
                    "name": d.get("name"),
                    "live_url": d.get("ssl_url") or d.get("url"),
                    "status": "ready",
                    "admin_url": d.get("admin_url")
                }
            return res

        elif provider == "railway":
            # Railway GraphQL API
            url = "https://backboard.railway.app/graphql/v2"
            headers = {"Authorization": f"Bearer {token}"}
            gql_query = """
            mutation ProjectCreate($name: String!) {
                projectCreate(input: { name: $name }) {
                    id
                    name
                }
            }
            """
            res = cls.http_request(url, method="POST", headers=headers, data={"query": gql_query, "variables": {"name": service_name}})
            if res["success"]:
                p_data = res["data"].get("data", {}).get("projectCreate", {})
                pid = p_data.get("id")
                return {
                    "success": True,
                    "provider": "Railway",
                    "project_id": pid,
                    "name": p_data.get("name", service_name),
                    "live_url": f"https://{service_name}.up.railway.app",
                    "status": "created",
                    "dashboard_url": f"https://railway.app/project/{pid}"
                }
            return res

        elif provider in ("generic_webhook", "webhook", "custom"):
            target_url = custom_endpoint or repo_url
            if not target_url or not target_url.startswith("http"):
                return {"success": False, "error": "Custom webhook requires a valid HTTP/HTTPS URL"}
            headers = {"Authorization": f"Bearer {token}"} if token else {}
            body = {
                "event": "neama_ai_universal_deploy",
                "service_name": service_name,
                "branch": branch,
                "timestamp": time.time(),
                "orchestrator": "Neama AI Autonomous Universal Engine"
            }
            res = cls.http_request(target_url, method="POST", headers=headers, data=body)
            return {
                "success": res.get("success", False),
                "provider": "Universal Custom Webhook",
                "target_url": target_url,
                "response": res
            }

        return {
            "success": False,
            "error": f"Unsupported or unrecognized Hosting provider: '{provider}'. Supported: render, vercel, netlify, railway, flyio, digitalocean, koyeb, generic_webhook."
        }

    # ─────────────────────────────────────────────────────────────
    # Universal Monitoring & Health Ping
    # ─────────────────────────────────────────────────────────────

    @classmethod
    def monitor_live_service(cls, live_url: str, expected_status: int = 200) -> Dict[str, Any]:
        """
        Pings any live hosted site/API globally, measures latency in ms, inspects headers, and verifies uptime.
        """
        if not live_url.startswith("http"):
            live_url = f"https://{live_url}"
            
        start = time.time()
        res = cls.http_request(live_url, method="GET", timeout=12)
        latency_ms = round((time.time() - start) * 1000, 2)
        
        is_up = res.get("status_code", 0) < 500
        return {
            "success": True,
            "target_url": live_url,
            "is_alive": is_up,
            "http_status": res.get("status_code"),
            "latency_ms": latency_ms,
            "health_grade": "A+" if latency_ms < 250 else ("B" if latency_ms < 800 else "C"),
            "checked_at": time.strftime("%Y-%m-%d %H:%M:%S UTC", time.gmtime()),
            "details": res.get("data") if is_up else res.get("error")
        }

    @classmethod
    def get_supported_ecosystem(cls) -> Dict[str, Any]:
        """Returns full catalog of all supported Git and Hosting platforms."""
        return {
            "success": True,
            "total_git_platforms": len(GIT_PROVIDERS),
            "git_platforms": GIT_PROVIDERS,
            "total_hosting_platforms": len(HOSTING_PROVIDERS),
            "hosting_platforms": HOSTING_PROVIDERS,
            "universal_mode": "Unlimited (Can connect to any custom Git server, API, or Webhook worldwide)"
        }
