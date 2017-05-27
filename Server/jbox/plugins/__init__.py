from flask import Blueprint

plugins = Blueprint('plugins', __name__)

from . import github, discourse