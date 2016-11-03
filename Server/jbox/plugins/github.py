import time
from flask import abort, Flask, jsonify, request, session
from . import plugins
from ..models import Developer, Integration, Authorization, db
from ..auth.views import github
import jpush
from jpush import common

baseurl = 'jbox.jiguang.cn:80'


@plugins.route('/github/<integration_id>/webhook', methods=['POST'])
def send_github_msg(integration_id):
    print("print the request json")
    print(request.json)
    
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    if integration is None:
        abort(404)
    developer = Developer.query.filter_by(id=integration.developer_id).first()
    if developer is None:
        abort(404)
    _jpush = jpush.JPush(u'1c29cb5814072b5b1f8ef829', u'600805207f9743a472b79108')
    push = _jpush.create_push()
    _jpush.set_logging("DEBUG")
    push.audience = jpush.audience(
        jpush.tag(developer.dev_key + '_' + integration.channel.channel)
    )

    message = ''
    title = ''
    commits = ''
    comment = ''
    issue = ''
    pull_request = ''
    repository = request.json['repository']
    sender = request.json['sender']
    author = sender['login']
    if 'commits' in request.json:
        commits = request.json['commits']
    if 'comment' in request.json:
        comment = request.json['comment']
    if 'issue' in request.json:
        issue = request.json['issue']
    if 'pull_request' in request.json:
        pull_request = request.json['pull_request']
    if 'action' in request.json:
        action = request.json['action']
    else:
        action = ''

    target_repository = '[' + repository['name'] + ':' + repository['default_branch'] + ']'
    # push event
    if commits != '':
        print('push event')
        length = len(commits)
        if length > 1:
            title = target_repository + str(length) + 'new commits by ' + author + ':'
        else:
            title = target_repository + '1 new commit by ' + author + ':'
        print(commits)
        for i in range(len(commits)):
            commit_id = commits[i]['id'][:7]
            commit_comment = commits[i]['message']
            message = message + commit_id + ': ' + commit_comment + '-' + author + '\n'
        print(message)
    elif issue != '':
        issue_title = issue['title']
        # issue comment event
        if comment != '':
            print('issue comment event')
            title = target_repository + 'New comment by ' + author + ' on issue ' + issue_title
            message = comment['body']
        # issue event(opened, closed)
        else:
            print('issue event')
            title = target_repository + 'Issue ' + action + ' by ' + author
            message = issue['body']
    # commit comment event
    elif comment != '':
        print('commit comment event')
        title = target_repository + 'New commit comment by ' + author
        message = comment['body']
    # pull request event
    elif pull_request != '':
        print('pull request event')
        if action == 'opened':
            title = target_repository + 'Pull request submitted by ' + author
            message = pull_request['body']
        elif action == 'closed':
            merged = pull_request['merged']
            if merged:
                title = target_repository + 'Pull request by ' + author + ' was merged'
            else:
                title = target_repository + 'Pull request by ' + author + ' was closed with unmerged commits'

    print("the title")
    print(title)
    print("the message")
    print(message)
    android_msg = jpush.android(alert=title, extras={'title': title, 'message': message})
    ios_msg = jpush.ios(alert=title, extras={'title': title, 'message': message})
    # ios_msg = jpush.ios(alert=request.json['title'], extras={'title': request.json['title']})
    print(integration.icon)
    if integration.icon is None or len(integration.icon) == 0:
        url = ''
    else:
        url = baseurl + '/static/images/' + integration.icon
    push.notification = jpush.notification(alert=title, android=android_msg, ios=ios_msg)
    push.message = jpush.message(msg_content=message, title=title, content_type="tyope",
                                 extras={'dev_key': developer.dev_key, 'channel': integration.channel.channel,
                                         'datetime': int(time.time()),
                                         'icon': url,
                                         'integation_name': integration.name})

    push.options = {"time_to_live": 864000, "sendno": 12345, "apns_production": False}
    push.platform = jpush.all_

    try:
        response = push.send()
        print(response)
    except common.Unauthorized:
        print("Unauthorized")
        return jsonify({'error': 'Unauthorized request'}), 401
    except common.APIConnectionException:
        print('connect error')
        return jsonify({'error': 'connect error, please try again later'}), 504
    except common.JPushFailure:
        print("JPushFailure")
        response = common.JPushFailure.response
        return jsonify({'error': 'JPush failed, please read the code and refer code document'}), 500
    except:
        print("Exception")
    return jsonify({}), 200


@plugins.route('/github/<string:dev_key>/cancel_authorization', methods=['POST'])
def cancel_github_authorization(dev_key):
    print('cancel github authorization')
    developer = Developer.query.filter_by(dev_key=dev_key).first()
    if developer is None:
        abort(400)
    authorization = Authorization.query.filter_by(developer=developer, type='github').first()
    if authorization:
        try:
            db.session.delete(authorization)
            db.session.commit()
        except:
            db.session.rollback()
            abort(500)
    session.pop('user', None)
    session.pop('github_token', None)
    return jsonify({}), 200


@plugins.route('/github/<string:integration_id>/override', methods=['POST'])
def github_override(integration_id):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    if integration is None:
        abort(400)
    githubs = integration.githubs
    if githubs:
        for entity in githubs:
            url = 'https://api.github.com/repos/' + integration.owner + '/' + entity.repository + '/hooks/' + str(
                entity.hook_id)
            response = github.delete(url, data=None)
            if response.status == 204:
                try:
                    db.session.delete(entity)
                    db.session.commit()
                except:
                    db.session.rollback()
                    abort(500)
    integration.owner = session['user']
    try:
        db.session.add(integration)
        db.session.commit()
        return jsonify({}), 200
    except:
        db.session.rollback()
        abort(500)
