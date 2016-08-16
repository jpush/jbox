import sys, os
import requests
from flask import json, jsonify, render_template, redirect, request, url_for, flash, session, send_from_directory
from flask_login import login_user, logout_user, login_required, current_user
from wtforms import Form
from . import auth
from config import basedir
from ..models import Developer, Integration
from ..main.forms import FakeUserForm
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
@login_required
def manage():
    integrations = Developer.query.filter_by(dev_key=current_user.dev_key).first().integrations
    return render_template('auth/manage.html', integrations=integrations, dev_key=current_user.dev_key)


@auth.route('/manage/create_integration/<string:integration_id>/<string:token>/<string:channel>', methods=['GET', 'POST'])
@login_required
def create_integration(integration_id, token, channel):
    print("create_integration")
    channels = get_channel_list()
    dev_key = current_user.dev_key
    return render_template('auth/create.html', **locals())


@auth.route('/manage/edit_integration/<string:integration_id>', methods=['GET', 'POST'])
@login_required
def edit_integration(integration_id):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    name = integration.name
    description = integration.description
    channel = integration.channel
    icon = integration.icon
    channels = get_channel_list()
    return render_template('auth/create.html', **locals())


@auth.route('/new/postTochannels', methods=['GET'])
@login_required
def post_to_channel():
    dev_key = current_user.dev_key
    return render_template('auth/new/postToChannels.html', dev_key=dev_key, channels=get_channel_list())


@auth.route('/new/channel', methods=['GET'])
@login_required
def new_channel():
    return render_template('auth/new/channel.html', dev_key=current_user.dev_key)


@auth.route('/qrcode', methods=['GET'])
@login_required
def qrcode():
    return render_template('auth/qrcode.html', dev_key=current_user.dev_key)


@auth.route('/uploadajax', methods=['POST'])
def upldfile():
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            filename = file.filename
            file.save(os.path.join(UPLOAD_FOLDER, filename))
            file_size = os.path.getsize(os.path.join(UPLOAD_FOLDER, filename))
            return jsonify(name=filename, size=file_size)


def get_channel_list():
    channel_list = []
    developer = Developer.query.filter_by(dev_key=current_user.dev_key).first()
    if developer is not None:
        channels = developer.channels
        for channel in channels:
            channel_list.append(channel.channel)
        return channel_list
