import os
import sys
import json
import time
import asyncio
from typing import Dict, List, Any, Optional

try:
    from app.health.health_engine import neama_health_engine, HERBAL_DATABASE, ANCIENT_TREATISES, MEDICAL_CURRICULUM, CLINICAL_CASES
    from app.sciences.universal_sciences import universal_science_engine, UNIVERSAL_SCIENCES_CATALOG
except ImportError:
    try:
        from health.health_engine import neama_health_engine, HERBAL_DATABASE, ANCIENT_TREATISES, MEDICAL_CURRICULUM, CLINICAL_CASES
        from sciences.universal_sciences import universal_science_engine, UNIVERSAL_SCIENCES_CATALOG
    except ImportError:
        neama_health_engine = None
        universal_science_engine = None
        HERBAL_DATABASE = {}
        ANCIENT_TREATISES = {}
        MEDICAL_CURRICULUM = []
        CLINICAL_CASES = []
        UNIVERSAL_SCIENCES_CATALOG = {}

try:
    from app.integrations.universal_cloud import UniversalCloudClient, GIT_PROVIDERS, HOSTING_PROVIDERS
except ImportError:
    try:
        from integrations.universal_cloud import UniversalCloudClient, GIT_PROVIDERS, HOSTING_PROVIDERS
    except ImportError:
        UniversalCloudClient = None
        GIT_PROVIDERS = {}
        HOSTING_PROVIDERS = {}


        run_builtin_server(port)
