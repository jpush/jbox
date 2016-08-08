from flask import render_template, redirect, request, url_for, flash, session
from flask_login import login_user, logout_user, login_required
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
def manage():
    return render_template('auth/manage.html')


@auth.route('/manage/create_integration', methods=['GET', 'POST'])
def create_integration():
    return render_template('auth/create.html')

@auth.route('new/postTochannels', methods = ['GET'])
def post_to_channel():
    return render_template('auth/new/postToChannels.html')

@auth.route('new/channel', methods= ['GET'])
def new_channel():
    return render_template('auth/new/channel.html')
