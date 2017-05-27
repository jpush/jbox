from flask import g, jsonify
from flask_httpauth import HTTPBasicAuth
from . import api
from .errors import unauthorized
from ..models import Developer

auth = HTTPBasicAuth()


@api.route('/token')
def get_token():
    if g.current_user.is_anonymous() or g.token_used:
        return unauthorized('Invalid credentials')
    return jsonify({'token': g.current_user.generate_auth_token(expiration=3600), 'expiration': 3600})


@auth.verify_password
def verify_pw(app_key, dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if not developer:
        return False
    return True


@auth.error_handler
def auth_error():
    return jsonify({"error": "Access Denied"}), 401
