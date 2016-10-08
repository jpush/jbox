import os
import requests
import uuid
from flask import Flask, json, jsonify, render_template, redirect, request, url_for, flash, session
from flask_httpauth import HTTPAuth
from flask_oauthlib.client import OAuth
from . import auth
from config import basedir
from ..models import db, Channel, Developer, Integration, generate_integration_id
from ..main.views import update_qq_api_request_data, qq, json_to_dict

app = Flask(__name__)
app.debug = True
app.secret_key = 'development'
oauth = OAuth(app)

UPLOAD_FOLDER = basedir + '/jbox/static/images/'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])
GITHUB_CLIENT_ID = "c293cc8df7ff97e14237"
GITHUB_CLIENT_SECRET = "b9eb46397fa59c4415a7a741d6f5490896ab710f"

github = oauth.remote_app(
    'github',
    consumer_key=GITHUB_CLIENT_ID,
    consumer_secret=GITHUB_CLIENT_SECRET,
    base_url='https://api.github.com/',
    request_token_url=None,
    access_token_url='https://github.com/login/oauth/access_token',
    authorize_url='https://github.com/login/oauth/authorize'
)


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


@auth.route('/manage', methods=['GET', 'POST'])
def manage():
    developer = get_developer()
    print(developer)
    integrations = developer.integrations
    return render_template('auth/manage.html', **locals())
    # return render_template('index.html')


@auth.route('/manage/create_integration/<string:integration_id>/<string:token>/<string:channel>',
            methods=['GET', 'POST'])
def create_integration(integration_id, token, channel):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    channels = get_channel_list()
    developer = get_developer()
    dev_key = developer.dev_key
    return render_template('auth/create.html', **locals())


@auth.route('/manage/edit_integration/<string:integration_id>', methods=['GET', 'POST'])
def edit_integration(integration_id):
    developer = get_developer()
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    channel = integration.channel.channel
    channels = get_channel_list()
    dev_key = developer.dev_key
    return render_template('auth/create.html', **locals())


@auth.route('/new/post_to_channel', methods=['GET'])
def post_to_channel():
    developer = get_developer()
    dev_key = developer.dev_key
    channels = get_channel_list()
    return render_template('auth/new/post2channel.html', **locals())


@auth.route('/new/channel', methods=['GET'])
def new_channel():
    developer = get_developer()
    dev_key = developer.dev_key
    return render_template('auth/new/channel.html', **locals())


@auth.route('/qrcode', methods=['GET'])
def qrcode():
    developer = get_developer()
    return render_template('auth/qrcode.html', dev_key=developer.dev_key)


@auth.route('/upload/avatar/<dev_key>', methods=['POST'])
def upload_avatar(dev_key):
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            developer = Developer.query.filter_by(dev_key=dev_key).first()
            if developer is not None and developer.avatar is not None:
                path = os.path.join(UPLOAD_FOLDER, developer.avatar)
                if os.path.exists(path) and os.path.isfile(path):
                    os.remove(path)
                file_type = file.filename.rsplit('.', 1)[1]
                filename = generate_file_name(developer.dev_key, file_type)
                file.save(os.path.join(UPLOAD_FOLDER, filename))
                return jsonify(name=filename)


@auth.route('/upload/<integration_id>', methods=['POST'])
def upload_icon(integration_id):
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            integration = Integration.query.filter_by(integration_id=integration_id).first()
            if integration is not None and integration.icon is not None:
                path = os.path.join(UPLOAD_FOLDER, integration.icon)
                if os.path.exists(path) and os.path.isfile(path):
                    os.remove(path)
                file_type = file.filename.rsplit('.', 1)[1]
                filename = generate_file_name(integration.integration_id, file_type)
                file.save(os.path.join(UPLOAD_FOLDER, filename))
                return jsonify(name=filename)


def get_channel_list():
    channel_list = []
    developer = get_developer()
    if developer is not None:
        channels = developer.channels
        for channel in channels:
            channel_list.append(channel.channel)
        return channel_list


def get_developer():
    if 'qq_token' in session:
        respMe = qq.get('/oauth2.0/me', {'access_token': session['qq_token'][0]})
        openid = json_to_dict(respMe.data)['openid']
        developer = Developer.query.filter_by(platform_id=openid).first()
        return developer
    return None


@auth.route('/profile', methods=['GET'])
def profile():
    developer = get_developer()
    return render_template('auth/profile.html', developer=developer)


@auth.route('/setting', methods=['GET', 'POST'])
def setting():
    developer = get_developer()
    return render_template('auth/setting.html', developer=developer)


def generate_file_name(id, file_type):
    return uuid.uuid3(uuid.NAMESPACE_DNS, id).__str__() + '.' + file_type


@auth.route('/github/authorize')
def authorize_github():
    return github.authorize(callback=url_for('auth.new_github_integration', _external=True))


@auth.route('/github/integrations', methods=['GET', 'POST'])
def new_github_integration():
    resp = github.authorized_response()
    if resp is None:
        return 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
    session['github_token'] = (resp['access_token'], '')
    token = session['github_token'][0]
    me = github.get('user')
    print(jsonify(me.data))
    new_integration_id = generate_integration_id()
    developer = get_developer()
    channel = 'github'
    github_channel = Channel.query.filter_by(developer=developer, channel=channel).first()
    if github_channel is None:
        github_channel = Channel(developer=developer, channel=channel)
        db.session.add(github_channel)
        db.session.commit()
    integration = Integration(developer=developer,
                              integration_id=new_integration_id,
                              channel=github_channel,
                              description='',
                              icon='',
                              token=token)
    db.session.add(integration)
    db.session.commit()
    # POST create webhook
    # respMe = github.get('https://api.github.com/user', {'access_token': session['github_token'][0]})
    data_dict = {"name": "web",
            "active": True,
            "events": [
                "push",
                "pull_request"
            ],
            "config": {
                "url": "http://jbox.jiguang.cn/plugins/github/webhook",
                "content_type": "json"
            }}
    print(token)
    # response = github.post('https://api.github.com/repos/KenChoi1992/jchat-android/hooks', data=data_dict,
    #                        headers=None, format='json')
    # print(response.data)
    import requests

    url = "https://api.github.com/repos/KenChoi1992/jchat-android/hooks"

    payload = "{\r\n  \"name\": \"web\",\r\n  \"active\": true,\r\n  \"events\": [\r\n    \"push\",\r\n    \"pull_request\"\r\n  ],\r\n  \"config\": {\r\n    \"url\": \"http://jbox.jiguang.cn/plugins/github/webhook\",\r\n    \"content_type\": \"json\"\r\n  }\r\n}"
    headers = {
        'Authorization': "Basic S2VuQ2hvaTE5OTI6Y3lnMTk5Mg==",
        'Content-Type': "application/json",
        'Accept': "application/vnd.github.damage-preview"
    }

    response = requests.request("POST", url, data=payload, headers=headers)

    print(response.text)
    # email = respMe.data['email']
    # print("email:" + email)
    return redirect(url_for('auth.edit_integration', integration_id=new_integration_id))


@github.tokengetter
def get_github_oauth_token():
    return session.get('github_token')
