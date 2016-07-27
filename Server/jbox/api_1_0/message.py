from flask import abort, Flask, jsonify, request
from . import api
from ..models import Developer, Integration

@api.route('message/<integration_id>/<token>', method=['POST'])
def send_message(integration_id, token):
    developer = Integration.query.filter_by(integration_id=integration_id).first()
    if developer is None:
        abort(404)
    # TODO: verify_auth_token
    # TODO: send notification
    return