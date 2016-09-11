import sys, os
import requests
from flask import json, jsonify, render_template, redirect, request, url_for, flash, session, send_from_directory
from flask_login import login_user, logout_user, login_required, current_user
from wtforms import Form
from . import auth
from config import basedir
from ..models import Developer, Integration
from ..main.forms import FakeUserForm
from ..main.views import qq, update_qq_api_request_data
from ..api_1_0.developers import get_channels, modificate_integration

UPLOAD_FOLDER = basedir + '/jbox/static/images/'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


@auth.route('/logout')
@login_required
def logout():
    logout_user()
    flash('You have been logged out.')
    return render_template('index.html')


@auth.route('/manage', methods=['GET', 'POST'])
def manage():
    developer = get_developer()
    print(developer)
    integrations = developer.integrations
    return render_template('auth/manage.html', integrations=integrations, dev_key=developer.dev_key)
    # return render_template('index.html')


@auth.route('/manage/create_integration/<string:integration_id>/<string:token>/<string:channel>', methods=['GET', 'POST'])
def create_integration(integration_id, token, channel):
    channels = get_channel_list()
    developer = get_developer()
    dev_key = developer.dev_key
    return render_template('auth/create.html', **locals())


@auth.route('/manage/edit_integration/<string:integration_id>', methods=['GET', 'POST'])
def edit_integration(integration_id):
    developer = get_developer()
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    name = integration.name
    description = integration.description
    channel = integration.channel.channel
    icon = integration.icon
    token = integration.token
    channels = get_channel_list()
    dev_key = developer.dev_key
    return render_template('auth/create.html', **locals())


@auth.route('/new/post_to_channel', methods=['GET'])
def post_to_channel():
    developer = get_developer()
    dev_key = developer.dev_key
    return render_template('auth/new/post2channel.html', dev_key=dev_key, channels=get_channel_list())


@auth.route('/new/channel', methods=['GET'])
def new_channel():
    developer = get_developer()
    return render_template('auth/new/channel.html', dev_key=developer.dev_key)


@auth.route('/qrcode', methods=['GET'])
def qrcode():
    developer = get_developer()
    return render_template('auth/qrcode.html', dev_key=developer.dev_key)


@auth.route('/uploadajax', methods=['POST'])
def uploadfile():
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            filename = file.filename
            file.save(os.path.join(UPLOAD_FOLDER, filename))
            file_size = os.path.getsize(os.path.join(UPLOAD_FOLDER, filename))
            return jsonify(name=filename, size=file_size)


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
        print(session)
        data = update_qq_api_request_data()
        print('')
        print()
        resp = qq.get('/user/get_user_info', data=data)

    developer = Developer.query.filter_by(platform_id=data['openid']).first()
    if developer is None:
        return redirect(url_for('main.login'))
    return developer

@auth.route('/profile', methods=['GET'])
def profile():
    developer = get_developer()
    return render_template('auth/profile.html', developer=developer)


@auth.route('/setting', methods=['GET', 'POST'])
def setting():
    developer =get_developer()
    return render_template('auth/setting.html', developer=developer)
