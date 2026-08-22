# -*- coding: utf-8 -*-
"""
Integration & Unit Tests for NeamaNextGen and SasaNextGen
"""
import pytest
from neama_module.NeamaNextGen import NeamaNextGen
from neama_module.SasaNextGen import SasaNextGen

def test_neama_nextgen_initialization():
    module = NeamaNextGen()
    status = module.get_status()
    assert status["status"] == "OPERATIONAL"
    assert status["version"] == "NextGen 5.0"
    assert status["capabilities_count"] >= 5
    assert "Auto-architectural Design" in status["capabilities"]

def test_neama_nextgen_upgrade():
    module = NeamaNextGen()
    upgrade_message = module.upgrade()
    assert "تحسين مستمر" in upgrade_message

def test_sasa_nextgen_compatibility_inheritance():
    compat_module = SasaNextGen()
    status = compat_module.get_status()
    assert status["status"] == "OPERATIONAL"
    assert "Cybersecurity Fortification & Zero-Trust Governance" in status["capabilities"]
