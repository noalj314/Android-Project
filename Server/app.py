import datetime

from flask import logging, request, jsonify
from flask_jwt_extended import create_access_token
from flask_jwt_extended import get_jwt_identity
from flask_jwt_extended import jwt_required
from flask_jwt_extended import JWTManager
from flask_bcrypt import Bcrypt
from database import *
import requests

app.config['JWT_SECRET_KEY'] = "London calling to the faraway towns..."
bcrypt = Bcrypt(app)
jwt = JWTManager(app)


@jwt.token_in_blocklist_loader
def check_if_token_in_blocklist(jwt_header, jwt_payload):
    """Check if token is in blocklist."""
    jti = jwt_payload["jti"]
    token = BlockedTokens.query.filter_by(jti=jti).first()
    return token is not None


#--------------User----------------#

@app.route('/user/create', methods=['POST'])
def create_user():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    u = User.query.filter_by(username=username).first()

    if u is not None:
        return jsonify({'status': 'fail', 'message': "username taken"}), 400
    
    u = User(username=username, password=password)
    db.session.add(u)
    db.session.commit()
    return "", 200


@app.route('/user/login', methods=['POST'])
def user_login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    
    u = User.query.filter_by(username=username).first()

    if u is None:
        return jsonify({'message': 'No such user or wrong password', 'status': 'fail'}), 400
    elif bcrypt.check_password_hash(u.password, password):
        access_token = create_access_token(identity=u.id, expires_delta=datetime.timedelta(hours=1))
        return jsonify({'token' : access_token}), 200
    else:
        return jsonify({'message': 'No such user or wrong password', 'status': 'fail'}), 400


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
        return jsonify({'message': 'No such user follow'}), 200
    elif current_user == user_to_follow:
        return jsonify({'message': 'You cannot follow yourself'}), 200
    elif user_to_follow in current_user.follows:
        return jsonify({'message': 'Already following'}), 200
    
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
    elif current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    elif user_to_unfollow not in current_user.follows:
        return jsonify({'message': 'Not following'}), 400
    
    current_user.follows.remove(user_to_unfollow)
    db.session.commit()
    
    return jsonify({'message': f"{username} unfollowed"}), 200


@app.route('/user/find_user/<username>', methods=['GET'])
def find_user_by_username(username):
    """Find a user by username, used for searching."""
    user_to_find = User.query.filter_by(username=username).first()

    if user_to_find is None:
        print(f"No user found with username: {username}")
        return jsonify({'message': 'No such user'}), 404
    
    print(f"User found: {user_to_find}")
    return jsonify(user_to_find.username_to_dict()), 200


@app.route('/user/get_followers/<username>', methods=['GET'])
def get_followers(username):
    """Get all followers of a user."""
    user_to_check = User.query.filter_by(username=username).first()

    if user_to_check is None:
        return jsonify({'message': 'Faulty login'}), 200
    
    return jsonify({"followers":[u.username for u in user_to_check.followers]}), 200


@app.route('/user/get_following/<user>', methods=['GET'])
def get_following(user):
    """Get all users that a user follows."""
    user_to_check = User.query.filter_by(username=user).first()

    if user_to_check is None:
        return jsonify({'message': 'Faulty login'}), 200
    
    return jsonify({"following":[u.username for u in user_to_check.follows]}), 200


@app.route('/user/check_following/<user>', methods=['POST'])
@jwt_required()
def check_following(user):
    """Check if the user is followed."""
    current_user_id = get_jwt_identity()
    current_user = User.query.filter_by(id=current_user_id).first()
    user_to_check = User.query.filter_by(username=user).first()

    if user_to_check in current_user.follows:
        return jsonify({'message': "true"}), 200
    elif user_to_check not in current_user.follows:
        return jsonify({'message': "false"}), 200
        

#--------------Events----------------#

@app.route('/event/create', methods=['POST'])
@jwt_required()
def create_event():
    event_data = request.get_json()

    try:
        # Retrieve the user from the database
        username = event_data['username']
        user = User.query.filter_by(username=username).first()

        # Create the event
        event = Event(
            username=event_data['username'],
            user_id=user.id, 
            location=event_data['location'], 
            description=event_data['description'],
            photo=event_data.get('photo')  # Use get() to handle optional field
        )

        # Add the event to the session and commit
        db.session.add(event)
        db.session.commit()

        return jsonify({'message': 'Event created'}), 201
    
    except KeyError as e:
        missing_field = e.args[0]
        return jsonify({'message': f'Missing required data: {missing_field}'}), 400
    except Exception as e:
        logging.exception("An error occurred while creating the event")
        return jsonify({'message': 'An error occurred'}), 500


@app.route("/event/delete/<event_id>", methods=['POST'])
@jwt_required()
def delete_event(event_id):
    """Delete an event by event id. Requires a jwt token."""
    user_id = get_jwt_identity()
    event_to_delete = db.session.get(Event, event_id)

    if event_to_delete is None:
        return jsonify({'message': 'No such event to delete'}), 404

    u = User.query.filter_by(id=user_id).first()
    if u is None:
        return jsonify({'message': 'No such user'}), 404
    
    db.session.delete(event_to_delete)
    u.created_events.remove(event_to_delete)
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
    elif current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    elif event_to_follow in current_user.followed_events:
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
    elif current_user is None:
        return jsonify({'message': 'Faulty login'}), 404
    elif event_to_unfollow not in current_user.followed_events:
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
    return jsonify({"events" : [event.to_dict() for event in events]}), 200


@app.route('/user/get_following/get_events', methods=['GET'])
@jwt_required()
def get_events_from_following():
    """Get all events from all users the current user follows."""
    current_user_id = get_jwt_identity()
    events = []
    for u in get_following(current_user_id):
        events += u.created_events
    return jsonify([event.to_dict() for event in events]), 200


if __name__ == '__main__':
    app.run(debug=True)
        