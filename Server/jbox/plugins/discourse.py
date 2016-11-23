import time
from flask import abort, Flask, jsonify, request
from . import plugins
from ..models import Developer, Integration
import jpush
from jpush import common
from .github import baseurl


@plugins.route('/discourse/<string:integration_id>/<string:token>/webhook', methods=['POST'])
def send_discourse_msg(integration_id, token):
    print("discourse")
    integration = Integration.query.filter_by(integration_id=integration_id, token=token).first()
    if integration is None:
        abort(400)

    # channel dev_ID
    developer = Developer.query.filter_by(id=integration.developer_id).first()
    if developer is None:
        abort(404)
    _jpush = jpush.JPush(u'1c29cb5814072b5b1f8ef829', u'600805207f9743a472b79108')
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
                                         'url': request.json['title'],
                                         'integation_name': integration.name})

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
