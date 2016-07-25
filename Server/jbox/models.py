# -*- coding: utf-8 -*-
from jbox import db


class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    dev_key = db.Column(db.String(40), unique=True)
    platform = db.Column(db.String(50))
    username = db.Column(db.String(150), index=True)
    channel = db.Column(db.String(150))
    apps = db.relationship('App', backref='user')

    def __repr__(self):
        return '<User %r>' % self.dev_key


class App(db.Model):
    __tablename__ = 'apps'
    id = db.Column(db.Integer, primary_key=True)
    integration_id = db.Column(db.String(40), unique=True)
    name = db.Column(db.String(100))
    description = db.Column(db.String(150))
    icon = db.Column(db.String(150))
    channel = db.Column(db.String(150))
    user_id = db.Column(db.Integer, db.ForeignKey('users.id'))

    def __repr__(self):
        return '<App %r>' % self.integration_id

#
# @jbox.teardown_request
# def shutdown_session(exception=None):
#     db_session.remove()

