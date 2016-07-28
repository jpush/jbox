from flask import abort, Flask, json, jsonify, request, make_response
from . import api
from ..models import Developer, db, Channel, Integration, generate_dev_key


# 通过 body 中的 platform, platform_id, username 来创建一个 Developer
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


# 通过 platform 和 platform_id 来获取用户信息
@api.route('/developers/<string:platform>/<string:platform_id>', methods=['GET'])
def get_developer(platform, platform_id):
    developer = Developer.query.filter_by(platform_id=platform_id).first()
    if developer is None:
        abort(404)
    return jsonify({'dev_key': developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform}), 200


# @api.route('/developers/<dev_key>/integrations', methods=['POST'])
# def get_integrations_info(dev_key):
#     developer = Developer.query.filter_by(dev_key=dev_key).first()
#     if developer is None:
#         abort(404)
#     integrations = Integration.query.filter_by(developer_id=developer.id)
#     if integrations is None:
#         abort(404)
#     return jsonify

# 获取 developer 的信息, 通过 dev_key 查询
@api.route('/developers/<dev_key>',methods=['GET'])
def get_developer_info(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    return jsonify({'dev_key':developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform}), 200


# 在dev_key 下创建一个 channel
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


# 删除 dev_key 下的某个 channel
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


# 获得 dev_key 下的所有 channel
@api.route('/developers/<string:dev_key>/channels', methods=['GET'])
def get_channels(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    channels = developer.channels
    if channels is not None:
        list = []
        for channel in channels:
            list.append(channel.channel)
        return jsonify({'channels': list}), 200
    return jsonify({'none': True}), 200

# 获取 dev_key 下的所有自定义集成的信息
@api.route('/developers/<dev_key>/integrations', methods=['GET'])
def get_integrations(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    integration_list = Integration.query.filter_by(developer_id=developer.id).all()
    if integration_list is None:
        abort(404)
    data_json = []
    for integration in integration_list:
        data_json.append({'name': integration.name,
                          'integration_id': integration.integration_id,
                          'description': integration.description,
                          'icon': integration.icon,
                          'channel': integration.channel})
    return jsonify(data_json), 200
