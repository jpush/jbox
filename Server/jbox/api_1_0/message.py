from flask import abort, Flask, jsonify, request
from . import api
from ..models import Developer, Integration
import jpush
from jpush import common

@api.route('/message/<integration_id>/<token>', methods=['POST'])
def send_message(integration_id, token):
    print("haungmin1234")
    if not request.json or not 'message' in request.json or not 'title' in request.json:
        abort(400)
    integration = Integration.verify_auth_token(token)
    if integration is None:
        abort(404)
    # channel dev_ID
    developer = Developer.query.filter_by(id=integration.developer_id).first()
    print(type(developer), "huangmin777")
    if developer is None:
        abort(404)

    _jpush = jpush.JPush(u'abcacdf406411fa656ee11c3', u'682acd395df807d97e24eb50')
    push = _jpush.create_push()
    _jpush.set_logging("DEBUG")
    push.audience = jpush.audience(
        jpush.tag_and(developer.dev_key, integration.channel)
    )

    # push.notification = jpush.notification(alert=request.json['title'],extras={'title': request.json['title'],
    #                                                                              'message': request.json['message']})
    android_msg = jpush.android(alert=request.json['title'])
    ios_msg = jpush.ios(alert=request.json['title'], extras={'title': request.json['title'],
                                                             'message': request.json['message']})
    
    push.notification = jpush.notification(alert=request.json['title'], android=android_msg, ios=ios_msg)
    push.options = {"time_to_live": 86400, "sendno": 12345, "apns_production": False}
    push.platform = jpush.all_
    try:
        response = push.send()
        print(response, "haungmin8919")
    except common.Unauthorized:
        raise common.Unauthorized("Unauthorized")
    except common.APIConnectionException:
        raise common.APIConnectionException("conn error")
    except common.JPushFailure:
        print("JPushFailure")
    except:
        print("Exception")
    return jsonify({}), 200