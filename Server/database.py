from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os

from flask_bcrypt import Bcrypt

app = Flask(__name__)
if "AZURE_POSTGRESQL_CONNECTIONSTRING" in os.environ:
    conn = os.environ["AZURE_POSTGRESQL_CONNECTIONSTRING"]
    values = dict(x.split('=') for x in conn.split(" "))
    user = values['user']
    host = values['host']
    database = values['dbname']
    password = values['password']
    db_uri = f'postgresql+psycopg2://{user}:{password}@{host}/{database}'
else:
    db_uri = 'sqlite:///.//our.db'
bcrypt = Bcrypt(app)

app.config['SQLALCHEMY_DATABASE_URI'] = db_uri

db = SQLAlchemy(app)

# Tables
user_followed = db.Table('user_followed',
                         db.Column('follower_id', db.String, db.ForeignKey('user.id'), primary_key=True),
                         db.Column('followed_id', db.String, db.ForeignKey('user.id'), primary_key=True)
                         )

event_followed = db.Table('event_followed',
                          db.Column('user_id', db.String, db.ForeignKey('user.id'), primary_key=True),
                          db.Column('event_id', db.String, db.ForeignKey('event.id'), primary_key=True)
                          )

event_going = db.Table('event_going',
                       db.Column('user_id', db.String, db.ForeignKey('user.id'), primary_key=True),
                       db.Column('event_id', db.String, db.ForeignKey('event.id'), primary_key=True))


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(20), unique=True, nullable=False)
    #photo = db.Column(db.String(200), nullable=True)  # add default later
    password = db.Column(db.String, nullable=False)

    # Relationships
    created_events = db.relationship('Event', backref="user", lazy='dynamic')
    comments = db.relationship('Comment', backref='user', lazy='dynamic')

    followed_events = db.relationship('Event', secondary=event_followed,
                                      back_populates="event_followed_by", lazy='dynamic')

    follows = db.relationship('User', secondary=user_followed,
                              primaryjoin=(user_followed.c.follower_id == id),
                              secondaryjoin=(user_followed.c.followed_id == id),
                              foreign_keys=[user_followed.c.follower_id, user_followed.c.followed_id],
                              back_populates="followers", lazy='dynamic')

    followers = db.relationship('User', secondary=user_followed,
                                primaryjoin=(user_followed.c.followed_id == id),
                                secondaryjoin=(user_followed.c.follower_id == id),
                                foreign_keys=[user_followed.c.followed_id, user_followed.c.follower_id],
                                back_populates="follows", lazy='dynamic')

    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username
        }

    def username_to_dict(self):
        return {
            'username': self.username
        }
    
    def add_event(self, event):
        self.created_events.append(event)
        db.session.commit()

    def __init__(self, username, password):
        self.username = username
        self.password = bcrypt.generate_password_hash(password).decode('utf-8')


class Event(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(20), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    location = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(200), nullable=False)
    photo = db.Column(db.String(200), nullable=False)  # no default

    # Relationships

    comments = db.relationship('Comment', backref='event', lazy='dynamic')

    event_followed_by = db.relationship('User', secondary=event_followed,
                                        back_populates="followed_events", lazy='dynamic')

    def to_dict(self):
        return {
            'username': self.username,
            'location': self.location,
            'description': self.description
        }
    
    def __init__(self, username, user_id, description, location, photo):
        self.username = username
        self.user_id = user_id
        self.description = description
        self.location = location
        self.photo = photo


class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    text = db.Column(db.String(200), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    event_id = db.Column(db.Integer, db.ForeignKey('event.id'), nullable=False)


class BlockedTokens(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    time = db.Column(db.DateTime, nullable=False)
    jti = db.Column(db.String(36), nullable=False, index=True)
