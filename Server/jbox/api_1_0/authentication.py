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
def get_auth3(platform, platform_id):
    if email == '':
        g.current_user = AnonymousUser()
        return True
    developer = Developer.query.filter_by(platform=platform, platform_id=platform_id).first()
    if developer is None:
        return False
    return True