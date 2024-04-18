from datetime import datetime

from flask import Flask, request, jsonify, url_for, session, redirect
from flask_sqlalchemy import SQLAlchemy
from flask_sqlalchemy import SQLAlchemy
from flask_jwt_extended import create_access_token
from flask_jwt_extended import get_jwt_identity
from flask_jwt_extended import jwt_required
from flask_jwt_extended import JWTManager
from flask_jwt_extended import get_jwt
from flask_bcrypt import Bcrypt
from database import *

from authlib.integrations.flask_client import OAuth
import os

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///./our.db'
app.config['JWT_SECRET_KEY'] = "London calling to the faraway towns..."
bcrypt = Bcrypt(app)
jwt = JWTManager(app)
db = SQLAlchemy(app)

oauth = OAuth(app)
google = oauth.register(
    name='google',
    client_id="637976557829-ki6fc4ubhd3pn9d5mpcrn9mu77unqm89.apps.googleusercontent.com",
    client_secret="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",
    access_token_url='https://accounts.google.com/o/oauth2/token',
    access_token_params=None,
    authorize_url='https://accounts.google.com/o/oauth2/auth',
    authorize_params=None,
    api_base_url='https://www.googleapis.com/oauth2/v1/',
    userinfo_endpoint='https://www.googleapis.com/oauth2/v1/userinfo',
    client_kwargs={'scope': 'openid profile email'},
)

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


@app.route('/')
def homepage():
    return '<a href="/login">Login with Google</a>'


@app.route('/authorize_login/', methods=['POST'])
def login():
    input = request.get_json()
    token = input["idToken"]

    try:
        google_request = oauth.Request()
        data = oauth.verify_oauth2_token(token, google_request)

        if data['iss'] in ['accounts.google.com', 'https://accounts.google.com']:
            username = data["sub"]
            email = data["email"]
            description = data["name"]

            user = User.query.filter_by(username=username).first()
            if user is None:
                user = User(username=username, email=email, description=description)
                db.session.add(user)
                db.session.commit()

            access_token = create_access_token(identity=user.id)
            data = user.to_dict()
            data['access_token'] = access_token
            return jsonify(data), 200

    except ValueError:
        return jsonify({'message': 'Invalid token'}), 401


@app.route('/follow/user/<username>', methods=['POST'])
@jwt_required()
def follow_user(username):
    current_user_id = get_jwt_identity()
    user_to_follow = User.query.filter_by(username=username).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if user_to_follow is None:
        return jsonify({'message': 'No such user to follow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if current_user == user_to_follow:
        return jsonify({'message': 'You cannot follow yourself'}), 400
    if user_to_follow in current_user.followed:
        return jsonify({'message': 'Already following'}), 400
    current_user.followed.append(user_to_follow)
    db.session.commit()
    return jsonify({'message': f"{username} followed"}), 200


@app.route('/unfollow/user/<username>', methods=['POST'])
@jwt_required()
def unfollow_user(username):
    current_user_id = get_jwt_identity()
    user_to_unfollow = User.query.filter_by(username=username).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if user_to_unfollow is None:
        return jsonify({'message': 'No such user to unfollow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if user_to_unfollow not in current_user.followed:
        return jsonify({'message': 'Not following'}), 400
    current_user.followed.remove(user_to_unfollow)
    db.session.commit()
    return jsonify({'message': f"{username} unfollowed"}), 200


@app.route('/user/find_user/<username>', methods=['GET'])
@jwt_required()
def find_user_by_username(username):
    user = User.query.filter_by(username=username)
    if user is None:
        return jsonify({'message': 'No such user'}), 404
    return jsonify(user.to_dict()), 200


@app.route('/event/create/', methods=['POST'])
@jwt_required()
def create_event():
    user_id = get_jwt_identity()
    event_data = request.get_json()
    try:
        event = Event(title=event_data['title'],
                      description=event_data['description'],
                      date=event_data['date'], location=event_data['location'], created_by_user_id=user_id)
    except KeyError:
        return jsonify({'message': 'Missing data'}), 400

    User.query.filter_by(id=user_id).first().event_created.append(event)  # Add event to user's created events
    db.session.commit()
    return jsonify({'message': 'Event created'}), 200

@app.route("event/delete/<event_id>", methods=['POST'])
@jwt_required()
def delete_event(event_id):
    user_id = get_jwt_identity()
    try:
        User.query.filter_by(id=user_id).first().event_created.remove(event_id)
    except ValueError:
        return jsonify({'message': 'No such event to delete'}), 404
    db.session.commit()
    return jsonify({'message': 'Event deleted'}), 200


@app.route('event/follow/<event_id>', methods=['POST'])
@jwt_required()
def follow_event(event_id):
    current_user_id = get_jwt_identity()
    event_to_follow = User.query.filter_by(id=event_id).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if event_to_follow is None:
        return jsonify({'message': 'No such event to follow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if event_to_follow in current_user.events:
        return jsonify({'message': 'Already following'}), 400
    current_user.events.append(event_to_follow)
    db.session.commit()
    return jsonify({'message': f"{event_id} followed"}), 200


@app.route('/event/unfollow/<event_id>', methods=['POST'])
@jwt_required()
def unfollow_event(event_id):
    current_user_id = get_jwt_identity()
    event_to_unfollow = User.query.filter_by(id=event_id).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if event_to_unfollow is None:
        return jsonify({'message': 'No such event to unfollow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if event_to_unfollow not in current_user.events:
        return jsonify({'message': 'Not following'}), 400
    current_user.events.remove(event_to_unfollow)
    db.session.commit()
    return jsonify({'message': f"{event_id} unfollowed"}), 200


@app.route('/<event-id>/comment/', methods=['POST'])
@jwt_required()
def comment_event(event_id):
    user_id = get_jwt_identity()
    user = User.query.filter_by(id=user_id).first()
    event = Event.query.filter_by(id=event_id).first()
    if user is None:
        return jsonify({'message': 'No such user'}), 404
    if event is None:
        return jsonify({'message': 'No such event'}), 404
    text = request.get_json()['text']
    comment = Comment(text=text, user_id=user_id, event_id=event_id)
    event.comments.append(comment)
    db.session.commit()
    return jsonify({'message': 'Comment added'}), 200


@app.route('/uncomment/<comment_id>', methods=['POST'])
@jwt_required()
def uncomment_event(comment_id):
    comment = Comment.query.get(id=comment_id)
    if comment is not None and comment.user_id == get_jwt_identity():
        db.session.delete(comment)
        db.session.commit()
        return jsonify({'message': 'Comment removed'}), 200
    else:
        return jsonify({'message': 'No such comment or not authorized'}), 400


if __name__ == '__main__':
    app.run(debug=True)
