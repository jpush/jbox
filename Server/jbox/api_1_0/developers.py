from flask import abort, Flask, jsonify, request, make_response
from . import api
from ..models import db, Developer, Channel, generate_dev_key


@api.route('/developers', methods=['POST'])
def create_developer():
    if not request.json or not 'platform_id' in request.json:
        abort(400)
    dev_key = generate_dev_key()
    developer = Developer(dev_key=dev_key,
                          platform=request.json['platform'],
                          platform_id=request.json['platform_id'],
                          username=request.json['username'])
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


@api.route('/developers/<dev_key>',methods=['GET'])
def get_developer_info(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    return jsonify({'dev_key':developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform}), 200


@api.route('/developers/<string:dev_key>/channels', methods=['POST'])
def create_channel(dev_key):
    if not request.json or not 'channel' in request.json:
        abort(400)
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    channel = Channel.query.filter_by(developer_id=developer.id).first()
    if channel is None or channel.channel != request.json['channel']:
        new_channel = Channel(developer=developer, channel=request.json['channel'])
        db.session.add(new_channel)
        try:
            db.session.commit()
        except:
            db.session.rollback()
            abort(500)
    else:
        return jsonify({'created': False}), 304
    return jsonify({'created': True}), 201


@api.route('/developers/<string:dev_key>/channels/<string:channel>', methods=['DELETE'])
def delete_channel(dev_key, channel):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    channels = Channel.query.filter_by(developer_id=developer.id).all()
    for item in channels:
        if item.channel == channel:
            db.session.delete(item)
            try:
                db.session.commit()
                return jsonify({'deleted': True}), 200
            except:
                db.session.rollback()
                abort(500)
    abort(404)


# @api.route('/developers/<string:dev_key>/channels', methods=['GET'])
# def get_channels(dev_key):
#     developer = Developer.query.filter_by(dev_key=dev_key).first()
#     if developer is None:
#         abort(404)
#     channels = developer.channels;
#     return jsonify({channels.to})
