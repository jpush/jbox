import os, operator
from flask import abort, Flask, json, jsonify, request, make_response, session, redirect, url_for, session
from sqlite3 import DatabaseError
import urllib
from . import api
from ..models import Developer, db, Channel, Integration, GitHub, generate_dev_key, generate_integration_id, generate_auth_token
from .authentication import auth
from ..main.views import qq, update_qq_api_request_data
from ..auth.views import github
from config import basedir

baseurl = 'jbox.jiguang.cn:80'
app_key = os.environ.get("APP_KEY")


# 通过 body 中的 platform, platform_id, username 来创建一个 Developer
@api.route('/developers', methods=['POST'])
def create_developer():
    if not request.json or not 'platform_id' in request.json:
        abort(400)
    developer = Developer.query.filter_by(platform_id=request.json['platform_id'],
                                          platform=request.json['platform']).first()
    if 'desc' in request.json:
        desc = request.json['desc']
    else:
        desc = ''
    if developer is None:
        dev_key = generate_dev_key()
        developer = Developer(dev_key=dev_key,
                              platform=request.json['platform'],
                              platform_id=request.json['platform_id'],
                              username=request.json['dev_name'],
                              email=request.json['email'],
                              description=desc)
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
    if developer.avatar is None or len(developer.avatar) == 0:
        url = ''
    else:
        url = baseurl + '/static/images/' + developer.avatar
    if developer.description is None:
        desc = ''
    else:
        desc = developer.description
    return jsonify({'dev_key': developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform,
                    'avatar': url,
                    'desc': desc}), 200


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
@auth.login_required
def get_developer_info(dev_key):
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        print("can not fint this developer in in devkey" + dev_key)
        return jsonify({'error': 'can not find this developer'})
        # abort(404)
    if developer.avatar is None or len(developer.avatar) == 0:
        url = ''
    else:
        url = baseurl + '/static/images/' + developer.avatar
    if developer.description is None:
        desc = ''
    else:
        desc = developer.description
    return jsonify({'dev_key': developer.dev_key,
                    'dev_name': developer.username,
                    'platform': developer.platform,
                    'avatar': url,
                    'desc': desc}), 200


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
    if 'email' in request.json:
        developer.email = request.json['email']
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
@auth.login_required
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
    # integration_list = Integration.query.filter_by(developer_id=developer.id).all()
    integration_list = developer.integrations
    if integration_list is None:
        abort(404)
    data_json = []
    for integration in integration_list:
        if integration.icon is None or len(integration.icon) == 0:
            url = ''
        else:
            url = baseurl + '/static/images/' + integration.icon
        if integration.description is None:
            desc = ''
        else:
            desc = integration.description
        data_json.append({'name': integration.name,
                          'integration_id': integration.integration_id,
                          'desc': desc,
                          'icon': url,
                          'channel': integration.channel.channel,
                          'token': integration.token})
    return jsonify(data_json), 200


# 获取某个自定义集成的信息
@api.route('/developers/integrations/<integration_id>', methods=['GET'])
@auth.login_required
def get_integration(integration_id):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    if integration is None:
        abort(404)
    developer = Developer.query.filter_by(id=integration.developer_id).first()
    if developer is None:
        abort(404)
    if integration.icon is None or len(integration.icon) == 0:
        url = ''
    else:
        url = baseurl + '/static/images/' + integration.icon
    if integration.description is None:
        desc = ''
    else:
        desc = integration.description
    return jsonify({'name': integration.name,
                    'desc': desc,
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
                token = generate_auth_token()
                if "discourse" in request.json:
                    print("create discourse integration")
                    new_integration = Integration(developer=developer,
                                                  integration_id=new_integration_id,
                                                  channel=channel,
                                                  name='discourse',
                                                  description='',
                                                  icon='',
                                                  token=token,
                                                  type='discourse')
                else:
                    new_integration = Integration(developer=developer,
                                                  integration_id=new_integration_id,
                                                  channel=channel,
                                                  description='',
                                                  icon='',
                                                  token=token)
                db.session.add(new_integration)
                try:
                    db.session.commit()
                    return jsonify({'integration_id': new_integration_id,
                                    'token': token}), 201
                except:
                    db.session.rollback()
                    abort(500)
    new_channel = Channel(developer=developer, channel=request.json['channel'])
    db.session.add(new_channel)
    try:
        new_integration_id = generate_integration_id()
        token = generate_auth_token()
        if "discourse" in request.json:
            print("create discourse integration")
            new_integration = Integration(developer=developer,
                                          integration_id=new_integration_id,
                                          channel=new_channel,
                                          name='discourse',
                                          description='',
                                          icon='',
                                          type='discourse',
                                          token=token)
        else:
            new_integration = Integration(developer=developer,
                                          integration_id=new_integration_id,
                                          channel=new_channel,
                                          description='',
                                          icon='',
                                          token=token)
        db.session.add(new_integration)
        db.session.commit()
        return jsonify({'integration_id': new_integration_id,
                        'token': token}), 201
    except:
        db.session.rollback()
        abort(500)


# PUT 修改 dev_key 下 所绑定的 integration
@api.route('/developers/<dev_key>/<integration_id>', methods=['PUT'])
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


# 保存 github 集成，将所选的仓库与之前的仓库比较，新增则生成 webhook, 否则去掉之前的 webhook
@api.route('/github/<string:integration_id>', methods=['POST'])
def save_github_integration(integration_id):
    if not request.json or not 'repos' in request.json:
        print('request json error')
        abort(400)
    repos = request.json['repos']
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    if integration is None:
        abort(400)
    githubs = integration.githubs
    hook_dict = {}
    if githubs:
        for entity in githubs:
            hook_dict['%s' % entity.repository] = entity.hook_id
    equal = operator.eq(repos, hook_dict.keys())
    if not equal:
        user = session['user']
        data_dict = {"name": "web",
                     "active": True,
                     "events": [
                         "push",
                         "commit_comment",
                         "pull_request",
                         "issues",
                         "issue_comment"
                     ],
                     "config": {
                         "url": "http://jbox.jiguang.cn/plugins/github/" + integration_id + "/webhook",
                         "content_type": "json"
                     }}
        if len(repos) == 0 and len(githubs) == 0:
            pass
        elif len(repos) == 0 and len(githubs) != 0:
            for entity in githubs:
                url = 'https://api.github.com/repos/' + entity.repository + '/hooks/' + str(entity.hook_id)
                response = github.delete(url, data=None)
                if response.status == 204:
                    db.session.delete(entity)
                    db.session.commit()
        elif len(repos) != 0 and len(githubs) == 0:
            for repo in repos:
                response = github.post('https://api.github.com/repos/' + repo + "/hooks", data=data_dict, format='json')
                print(response.data)
                if response.status == 201:
                    new_github = GitHub(integration=integration,
                                        repository=repo,
                                        hook_id=response.data['id'])
                    db.session.add(new_github)
                    db.session.commit()
        else:
            # 得到选中的仓库(repos)与数据库中保存的仓库的差集，然后创建 webhook
            rest1 = list(set(repos).difference(set(hook_dict.keys())))
            if len(rest1) > 0:
                for repository in rest1:
                    response = github.post('https://api.github.com/repos/' + repository + "/hooks",
                                           data=data_dict, format='json')
                    if response.status == 201:
                        new_github = GitHub(integration=integration,
                                            repository=repository,
                                            hook_id=response.data['id'])
                        db.session.add(new_github)
                        db.session.commit()
            # 得到数据库中保存的仓库与选中的仓库(repo)的差集，然后删除 webhook
            rest2 = list(set(hook_dict.keys()).difference(set(repos)))
            if len(rest2) > 0:
                for i in range(len(rest2)):
                    url = 'https://api.github.com/repos/' + rest2[i] + '/hooks/' + str(hook_dict[rest2[i]])
                    response = github.delete(url, data=None)
                    if response.status == 204:
                        old_github = GitHub.query.filter_by(repository=rest2[i], hook_id=hook_dict[rest2[i]]).first()
                        db.session.delete(old_github)
                        db.session.commit()
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
    return jsonify({}), 200


@api.route('/developers/<dev_key>/<integration_id>', methods=['DELETE'])
@auth.login_required
def delete_integration(dev_key, integration_id):
    print('----------------------------delete_integration')
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
                if integration.icon is not None:
                    path = basedir + '/jbox/static/images/' + integration.icon
                    if os.path.exists(path) and os.path.isfile(path):
                        os.remove(path)
                # 如果是 github 集成，删除所有的 webhook
                if integration.type == 'github':
                    githubs = integration.githubs
                    if githubs:
                        user = session['user']
                        for entity in githubs:
                            try:
                                url = 'https://api.github.com/repos/' + user + '/' + entity.repository + '/hooks/' \
                                    + str(entity.hook_id)
                                response = github.delete(url, data=None)
                                if response.status == 204:
                                    print("response status is 204")
                                    db.session.delete(entity)
                                    db.session.commit()
                            except:
                                print("delete webhook failed")
                db.session.delete(integration)
                db.session.commit()
                return jsonify({'deleted': True}), 200
            except DatabaseError:
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
@api.route('/developers/<string:integration_id>/token', methods=['PUT'])
def regenerate_integration_token(integration_id):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    if integration is None:
        abort(400)
    token = generate_auth_token()
    integration.token = token
    try:
        db.session.add(integration)
        db.session.commit()
        return jsonify({'token': token}), 200
    except:
        db.session.rollback()
        abort(500)


def get_developer_from_session():
    if 'qq_token' in session:
        respMe = qq.get('/oauth2.0/me', {'access_token': session['qq_token'][0]})
        openid = json_to_dict(respMe.data)['openid']
        developer = Developer.query.filter_by(platform_id=openid).first()
        return developer
    return None
