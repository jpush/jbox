import os
from flask import abort, Flask, json, jsonify, request, make_response, session, redirect, url_for
from flask_login import login_required
from . import api
from ..models import Developer, db, Channel, Integration, generate_dev_key, generate_integration_id
from .authentication import auth
from ..main.views import qq, update_qq_api_request_data

baseurl = 'jobx.jiguang.cn'


# 通过 body 中的 platform, platform_id, username 来创建一个 Developer
@api.route('/developers', methods=['POST'])
def create_developer():
    if not request.json or not 'platform_id' in request.json:
        abort(400)
    developer = Developer.query.filter_by(platform_id=request.json['platform_id'],
                                          platform=request.json['platform']).first()
    if developer is None:
        dev_key = generate_dev_key()
        developer = Developer(dev_key=dev_key,
                              platform=request.json['platform'],
                              platform_id=request.json['platform_id'],
                              username=request.json['dev_name'])
        developer.insert_to_db()
        return jsonify({'dev_key': developer.dev_key}), 201
    else:
        return jsonify({'created': False}), 304


# 通过 platform 和 platform_id 来获取用户信息
@api.route('/developers/<string:platform>/<string:platform_id>', methods=['GET'])
def get_developer(platform, platform_id):
    developer = Developer.query.filter_by(platform=platform, platform_id=platform_id).first()
    if developer is None:
        abort(404)
    if developer.avatar is None:
        url = baseurl + '/static/images/jiguang-bear.png'
    else:
        url = baseurl + '/static/images/' + developer.avatar
    return jsonify({'dev_key': developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform,
                    'avatar': url,
                    'desc': developer.description}), 200


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
@api.route('/developers/<dev_key>', methods=['GET'])
# @auth.login_required
def get_developer_info(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    if developer.avatar is None:
        url = baseurl + '/static/images/jiguang-bear.png'
    else:
        url = baseurl + '/static/images/' + developer.avatar
    return jsonify({'dev_key': developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform,
                    'avatar': url,
                    'desc': developer.description}), 200


@api.route('/developers/<dev_key>', methods=['PUT'])
def modify_developer(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    if 'name' in request.json:
        developer.username = request.json['name']
    if 'desc' in request.json:
        developer.description = request.json['desc']
    if 'avatar' in request.json:
        developer.avatar = request.json['avatar']
    db.session.add(developer)
    try:
        db.session.commit()
        return jsonify({'modified': True}), 200
    except:
        db.session.rollback()
        abort(500)


# 在dev_key 下创建一个 channel
@api.route('/developers/<string:dev_key>/channels', methods=['POST'])
def create_channel(dev_key):
    if not request.json or not 'channel' in request.json:
        abort(400)
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    channels = developer.channels
    if channels is None:
        create_channel_and_insert2db(developer, request.json['channel'])
    else:
        for channel in channels:
            if channel.channel == request.json['channel']:
                print("existed")
                return jsonify({'created': False, 'existed': True}), 304
        create_channel_and_insert2db(developer, request.json['channel'])
    return jsonify({'created': True, 'existed': False}), 201


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
# @auth.login_required
def get_integrations(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(404)
    # integration_list = Integration.query.filter_by(developer_id=developer.id).all()
    integration_list = developer.integrations
    if integration_list is None:
        abort(404)
    data_json = []
    for integration in integration_list:
        if integration.icon is None:
            url = baseurl + '/static/images/image.png'
        else:
            url = baseurl + '/static/images/' + integration.icon
        data_json.append({'name': integration.name,
                          'integration_id': integration.integration_id,
                          'desc': integration.description,
                          'icon': url,
                          'channel': integration.channel.channel,
                          'token': integration.token})
    return jsonify(data_json), 200


# 获取某个自定义集成的信息
@api.route('/developers/integrations/<integration_id>', methods=['GET'])
def get_integration(integration_id):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    if integration is None:
        abort(404)
    developer = Developer.query.filter_by(id=integration.developer_id).first()
    if developer is None:
        abort(404)
    if integration.icon is None:
        url = baseurl + '/static/images/image.png'
    else:
        url = baseurl + '/static/images/' + integration.icon
    return jsonify({'name': integration.name,
                    'desc': integration.description,
                    'icon': url,
                    'channel': integration.channel.channel}), 200


# 添加一个集成，并返回 integration_id ，如果 channel 已存在，直接绑定该 channel， 否则新建一个 channel
@api.route('/developers/<dev_key>/integrations', methods=['POST'])
def create_integrations(dev_key):
    if not request.json or not 'channel' in request.json:
        print("request json error")
        abort(400)
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        print("developer not found")
        abort(400)
    # channel_list = Channel.query.filter_by(developer_id=developer.id).all()
    channel_list = developer.channels
    if channel_list is not None:
        for channel in channel_list:
            if request.json['channel'] == channel.channel:
                new_integration_id = generate_integration_id()
                new_integration = Integration(developer=developer,
                                              integration_id=new_integration_id,
                                              channel=channel)
                new_integration.insert_to_db()
                token = new_integration.generate_auth_token(3600000000)
                new_integration.token = token.decode('utf-8')
                db.session.add(new_integration)
                try:
                    db.session.commit()
                    return jsonify({'integration_id': new_integration_id,
                                    'token': token.decode('utf-8')}), 201
                except:
                    db.session.rollback()
                    abort(500)
    new_channel = Channel(developer=developer, channel=request.json['channel'])
    db.session.add(new_channel)
    try:
        new_integration_id = generate_integration_id()
        new_integration = Integration(developer=developer,
                                      integration_id=new_integration_id,
                                      channel=new_channel)
        new_integration.insert_to_db()
        token = new_integration.generate_auth_token(3600000000)
        new_integration.token = token.decode('utf-8')
        db.session.add(new_integration)
        db.session.commit()
        return jsonify({'integration_id': new_integration_id,
                        'token': token.decode('utf-8')}), 201
    except:
        db.session.rollback()
        abort(500)


# PUT 修改 dev_key 下 所绑定的 integration
@api.route('/developers/<dev_key>/<integration_id>', methods=['POST', 'PUT'])
# @login_required
def modificate_integration(dev_key, integration_id):
    if not request.json or not 'channel' in request.json:
        abort(400)
    developer = get_developer_with_devkey(dev_key)
    integration = Integration.query.filter_by(developer_id=developer.id, integration_id=integration_id).first()
    if integration is None:
        abort(400)
    integration.channel.channel = request.json['channel']
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
    return jsonify({'modification': True}), 200


@api.route('/developers/<dev_key>/<integration_id>', methods=['DELETE'])
# @login_required
def delete_integration(dev_key, integration_id):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is not None:
        integration = Integration.query.filter_by(integration_id=integration_id).first()
        if integration is not None:
            channel = Channel.query.filter_by(id=integration.channel_id).first()
            integrations = channel.integrations
            # 如果这个 channel 只绑定了要删除的 integration, 删除这个 channel
            if len(integrations) == 1:
                db.session.delete(channel)
            try:
                db.session.delete(integration)
                db.session.commit()
                return jsonify({'deleted': True}), 200
            except:
                db.session.rollback()
                abort(500)
        else:
            abort(404)
    else:
        abort(404)


def create_channel_and_insert2db(developer, channel):
    new_channel = Channel(developer=developer, channel=channel)
    db.session.add(new_channel)
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


# FIX: TOKEN
# 重新生成 integration token   这个接口没有测试
@api.route('/<integration_id>/token', methods=['PUT'])
def regenerate_integration_token(integration_id):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    if integration is None:
        abort(400)
    token = integration.generate_auth_token(3600000000)
    integration.token = token.decode('utf-8')
    try:
        db.session.add(integration)
        db.session.commit()
        return jsonify({'token': token.decode('utf-8')}), 200
    except:
        db.session.rollback()
        abort(500)

