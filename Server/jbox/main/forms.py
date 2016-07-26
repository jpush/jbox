from flask_wtf import Form
from wtforms import StringField, SubmitField
from wtforms.validators import DataRequired


class UserForm(Form):
    dev_key = StringField('Input your dev_key', validators=[DataRequired()])
    platform = StringField('Input platform name', validators=[DataRequired()])
    username = StringField('Input your username', validators=[DataRequired()])
    channel = StringField('Input channel name')
    submit = SubmitField('Submit')


class FakeUserForm(Form):
    username = StringField('input username')
    password = StringField('input password')
    submit = SubmitField('submit')
