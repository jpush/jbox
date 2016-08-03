from flask import Blueprint

api = Blueprint('api', __name__)

from . import authentication, developers, integrations, message, errors
