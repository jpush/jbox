import sys, os
import requests
from flask import json, render_template, redirect, request, url_for, flash, session, send_from_directory
from flask_login import login_user, logout_user, login_required, current_user
from wtforms import Form
from . import auth
from ..models import Developer
from .forms import IntegrationForm
from ..main.forms import FakeUserForm
from ..api_1_0.developers import get_channels, modificate_integration

UPLOAD_FOLDER = '/users/admin/Desktop/temp/'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


@auth.route('/logout')
@login_required
def logout():
    logout_user()
    flash('You have been logged out.')
    return redirect(url_for('main.index'))


@auth.route('/manage', methods=['GET'])
@login_required
def manage():
    return render_template('auth/manage.html')


@auth.route('/manage/create_integration/<string:integration_id>/<string:token>/<string:channel>', methods=['GET', 'POST'])
@login_required
def create_integration(integration_id, token, channel):
    form = IntegrationForm()
    channels = get_channel_list()
    dev_key = current_user.dev_key
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
    print(current_user.dev_key, "huangmin999")
    return render_template('auth/qrcode.html', dev_key=current_user.dev_key)


@auth.route('/edit_integration/<string:integration_id>', methods=['POST'])
@login_required
def edit_integration(integration_id):
    form = IntegrationForm()
    if form.validate_on_submit():
        file = form.icon.data
        if file and allowed_file(file.filename):
            filename = file.filename
            file.save(os.path.join(UPLOAD_FOLDER), filename)
            name = form.integration_name.data
            description = form.description.data
            channel = form.input.data
            print("name: " + name + " description: " + description + " channel: " + channel)
            r = modificate_integration(current_user.dev_key, integration_id)
            print(dir(r))
            return redirect(url_for('/manage'))
        else:
            flash('file is not null or not allowed')
    return post_to_channel()


def get_channel_list():
    channel_list = []
    developer = Developer.query.filter_by(dev_key=current_user.dev_key).first()
    if developer is not None:
        channels = developer.channels
        for channel in channels:
            channel_list.append(channel.channel)
        return channel_list
