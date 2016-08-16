from datetime import datetime
from flask import render_template, session, redirect, url_for, flash
from flask_login import login_user, current_user
from . import main
from .forms import UserForm, FakeUserForm
from .. import db
from ..models import Developer, Integration


@main.route('/', methods=['GET', 'POST'])
def index():
    return render_template('index.html')


@main.route('/login', methods=['GET', 'POST'])
def login():
    # username = session.get('username')
    # if username is None:
    #     return render_template('auth/login.html')
    form = FakeUserForm()
    if form is None:
        flash('Should input username and password')
    else:
        if form.validate_on_submit():
            developer = Developer.query.filter_by(platform=form.platform.data, platform_id=form.platform_id.data).first()
            if developer is not None:
                login_user(developer, remember=True)
                integrations = Developer.query.filter_by(dev_key=current_user.dev_key).first().integrations
                return render_template('auth/manage.html', integrations=integrations, dev_key=current_user.dev_key)
            else:
                flash('Can find user by user_id=1')
    return render_template('login.html', form=form)


@main.route('/document', methods=['GET'])
def document():
    return render_template('document.html')


@main.route('/guide', methods=['GET'])
def guide():
    return render_template('guide.html')

@main.route('/application', methods=['GET'])
def application():
    return render_template('application.html')