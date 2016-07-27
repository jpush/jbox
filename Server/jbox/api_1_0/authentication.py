from flask import g, jsonify
from flask_httpauth import HTTPBasicAuth
from . import api
from .errors import unauthorized

auth = HTTPBasicAuth()


@api.route('/token')
def get_token():
    if g.current_user.is_anonymous() or g.token_used:
        return unauthorized('Invalid credentials')
    return jsonify({'token': g.current_user.generate_auth_token(expiration=3600), 'expiration': 3600})
