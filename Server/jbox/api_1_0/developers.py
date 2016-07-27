from flask import abort, Flask, jsonify, request
from . import api
from ..models import Developer, generate_dev_key


@api.route('/developers', methods=['POST'])
def create_developer():
    if not request.json or not 'platform_id' in request.json:
        abort(400)
    dev_key = generate_dev_key()
    developer = Developer(dev_key=dev_key,
                          platform=request.json['platform'],
                          platform_id=request.json['platform_id'],
                          username=request.json['username'],
                          channel=request.json['channel'])
    developer.insert_to_db()
    return jsonify({'dev_key': developer.dev_key}), 201


@api.route('/developers/<string:platform>/<string:platform_id>', methods=['GET'])
def get_developer(platform, platform_id):
    developer = Developer.query.filter_by(platform_id=platform_id).first()
    if developer is None:
        abort(404)
    return jsonify({'dev_key': developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform}), 200

