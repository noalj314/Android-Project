from datetime import datetime

from flask import request, jsonify
from flask_jwt_extended import create_access_token
from flask_jwt_extended import get_jwt_identity
from flask_jwt_extended import jwt_required
from flask_jwt_extended import JWTManager
from flask_bcrypt import Bcrypt
from database import *
import requests
from google.oauth2 import id_token
from google.auth.transport import requests

from authlib.integrations.flask_client import OAuth

app.config['JWT_SECRET_KEY'] = "London calling to the faraway towns..."
bcrypt = Bcrypt(app)
jwt = JWTManager(app)

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


@jwt.token_in_blocklist_loader
def check_if_token_in_blocklist(jwt_header, jwt_payload):
    """Check if token is in blocklist."""
    jti = jwt_payload["jti"]
    token = BlockedTokens.query.filter_by(jti=jti).first()
    return token is not None


@app.route('/')
def homepage():
    return '<a href="/login">Login with Google</a>'


@app.route('/user/login/', methods=['POST'])
def login():
    """ Authorize login with Google. Takes a idToken from Google and checks if correct.
    We then check if we have a user in our database with the given username, if so login else create user.
    Then returns a jwt token."""
    json_input = request.get_json()
    token = json_input["idToken"]

    try:

        google_request = requests.Request()
        data = id_token.verify_oauth2_token(token, google_request, google)

        if data['iss'] in ['accounts.google.com', 'https://accounts.google.com']:
            username = data["sub"]
            email = data["email"]
            description = data["name"]

            current_user = User.query.filter_by(username=username).first()
            if current_user is None:
                current_user = User(username=username, email=email, description=description)
                db.session.add(current_user)
                db.session.commit()

            access_token = create_access_token(identity=current_user.id)
            data = current_user.to_dict()
            data['access_token'] = access_token
            return jsonify(data), 200

    except ValueError:
        return jsonify({'message': 'Invalid token'}), 401


@app.route('/user/logout', methods=['POST'])
@jwt_required()
def logout():
    """Logout user then add token to blocklist."""
    jti = get_jwt_identity()
    db.session.add(BlockedTokens(token_id=jti, time=datetime.now()))
    db.session.commit()
    return jsonify({'message': "logged out"}), 200


@app.route('/user/follow/<username>', methods=['POST'])
@jwt_required()
def follow_user(username):
    """Follow a user that exists takes a username as argument. Requires a jwt token. """
    current_user_id = get_jwt_identity()
    user_to_follow = User.query.filter_by(username=username).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if user_to_follow is None:
        return jsonify({'message': 'No such user to follow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if current_user == user_to_follow:
        return jsonify({'message': 'You cannot follow yourself'}), 400
    if user_to_follow in current_user.follows:
        return jsonify({'message': 'Already following'}), 400
    current_user.follows.append(user_to_follow)
    db.session.commit()
    return jsonify({'message': f"{username} followed"}), 200


@app.route('/user/unfollow/<username>', methods=['POST'])
@jwt_required()
def unfollow_user(username):
    """Unfollow a user that the current user follows takes a username as argument. Requires a jwt token."""
    current_user_id = get_jwt_identity()
    user_to_unfollow = User.query.filter_by(username=username).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if user_to_unfollow is None:
        return jsonify({'message': 'No such user to unfollow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if user_to_unfollow not in current_user.follows:
        return jsonify({'message': 'Not following'}), 400
    current_user.follows.remove(user_to_unfollow)
    db.session.commit()
    return jsonify({'message': f"{username} unfollowed"}), 200


@app.route('/user/find_user/<username>', methods=['GET'])
def find_user_by_username(username):
    """Find a user by username, used for searching. Requires a jwt token."""
    user_to_find = User.query.filter_by(username=username)
    if user_to_find is None:
        return jsonify({'message': 'No such user'}), 404
    return jsonify(user_to_find.to_dict()), 200


@app.route('/user/get_followers/<user_id>', methods=['GET'])
@jwt_required()
def get_followers(user_id):
    """Get all followers of a user."""
    user_to_check = User.query.filter_by(id=user_id).first()
    user_to_check = User.query.filter_by(id=user_id).first()
    if user_to_check is None:
        return jsonify({'message': 'Faulty login'}), 404
    return jsonify([u.username_to_dict() for u in user_to_check.followers]), 200


def get_following(user_id):
    """Get all users that a user follows."""
    user_to_check = User.query.filter_by(id=user_id).first()
    if user_to_check is None:
        return jsonify({'message': 'Faulty login'}), 404
    return [u for u in user_to_check.follows], 200


@app.route('/event/create', methods=['POST'])
@jwt_required()
def create_event():
    user_id = get_jwt_identity()
    event_data = request.get_json()
    try:
        event = Event(title=event_data['title'],
                      description=event_data['description'],
                      date=event_data['date'], location=event_data['location'], user_id=user_id)
        db.session.add(event)
        User.query.filter_by(id=user_id).first().created_events.append(event)

        db.session.commit()
        return jsonify({'message': 'Event created'}), 200
    except KeyError:
        return jsonify({'message': 'Missing required data'}), 400

@app.route("/event/delete/<event_id>", methods=['POST'])
@jwt_required()
def delete_event(event_id):
    """Delete an event by event id. Requires a jwt token."""
    user_id = get_jwt_identity()
    event_to_delete = db.session.get(Event, event_id)
    if event_to_delete is None:
        return jsonify({'message': 'No such event to delete'}), 404

    user = User.query.filter_by(id=user_id).first()
    if user is None:
        return jsonify({'message': 'No such user'}), 404
    db.session.delete(event_to_delete)
    user.created_events.remove(event_to_delete)

    db.session.commit()
    return jsonify({'message': 'Event deleted'}), 200


@app.route('/event/follow/<event_id>', methods=['POST'])
@jwt_required()
def follow_event(event_id):
    """Follow an event by event id. Requires a jwt token."""
    current_user_id = get_jwt_identity()
    event_to_follow = Event.query.filter_by(id=event_id).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if event_to_follow is None:
        return jsonify({'message': 'No such event to follow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if event_to_follow in current_user.followed_events:
        return jsonify({'message': 'Already following'}), 400
    current_user.followed_events.append(event_to_follow)
    db.session.commit()
    return jsonify({'message': f"{event_id} followed"}), 200


@app.route('/event/unfollow/<event_id>', methods=['POST'])
@jwt_required()
def unfollow_event(event_id):
    """Unfollow an event by event id. Requires a jwt token."""
    current_user_id = get_jwt_identity()
    event_to_unfollow = User.query.filter_by(id=event_id).first()
    current_user = User.query.filter_by(id=current_user_id).first()
    if event_to_unfollow is None:
        return jsonify({'message': 'No such event to unfollow'}), 404
    if current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    if event_to_unfollow not in current_user.followed_events:
        return jsonify({'message': 'Not following'}), 400
    current_user.followed_events.remove(event_to_unfollow)
    db.session.commit()
    return jsonify({'message': f"{event_id} unfollowed"}), 200


@app.route('/event/comment/<event_id>', methods=['POST'])
@jwt_required()
def comment_event(event_id):
    """Comment an event by event id. Requires a jwt token."""
    user_id = get_jwt_identity()
    event = Event.query.filter_by(id=event_id).first()
    if User.query.filter_by(id=user_id).first() is None:
        return jsonify({'message': 'No such user'}), 404
    if event is None:
        return jsonify({'message': 'No such event'}), 404
    text = request.get_json()['text']
    comment = Comment(text=text, user_id=user_id, event_id=event_id)
    db.session.add(comment)
    event.comments.append(comment)
    db.session.commit()
    return jsonify({'message': 'Comment added'}), 200


@app.route('/event/uncomment/<comment_id>', methods=['POST'])
@jwt_required()
def uncomment_event(comment_id):
    """Uncomment an event by comment id. Requires a jwt token."""
    comment = Comment.query.filter_by(id=comment_id).first()
    if comment is not None and comment.user_id == get_jwt_identity():
        db.session.delete(comment)
        db.session.commit()
        return jsonify({'message': 'Comment removed'}), 200
    else:
        return jsonify({'message': 'No such comment or not authorized'}), 400


@app.route('/event/get_events/', methods=['GET'])
def get_events():
    """Get all events."""
    events = Event.query.all()
    return jsonify([event.to_dict() for event in events]), 200

@app.route('/user/get_following/get_events', methods=['GET'])
@jwt_required()
def get_events_from_following():
    """Get all events from all users the current user follows."""
    current_user_id = get_jwt_identity()
    events = []
    for user in get_following(current_user_id):
        events += user.created_events
    return jsonify([event.to_dict() for event in events]), 200


if __name__ == '__main__':
    app.run(debug=True)
