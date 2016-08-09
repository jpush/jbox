from flask import render_template, redirect, request, url_for, flash, session
from flask_login import login_user, logout_user, login_required, current_user
from wtforms import Form
from . import auth
from ..models import Developer
from ..main.forms import FakeUserForm


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


@auth.route('/manage/create_integration', methods=['GET', 'POST'])
@login_required
def create_integration():
    return render_template('auth/create.html')


@auth.route('/new/postTochannels', methods=['GET'])
@login_required
def post_to_channel():
    return render_template('auth/new/postToChannels.html', dev_key=current_user.dev_key)


@auth.route('/new/channel', methods=['GET'])
@login_required
def new_channel():
    return render_template('auth/new/channel.html', dev_key=current_user.dev_key)

@auth.route('/qrcode', methods=['GET'])
@login_required
def qrcode():
    print(current_user.dev_key,"huangmin999")
    return render_template('auth/qrcode.html', dev_key=current_user.dev_key)