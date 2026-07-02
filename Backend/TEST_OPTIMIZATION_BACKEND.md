# ⚡ Backend Pytest Speed Optimization Guide

This guide details how to target, filter, and run backend tests inside your Docker environment instantly.

---

## 🏎️ Quick Optimization Summary

| Speedup Method | Terminal Command | Expected Speedup | Recommended Use Case |
| :--- | :--- | :--- | :--- |
| **Run Single File** | `docker exec flask_app pytest tests/test_auth.py` | 🟢 **90% faster** | Active development on a specific blueprint/module |
| **Grep Single Test** | `docker exec flask_app pytest -k "test_login"` | 🟢 **95% faster** | Debugging a single endpoint or constraint |
| **Quiet Mode** | `docker exec flask_app pytest -q -p no:warnings` | 🟡 **10% - 15% faster** | Eliminates CLI rendering overhead |
| **Fail-Fast (Exit Early)** | `docker exec flask_app pytest -x` | 🟡 **Varies** | Stops immediately on the first failed test |

---

## 🛠️ 1. Running Targeted Tests (Highly Recommended)
By default, the backend has **166 tests**. Bypassing the full suite saves considerable execution time.

### A. Run a Single Test File
To run only the authentication tests:
```bash
docker exec flask_app pytest tests/test_auth.py
```

To run only the vehicle endpoints tests:
```bash
docker exec flask_app pytest tests/test_vehicles.py
```

### B. Run a Specific Test Case (By Name)
Use the `-k` flag to execute only tests that match a specific text query:
```bash
docker exec flask_app pytest -k "test_login_success"
```

---

## ⚙️ 2. Speed Boost Flags
Combine these flags with pytest for faster console outputs:
* **`-q` (Quiet):** Minimizes output logs to a simple dot progress bar.
* **`-p no:warnings`:** Disables displaying deprecation and user warnings.
* **`-x` (Fail-Fast):** Stops the test suite immediately upon the first failure instead of executing the remaining tests.

### Example (Super-Fast, Targeted Run):
```bash
docker exec flask_app pytest tests/test_auth.py -k "login" -q -p no:warnings -x
```
*This command runs login-related tests inside `test_auth.py`, hides warnings, uses concise output formats, and exits instantly if a failure is found.*
