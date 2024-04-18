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
    password  = values['password']
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
event_created = db.Table('event_created',
                       db.Column('user_id', db.String, db.ForeignKey('user.id'), primary_key=True),
                       db.Column('event_id', db.String, db.ForeignKey('event.id'), primary_key=True))


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(20), unique=True, nullable=False)
    photo = db.Column(db.String(200), nullable=True)  # add default later
    email = db.Column(db.String(120), unique=True, nullable=False)
    description = db.Column(db.String(200), nullable=False)


    # Relationships
    event_created = db.relationship('Event', backref="user", lazy='dynamic')
    event_followed = db.relationship('Event', secondary=event_followed, back_populates="event_followed")
    follows = db.relationship('User', secondary=user_followed, back_populates="users")
    followed = db.relationship(
        'User', secondary=user_followed,
        primaryjoin=(user_followed.c.follower_id == id),
        secondaryjoin=(user_followed.c.followed_id == id),
        back_populates='followers', lazy='dynamic')

    comments = db.relationship('User', back_populates='user')

    def to_dict(self):
        return {
            'id': self.id,
            'username': self.username,
            'email': self.email,
            'description': self.description,
            'photo': self.photo
        }


class Event(db.Model):
    id = db.Column(db.String, primary_key=True)
    title = db.Column(db.String(100), nullable=False)
    created_by_user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    location = db.Column(db.String(100), nullable=False)
    date = db.Column(db.DateTime, nullable=False)
    description = db.Column(db.String(200), nullable=False)

    # Relationships
    interested = db.relationship('User', secondary=event_followed,
                                 back_populates="events")

    comments = db.relationship('Comment', back_populates='event')


class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    text = db.Column(db.String(200), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    event_id = db.Column(db.String, db.ForeignKey('event.id'), nullable=False)

    user = db.relationship('User', back_populates='comments')
    event = db.relationship('Event', back_populates='comments')