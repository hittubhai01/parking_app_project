import pytest
from fastapi.testclient import TestClient
from Backend.main import app

client = TestClient(app)

def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}

def test_create_user():
    payload = {"username": "testuser", "password": "testpass"}
    response = client.post("/users/", json=payload)
    assert response.status_code == 201
    data = response.json()
    assert data["username"] == "testuser"
