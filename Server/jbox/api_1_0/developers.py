from flask import abort, Flask, json, jsonify, request, make_response
from . import api
from ..models import Developer, db, Channel, Integration, generate_dev_key, generate_integration_id


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

# 添加一个集成，并返回 integration_id ，如果 channel 已存在，直接绑定该 channel， 否则新建一个 channel
@api.route('/developers/<dev_key>/integrations', methods=['POST'])
def create_integrations(dev_key):
    if not request.json or not 'channel' in request.json:
        abort(400)
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(400)
    channel_list = Channel.query.filter_by(developer_id=developer.id).all()
    is_include_channel = False
    for channel in channel_list:
        if request.json['channel'] == channel.channel:
            is_include_channel = True
    if is_include_channel:
        new_integration_id = generate_integration_id()
        new_integration = Integration(developer=developer,
                                      integration_id=new_integration_id,
                                      developer_id=developer.id,
                                      name=request.json['name'],
                                      description=request.json['description'],
                                      icon=request.json['icon'],
                                      channel=request.json['channel'])
        db.session.add(new_integration)
        try:
            db.session.commit()
        except:
            db.session.rollback()
            abort(500)
        # TODO: add token
        return jsonify({'integration_id': new_integration_id})
    else:
        new_channel = Channel(developer=developer, channel=request.json['channel'])
        db.session.add(new_channel)
        try:
            db.session.commit()
        except:
            db.session.rollback()
            abort(500)
            new_integration_id = generate_integration_id()
            new_integration = Integration(developer=developer,
                                          integration_id=new_integration_id,
                                          developer_id=developer.id,
                                          name=request.json['name'],
                                          description=request.json['description'],
                                          icon=request.json['icon'],
                                          channel=request.json['channel'])
            db.session.add(new_integration)
            try:
                db.session.commit()
            except:
                db.session.rollback()
                abort(500)
            # TODO: add token
            return jsonify({'integration_id': new_integration_id})

# PUT 修改 dev_key 下 所绑定的 integration
@api.route('/developers/<dev_key>/<integration_id>', methods=['PUT'])
def modificate_integration(dev_key, integration_id):
    if not request.json or not 'channel' in request.json:
        abort(400)
    developer = get_developer_with_devkey(dev_key)
    integration = Integration.query.filter_by(developer_id=developer.id,integration_id=integration_id).frist()
    if integration is None:
        abort(400)
    integration.channel = request.json['channel']
    if 'name' in request.json:
        integration.name = request.json['name']
    if 'description' in request.json:
        integration.description = request.json['description']
    if 'icon' in request.json:
        integration.icon = request.json['icon']
    db.session.add(integration)
    try:
        db.session.commit()
    except:
        db.session.rollback()
        abort(500)


def get_developer_with_devkey(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(400)
    return developer