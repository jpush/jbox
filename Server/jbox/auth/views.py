from flask import render_template, redirect, request, url_for, flash, session
from flask_login import login_user, logout_user, login_required
from wtforms import Form
from . import auth
from ..models import Developer
from ..main.forms import FakeUserForm


@auth.route('/login', methods=['GET', 'POST'])
def login():
    # username = session.get('username')
    # if username is None:
    #     return render_template('auth/login.html')
    form = FakeUserForm()
    if form.validate_on_submit():
        developer = Developer.query.filter_by(id=1).first()
        if developer is not None:
            login_user(developer, remember=True)
            return redirect(url_for('main.index'))
        else:
            flash('Can find user by user_id=1')
    return render_template('auth/login.html', form=form)


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
    return render_template('auth/create_integration.html')
