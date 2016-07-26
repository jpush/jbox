from datetime import datetime
from flask import render_template, session, redirect, url_for

from . import main
from .forms import UserForm
from .. import db
from ..models import Developer, Integration


@main.route('/', methods=['GET', 'POST'])
def index():
    name = session.get('username')
    if name is None:
        return render_template('index.html')
    else:
        return render_template('index_logged_in.html', username=name)


@main.route('/document', methods=['GET'])
def document():
    return render_template('document.html')


@main.route('/guide', methods=['GET'])
def guide():
    return render_template('guide.html')
