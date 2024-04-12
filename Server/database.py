from flask import Flask
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)

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
    photo = db.Column(db.String(200), nullable=True, default='default.jpg')
    description = db.Column(db.String(200), nullable=False)

    # Relationships
    events = db.relationship('event', secondary=event_followed, back_populate="events")
    follows = db.relationship('user', secondary=user_followed, back_populate="users")
    followed = db.relationship(
        'User', secondary=user_followed,
        primaryjoin=(user_followed.c.follower_id == id),
        secondaryjoin=(user_followed.c.followed_id == id),
        back_populates='followers', lazy='dynamic')


class Event(db.Model):
    id = db.Column(db.String, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    location = db.Column(db.String(100), nullable=False)
    date = db.Column(db.DateTime, nullable=False)
    description = db.Column(db.String(200), nullable=False)

    # Relationships
    interested = db.relationship('User', secondary=event_followed, back_populate="events")
