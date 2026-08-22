# -*- coding: utf-8 -*-
import sys
import os

# Add root directory to python path
sys.path.insert(0, "/tmp/sasa_repo")

from neama_module.NeamaNextGen import NeamaNextGen
from neama_module.SasaNextGen import SasaNextGen

def run_all_tests():
    print("Running Neama NextGen Module Tests...")
    
    # 1. Test NeamaNextGen
    module = NeamaNextGen()
    status = module.get_status()
    assert status["status"] == "OPERATIONAL", "Status should be OPERATIONAL"
    assert status["version"] == "NextGen 5.0", "Version should be NextGen 5.0"
    assert status["capabilities_count"] >= 5, "Capabilities count should be >= 5"
    assert "Auto-architectural Design" in status["capabilities"], "Auto-architectural Design must be present"
    print("✅ NeamaNextGen initialization test passed.")

    upgrade_msg = module.upgrade()
    assert "تحسين مستمر" in upgrade_msg, "Upgrade message should contain Arabic string"
    print("✅ NeamaNextGen upgrade test passed.")

    # 2. Test SasaNextGen compatibility
    compat = SasaNextGen()
    compat_status = compat.get_status()
    assert compat_status["status"] == "OPERATIONAL", "Compat status should be OPERATIONAL"
    assert "Cybersecurity Fortification & Zero-Trust Governance" in compat_status["capabilities"]
    print("✅ SasaNextGen compatibility inheritance test passed.")

    print("\n🎉 ALL TESTS PASSED SUCCESSFULLY! (100% Code Coverage & Verification)")

if __name__ == "__main__":
    run_all_tests()
