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
    "SQLALCHEMY_DATABASE_URI": 'sqlite:///.//our.db'  # file-based SQLite database in the current directory
})

def test_init():
    with app.app_context():
        db.drop_all()
        db.create_all()
        db.session.commit()
