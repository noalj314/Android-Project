from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os

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
    photo = db.Column(db.String(200), nullable=True)  # add default later
    email = db.Column(db.String(120), unique=True, nullable=False)
    description = db.Column(db.String(200), nullable=False)

    # Relationships
    created_events = db.relationship('Event', back_populates="event_created_by", lazy='dynamic')
    followed_events = db.relationship('Event', secondary=event_followed, back_populates="event_followed_by")
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

    comments = db.relationship('Comment', back_populates='user')

    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'email': self.email,
            'description': self.description,
            'photo': self.photo
        }

    def username_to_dict(self):
        return {
            'username': self.username
        }


class Event(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    title = db.Column(db.String(100), nullable=False)
    created_by_user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    location = db.Column(db.String(100), nullable=False)
    date = db.Column(db.String, nullable=False)
    description = db.Column(db.String(200), nullable=False)

    # Relationships

    event_created_by = db.relationship('User', back_populates='created_events')
    comments = db.relationship('Comment', back_populates='event')
    event_followed_by = db.relationship('User', back_populates='followed_events')

    def event_to_dict(self):
        return {
            'id': self.id,
            'title': self.title,
            'created_by_user_id': self.created_by_user_id,
            'location': self.location,
            'date': self.date,
            'description': self.description
        }


class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    text = db.Column(db.String(200), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    event_id = db.Column(db.String, db.ForeignKey('event.id'), nullable=False)

    # Relationships
    user = db.relationship('User', back_populates='comments')
    event = db.relationship('Event', back_populates='comments')



class BlockedTokens(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    time = db.Column(db.DateTime, nullable=False)
    jti = db.Column(db.String(36), nullable=False, index=True)
