from datetime import datetime
from flask import render_template, session, redirect, url_for, flash
from flask_login import login_user
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
            developer = Developer.query.filter_by(id=1).first()
            if developer is not None:
                login_user(developer, remember=True)
                return redirect(url_for('main.index'))
            else:
                flash('Can find user by user_id=1')
    return render_template('login.html', form=form)


@main.route('/document', methods=['GET'])
def document():
    return render_template('document.html')


@main.route('/guide', methods=['GET'])
def guide():
    return render_template('guide.html')

@main.route('/qrcode', methods=['GET'])
def qrcode():
    return render_template('qrcode.html')