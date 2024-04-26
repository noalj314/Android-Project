from unittest.mock import patch

import pytest

from app import *
from google.oauth2 import id_token



import uuid
import os

# Get the current directory
current_dir = os.path.dirname(os.path.abspath(__file__))

# Define the database file path
db_file_path = os.path.join(current_dir, 'test.db')

url = 'http://127.0.0.1:5000'

# HOW TO RUN:
# coverage run -m pytest test_server.py
# coverage report -m

# IMPORTANT: Save your database before, this destroys the database.
@pytest.fixture(scope="session", autouse=True)
def init():
    with app.app_context():
        db.create_all()
        db.session.commit()

@pytest.fixture
def client():
    # Setup for testing
    app.config.update({
        "TESTING": True,
        "SQLALCHEMY_DATABASE_URI": 'sqlite:///.//our.db' # file-based SQLite database in the current directory
    })
    with app.test_client() as client:
        with app.app_context():
            yield client  # testing happens here

@pytest.fixture()
def test_login(client):
    """Two users are already created 2 and 1234567890"""
    # Mock the verify_oauth2_token function to return a specific result
    with patch.object(id_token, 'verify_oauth2_token') as mock_verify:
        # This is the result that verify_oauth2_token will return
        mock_verify.return_value = {
            'iss': 'accounts.google.com',
            'sub': '1234567890',
            'email': 'test@example.com',
            'email_verified': True,
            'name': 'Test User 2',
            'picture': 'http://example.com/image.jpg',
            'given_name': 'Test',
            'family_name': 'User',
            'locale': 'en'
        }

        # Now when you make a request with any idToken, it will be considered valid
        response = client.post('/user/login/', json={'idToken': 'test'})
        # Continue with your assertions
        assert response.status_code == 200
        jwt_token = response.json['access_token']

    return jwt_token

def test_follow(client, test_login):
    """Test if a user can follow another user. To test this be logged in as user 1."""
    jwt_token = test_login
    response = client.post('/user/follow/2', headers={'Authorization': f'Bearer {jwt_token}'})

    assert response == 200

