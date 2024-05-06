import datetime
from unittest.mock import patch

import pytest

from app import *
from google.oauth2 import id_token



import uuid
import os

# Get the current directory

# Define the database file path

url = 'http://127.0.0.1:5000'

# HOW TO RUN:
# coverage run -m pytest test_server.py
# coverage report -m

# IMPORTANT: Save your database before, this destroys the database.
app.config.update({
    "SQLALCHEMY_DATABASE_URI": 'sqlite:///.//our.db'  # file-based SQLite database in the current directory
})
@pytest.fixture(scope="session", autouse=True)
def test_init():
    with app.app_context():
        db.drop_all()
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
    """User will be created 1"""
    # Mock the verify_oauth2_token function to return a specific result
    with patch.object(id_token, 'verify_oauth2_token') as mock_verify:
        # This is the result that verify_oauth2_token will return
        mock_verify.return_value = {
            'iss': 'accounts.google.com',
            'sub': 'us1',
            'email': 'test1@example.com',
            'email_verified': True,
            'name': '1',
            'picture': 'http://example.com/image.jpg',
            'given_name': '1',
            'family_name': '1',
            'locale': 'en'
        }

        # Now when you make a request with any idToken, it will be considered valid
        response = client.post('/user/login/', json={'idToken': 'test'})
        # Continue with your assertions
        assert response.status_code == 200
        jwt_token = response.json['access_token']

    return jwt_token

@pytest.fixture()
def test_login_user_2(client):
    """create suer 2"""
    # Mock the verify_oauth2_token function to return a specific result
    with patch.object(id_token, 'verify_oauth2_token') as mock_verify:
        # This is the result that verify_oauth2_token will return
        mock_verify.return_value = {
            'iss': 'accounts.google.com',
            'sub': '2',
            'email': 'test2@example.com',
            'email_verified': True,
            'name': 'user2',
            'picture': 'http://example.com/image.jpg',
            'given_name': '2',
            'family_name': '2',
            'locale': 'en'
        }

        # Now when you make a request with any idToken, it will be considered valid
        response = client.post('/user/login/', json={'idToken': 'test2'})
        # Continue with your assertions
        assert response.status_code == 200
        jwt_token = response.json['access_token']

    return jwt_token

def test_follow(client, test_login, test_login_user_2):
    """Test if a user can follow another user. To test this be logged in as user 1."""
    jwt_token = test_login
    response = client.post('/user/follow/2', headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200

def test_unfollow(client, test_login, test_login_user_2):
    jwt_token = test_login
    response = client.post('/user/unfollow/2', headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


def test_create_event(client, test_login):
    jwt_token = test_login
    event_data = {
        "title": "Test Event",
        "description": "This is a test event",
        "location": "Test Location",
        "date": "12"
    }
    response = client.post('/event/create', headers={'Authorization': f'Bearer {jwt_token}'},
                           json=event_data)
    assert response.status_code == 200

def test_follow_event(client, test_login_user_2):
    jwt_token = test_login_user_2
    response = client.post('/event/follow/1',  headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


def test_unfollow_event(client, test_login_user_2):
    jwt_token = test_login_user_2
    response = client.post('/event/unfollow/1', headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 400


def test_comment_event(client, test_login):
    jwt_token = test_login
    comment_data = {
        "text": "This is a comment"
    }
    response = client.post('/event/comment/1', headers={'Authorization': f'Bearer {jwt_token}'}, json=comment_data)
    assert response.status_code == 200


def test_uncomment_event(client, test_login):
    jwt_token = test_login
    response = client.post('/event/uncomment/1', headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


def test_del_event(client, test_login):
    jwt_token = test_login
    response = client.post('/event/delete/1',  headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


def test_get_followers(client, test_login, test_login_user_2):
    # First, make user 2 follow user 1
    jwt_token_user_2 = test_login_user_2
    client.post('/user/follow/us1', headers={'Authorization': f'Bearer {jwt_token_user_2}'})

    # Then retrieve followers for user 1
    jwt_token_user_1 = test_login
    response = client.get('/user/get_followers/1', headers={'Authorization': f'Bearer {jwt_token_user_1}'})
    assert response.status_code == 200
    followers = response.json
    assert any(follower['username'] == '2' for follower in followers)


def test_invalid_login(client):
    # Attempt login with an invalid token
    response = client.post('/user/login/', json={'idToken': 'invalid_token'})
    assert response.status_code == 401
    assert 'Invalid token' in response.json['message']


def test_access_denied_without_jwt(client):
    response = client.post('/user/follow/us1')
    assert response.status_code == 401
    assert 'msg' in response.json and response.json['msg'] == 'Missing Authorization Header'


