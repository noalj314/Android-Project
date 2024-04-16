import pytest

from Server.app import authorize
from app import *
from database import *
import json
from flask_jwt_extended import create_access_token


import uuid

url = 'http://127.0.0.1:5000'

# HOW TO RUN:
# coverage run -m pytest test_server.py
# coverage report -m

# IMPORTANT: Save your database before, this destroys the database.
@pytest.fixture(scope="session", autouse=True)
def init():
    with app.app_context():
        db.drop_all()
        db.create_all()
        db.session.commit()

@pytest.fixture
def test_app():
    # Setup for testing
    app.config.update({
        "TESTING": True,
        "SQLALCHEMY_DATABASE_URI": 'sqlite:///:memory:'  # juse in memory database for testing
    })

    with app.app_context():
        db.create_all()
        yield app  # testing happens here
        db.drop_all()



