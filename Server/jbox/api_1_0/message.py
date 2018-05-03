import time
from flask import abort, Flask, jsonify, request
from . import api
from ..models import Developer, Integration, Channel
import jpush
from jpush import common
from .developers import baseurl
from ..auth.views import get_developer,get_channel_list


@api.route('/message/<integration_id>/<token>', methods=['POST'])
def send_message(integration_id, token):
    if not request.json or not 'message' in request.json or not 'title' in request.json:
        abort(400)
    integration = Integration.query.filter_by(integration_id=integration_id, token=token).first()
    if integration is None:
        abort(400)

    message_url = ""
    if 'url' in request.json:
       message_url = request.json['url']
    print("the message url "+ message_url)
    # channel dev_ID
    developer = Developer.query.filter_by(id=integration.developer_id).first()
    if developer is None:
        abort(404)
    _jpush = jpush.JPush(u'1c29cb5814072b5b1f8ef829', u'b46af6af73ee8f9480d4edad')
    push = _jpush.create_push()
    _jpush.set_logging("DEBUG")
    push.audience = jpush.audience(
        jpush.tag(developer.dev_key + '_' + integration.channel.channel)
    )
    # push.audience = jpush.all_
    # push.notification = jpush.notification(alert=request.json['title'],extras={'title': request.json['title'],
    #                                                                              'message': request.json['message']})
    android_msg = jpush.android(alert=request.json['title'], extras={'title': request.json['title'],
                                                                     'message': request.json['message']})
    ios_msg = jpush.ios(alert=request.json['title'], extras={'title': request.json['title'],
                                                             'message': request.json['message']})
    # ios_msg = jpush.ios(alert=request.json['title'], extras={'title': request.json['title']})
    print(integration.icon)
    if integration.icon is None or len(integration.icon) == 0:
        url = ''
    else:
        url = baseurl + '/static/images/' + integration.icon
    push.notification = jpush.notification(alert=request.json['title'], android=android_msg, ios=ios_msg)
    push.message = jpush.message(msg_content=request.json['message'], title=request.json['title'], content_type="tyope",
                                 extras={'dev_key': developer.dev_key, 'channel': integration.channel.channel,
                                         'datetime': int(time.time()),
                                         'icon': url,
                                         'url': message_url,
                                         'integration_name': integration.name})

    push.options = {"time_to_live": 864000, "sendno": 12345, "apns_production": True}
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


@api.route('/message/<string:channel>', methods=['POST'])
def send_direct_to_channel(channel):
    developer = get_developer()
    if developer is None:
        return jsonify({}), 404

    channel_to_post = Channel.query.filter_by(developer_id=developer.id,channel=channel).first()
    print(channel_to_post)
    if channel_to_post is None:
        return jsonify({}), 404
    message_url = ""
    if 'url' in request.json:
       message_url = request.json['url']
    _jpush = jpush.JPush(u'1c29cb5814072b5b1f8ef829', u'b46af6af73ee8f9480d4edad')
    push = _jpush.create_push()
    _jpush.set_logging("DEBUG")
    push.audience = jpush.audience(
        jpush.tag(developer.dev_key + '_' + channel)
    )
    android_msg = jpush.android(alert=request.json['title'], extras={'title': request.json['title'],
                                                                     'message': request.json['message']})
    ios_msg = jpush.ios(alert=request.json['title'], extras={'title': request.json['title'],
                                                             'message': request.json['message']})

    push.notification = jpush.notification(alert=request.json['title'], android=android_msg, ios=ios_msg)
    push.message = jpush.message(msg_content=request.json['message'], title=request.json['title'], content_type="tyope",
                                 extras={'dev_key': developer.dev_key, 'channel': channel,
                                         'datetime': int(time.time()),
                                         'icon': "",
                                         'url': message_url,
                                         'integration_name': ""})

    # push.options = {"time_to_live": 864000, "sendno": 12345, "apns_production": False}
    push.options = {"time_to_live": 864000, "sendno": 12345, "apns_production": True}
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
