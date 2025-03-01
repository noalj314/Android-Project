from unittest.mock import patch

import pytest

from app import *

# Get the current directory

# Define the database file path

url = 'http://127.0.0.1:5000'

# HOW TO RUN:
# coverage run -m pytest test_server.py
# coverage report -m

# IMPORTANT: Save your database before, this destroys the database.
app.config.update({
    "SQLALCHEMY_DATABASE_URI": 'sqlite:///.//ourtest.db'  # file-based SQLite database in the current directory
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
        "SQLALCHEMY_DATABASE_URI": 'sqlite:///.//ourtest.db' # file-based SQLite database in the current directory
    })
    with app.test_client() as client:
        with app.app_context():
            yield client  # testing happens here


def test_create_user1(client):
    """Create user 1"""
    data = {
        'username': 'user1',
        'password': 'user1',
    }
    response = client.post('/user/create', json=data)
    assert response.status_code == 200


def test_create_user2(client):
    """Create user 2"""
    data = {
        'username': 'user2',
        'password': 'user2',
    }
    response = client.post('/user/create', json=data)
    assert response.status_code == 200


@pytest.fixture()
def test_login(client):
    """Test user login with correct credentials."""
    # Then login
    login_data = {
        'username': 'user1',
        'password': 'user1'
    }
    response = client.post('/user/login', json=login_data)
    assert response.status_code == 200
    assert 'token' in response.json
    return response.json['token']


@pytest.fixture()
def test_login_user_2(client):
    """Test user login with correct credentials."""
    # Then login
    login_data = {
        'username': 'user2',
        'password': 'user2'
    }
    response = client.post('/user/login', json=login_data)
    assert response.status_code == 200
    assert 'token' in response.json
    return response.json['token']


def test_follow(client, test_login):
    """Test if a user can follow another user. To test this be logged in as user 1."""
    jwt_token = test_login
    response = client.post('/user/follow/user2', headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


#def test_unfollow(client, test_login):
#    jwt_token = test_login
#    response = client.post('/user/unfollow/user2', headers={'Authorization': f'Bearer {jwt_token}'})
#    assert response.status_code == 200


def test_create_event_1(client, test_login):
    jwt_token = test_login
    event_data = {
        "username":"user1",
        "description": "This is a test event",
        "location": "Test Location",
        "photo" : "Test photo"
    }
    response = client.post('/event/create', headers={'Authorization': f'Bearer {jwt_token}'},
                           json=event_data)
    assert response.status_code == 201


def test_create_event_2(client, test_login):
    jwt_token = test_login
    event_data = {
        "username":"user2",
        "description": "This is another test event",
        "location": "Another test Location",
        "photo" : "Another test photo"
    }
    response = client.post('/event/create', headers={'Authorization': f'Bearer {jwt_token}'},
                           json=event_data)
    assert response.status_code == 201


def test_create_event_3(client, test_login):
    jwt_token = test_login
    event_data = {
        "username":"user2",
        "description": "This is a third test event",
        "location": "A third test Location",
        "photo" : "A third test photo"
    }
    response = client.post('/event/create', headers={'Authorization': f'Bearer {jwt_token}'},
                           json=event_data)
    assert response.status_code == 201


def test_get_events(client, test_login):
    jwt_token = test_login
    response = client.get('/event/get_events',  headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


def test_get_follwing_events(client, test_login):
    jwt_token = test_login
    response = client.get('/user/get_following/get_events',  headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


def test_follow_event(client, test_login_user_2):
    jwt_token = test_login_user_2
    response = client.post('/event/follow/1',  headers={'Authorization': f'Bearer {jwt_token}'})
    assert response.status_code == 200


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


def test_access_denied_without_jwt(client):
    response = client.post('/user/follow/us1')
    assert response.status_code == 401
    assert 'msg' in response.json and response.json['msg'] == 'Missing Authorization Header'

