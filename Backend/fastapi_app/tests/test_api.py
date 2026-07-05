"""Basic smoke tests for the FastAPI app.

Uses the `client` fixture from conftest.py (SQLite in-memory, no Postgres needed).
"""
import pytest


def test_health_check(client):
    """GET /health should return 200 with status=healthy."""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "healthy"


def test_root(client):
    """GET / should return 200."""
    response = client.get("/")
    assert response.status_code == 200


def test_docs_available(client):
    """GET /docs should return 200 (OpenAPI UI is enabled)."""
    response = client.get("/docs")
    assert response.status_code == 200


def test_unknown_route_returns_404(client):
    """Unknown routes should return 404."""
    response = client.get("/this-route-does-not-exist")
    assert response.status_code == 404
