from flask_wtf import Form
from flask_login import current_user
from wtforms import FileField, StringField, SubmitField
from wtforms.ext.sqlalchemy.fields import QuerySelectField
from wtforms.validators import DataRequired
from ..models import Developer, Channel


class IntegrationForm(Form):
    def query_factory():
        return [channel.channel for channel in Developer.query.filter_by(
            dev_key=current_user.dev_key).first().channels]

    integration_name = StringField(validators=[DataRequired()])
    description = StringField()
    channel = QuerySelectField(label=u'channel', query_factory=query_factory, get_pk=lambda x: x)
    input = StringField()
    icon = FileField()
    submit = SubmitField('submit')
