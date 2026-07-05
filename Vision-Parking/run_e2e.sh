#!/usr/bin/env bash
# run_e2e.sh
# Convenience wrapper to run the Appium/Pytest E2E suite locally.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"
python -m pytest tests/ \
  -v --tb=short \
  --html=tests/report.html \
  --self-contained-html \
  "$@"
