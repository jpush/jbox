import os
import requests
import uuid
from flask import abort, Flask, json, jsonify, render_template, redirect, request, url_for, flash, session
from flask_httpauth import HTTPAuth
from flask_oauthlib.client import OAuth
from . import auth
from config import basedir
from ..models import db, Channel, Developer, Integration, Authorization, generate_integration_id, generate_auth_token
from ..main.views import update_qq_api_request_data, qq, json_to_dict

app = Flask(__name__)
app.debug = True
app.secret_key = 'development'
oauth = OAuth(app)

UPLOAD_FOLDER = basedir + '/jbox/static/user/images/'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])
GITHUB_CLIENT_ID = os.environ.get("GITHUB_CLIENT_ID")
GITHUB_CLIENT_SECRET = os.environ.get("GITHUB_CLIENT_SECRET")

github = oauth.remote_app(
    'github',
    consumer_key=GITHUB_CLIENT_ID,
    consumer_secret=GITHUB_CLIENT_SECRET,
    base_url='https://api.github.com/',
    request_token_url=None,
    access_token_url='https://github.com/login/oauth/access_token',
    request_token_params={'scope': 'admin:repo_hook,admin:org_hook,repo'},
    authorize_url='https://github.com/login/oauth/authorize'
)


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1] in ALLOWED_EXTENSIONS


@auth.route('/manage', methods=['GET', 'POST'])
def manage():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    username = developer.username
    if username is None or username == '':
        return redirect(url_for('auth.setting'))
    integrations = Integration.query.filter_by(developer=developer, type='custom').all()
    github_integrations = Integration.query.filter_by(developer=developer, type='github').all()
    discourse_integrations = Integration.query.filter_by(developer=developer, type='discourse').all()
    return render_template('auth/manage.html', **locals())


@auth.route('/add_third_party', methods=['GET'])
def add_third_party():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    return render_template('auth/third_party_integration.html', developer=developer)


@auth.route('/github_integration', methods=['GET'])
def github_integration():
    try:
        developer = get_developer()
        if developer is None:
            return redirect(url_for('main.login'))
        dev_key = developer.dev_key
        if 'user' in session:
            user = session['user']
        else:
            authorization = Authorization.query.filter_by(developer=developer, type='github').first()
            if authorization:
                response = github.get("user", {'access_token': authorization.oauth_token})
                user = response.data['login']
                session['user'] = user
                session['github_token'] = (authorization.oauth_token, '')
        integrations = developer.integrations
        github_integrations = []
        if integrations:
            for integration in integrations:
                if integration.type == 'github':
                    githubs = integration.githubs
                    if githubs:
                        repo_list = []
                        for entity in githubs:
                            display = entity.repository
                            index = display.index('/')
                            if index > 0:
                                repo_list.append(display[index + 1:len(display)])
                        new_github = GitHub(integration_id=integration.integration_id, name=integration.name, icon=integration.icon,
                                            channel=integration.channel.channel, repositories=repo_list)
                        github_integrations.append(new_github)
                    else:
                        github_integrations.append(integration)
        return render_template('auth/github_integration.html', **locals())
    except Exception:
        # 重新授权
        return github.authorize(callback=url_for('auth.github_re_authorize', _external=True))


@auth.route('/github/re-authorize', methods=['GET'])
def github_re_authorize():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    dev_key = developer.dev_key
    resp = github.authorized_response()
    if resp is None:
        return 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
    session['github_token'] = (resp['access_token'], '')
    token = session['github_token'][0]
    authorization = Authorization.query.filter_by(developer_id=developer.id, type='github').first()
    print("huangmin1")
    if authorization is None:
        print("huangmin2")
        authorization = Authorization(developer=developer, oauth_token=token, type='github')
        db.session.add(authorization)
        db.session.commit()
    else:
        try:
            print("huangmin3")
            print(token)
            authorization.oauth_token = token
            db.session.add(authorization)
            db.session.commit()
        except:
            print("huangmin4")
            db.session.rollback()
            abort(500)
    if 'user' not in session:
        print("huangmin5")
        me = github.get('user')
        user = me.data['login']
        session['user'] = user
    integrations = developer.integrations
    user = session['user']
    github_integrations = []
    if integrations:
        for integration in integrations:
            if integration.type == 'github':
                githubs = integration.githubs
                if githubs:
                    repo_list = []
                    for entity in githubs:
                        display = entity.repository
                        if '/' in display:
                            index = display.index('/')
                            if index > 0:
                                repo_list.append(display[index + 1:len(display)])
                    new_github = GitHub(integration_id=integration.integration_id, name=integration.name, icon=integration.icon,
                                        channel=integration.channel.channel, repositories=repo_list)
                    github_integrations.append(new_github)
                    print("huangmin6")
                else:
                    print("huangmin7")
                    github_integrations.append(integration)
    return render_template('auth/github_integration.html', **locals())


class GitHub(object):
    def __init__(self, integration_id, name, icon, channel, repositories):
        self.integration_id = integration_id
        self.name = name
        self.icon = icon
        self.channel = channel
        self.repositories = repositories


@auth.route('/discourse_integration', methods=['GET'])
def discourse_integration():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    integrations = developer.integrations
    discourse_integrations = []
    if integrations:
        for integration in integrations:
            if integration.type == 'discourse':
                discourse_integrations.append(integration)
    return render_template('auth/discourse_integration.html', **locals())


@auth.route('/manage/create_integration/<string:integration_id>/<string:token>/<string:channel>',
            methods=['GET', 'POST'])
def create_integration(integration_id, token, channel):
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    channels = get_channel_list()
    dev_key = developer.dev_key
    return render_template('auth/create.html', **locals())


@auth.route('/manage/edit_integration/<string:integration_id>', methods=['GET', 'POST'])
def edit_integration(integration_id):
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    channel = integration.channel.channel
    channels = get_channel_list()
    dev_key = developer.dev_key
    return render_template('auth/create.html', **locals())


@auth.route('/manage/edit_github_integration/<string:integration_id>', methods=['GET', 'POST'])
def edit_github_integration(integration_id):
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    integration = Integration.query.filter_by(integration_id=integration_id).first()
    githubs = integration.githubs
    if 'user' not in session:
        flash("尚未进行 GitHub 授权，编辑失败，请点击 GitHub 授权按钮进行授权")
        return redirect(url_for('auth.github_integration'))
    else:
        user = session['user']
        owner = integration.owner
        if user == owner:
            confirmed = True
        else:
            confirmed = False
        store_repos = []
        if githubs:
            for entity in githubs:
                display = entity.repository
                if '/' in display:
                    index = display.index('/')
                    if index > 0:
                        store_repos.append(display[index + 1:len(display)])
        length = len(store_repos)
        channel = integration.channel.channel
        channels = get_channel_list()
        dev_key = developer.dev_key
        response = github.get('https://api.github.com/user/repos?per_page=400')
        list = response.data
        result_key = {}
        result = {}
        if len(list) > 0:
            for i in range(len(list)):
                repo_owner = list[i]['owner']
                if repo_owner['login'] in result:
                    result[repo_owner['login']].append(list[i]['name'])
                else:
                    result_key[repo_owner['login']] = list[i]["permissions"]["admin"]
                    result[repo_owner['login']] = [list[i]['name']]
        return render_template('auth/create.html', developer=developer, dev_key=dev_key, channel=channel,
                               channels=channels, length=length, confirmed=confirmed,result=result, user=user,
                               owner=owner,store_repos=store_repos,result_key=result_key, integration=integration)


@auth.route('/new/post_to_channel', methods=['GET'])
def post_to_channel():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    dev_key = developer.dev_key
    channels = get_channel_list()
    return render_template('auth/new/post2channel.html', **locals())


@auth.route('/new/github/post_to_channel', methods=['GET'])
def post_to_channel_github():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    dev_key = developer.dev_key
    channels = get_channel_list()
    github = True
    return render_template('auth/new/post2channel.html', **locals())


@auth.route('/new/discourse/post_to_channel', methods=['GET'])
def post_to_channel_discourse():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    channels = get_channel_list()
    dev_key = developer.dev_key
    discourse = True
    return render_template('auth/new/post2channel.html', **locals())


@auth.route('/new/channel', methods=['GET'])
def new_channel():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    dev_key = developer.dev_key
    return render_template('auth/new/channel.html', **locals())


@auth.route('/upload/avatar/<dev_key>', methods=['POST'])
def upload_avatar(dev_key):
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            developer = Developer.query.filter_by(dev_key=dev_key).first()
            if developer is not None and developer.avatar is not None:
                path = os.path.join(UPLOAD_FOLDER, developer.avatar)
                if os.path.exists(path) and os.path.isfile(path):
                    os.remove(path)
                file_type = file.filename.rsplit('.', 1)[1]
                filename = generate_file_name(file_type)
                file.save(os.path.join(UPLOAD_FOLDER, filename))
                developer.avatar = filename
                db.session.add(developer)
                db.session.commit()
                return jsonify(name=filename)


@auth.route('/upload/<integration_id>', methods=['POST'])
def upload_icon(integration_id):
    if request.method == 'POST':
        file = request.files['file']
        if file and allowed_file(file.filename):
            integration = Integration.query.filter_by(integration_id=integration_id).first()
            if integration is not None and integration.icon is not None:
                path = os.path.join(UPLOAD_FOLDER, integration.icon)
                if os.path.exists(path) and os.path.isfile(path):
                    os.remove(path)
                file_type = file.filename.rsplit('.', 1)[1]
                filename = generate_file_name(file_type)
                file.save(os.path.join(UPLOAD_FOLDER, filename))
                integration.icon = filename
                db.session.add(integration)
                db.session.commit()
                return jsonify(name=filename)


def get_channel_list():
    channel_list = []
    developer = get_developer()
    if developer is not None:
        channels = developer.channels
        for channel in channels:
            channel_list.append(channel.channel)
        return channel_list


def get_developer():
    if 'qq_token' in session:
        respMe = qq.get('/oauth2.0/me', {'access_token': session['qq_token'][0]})
        openid = json_to_dict(respMe.data)['openid']
        developer = Developer.query.filter_by(platform_id=openid).first()
        return developer
    return None


@auth.route('/profile', methods=['GET'])
def profile():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    username = developer.username
    if username is None or username == '':
        return redirect(url_for('auth.setting'))
    return render_template('auth/profile.html', developer=developer)


@auth.route('/setting', methods=['GET', 'POST'])
def setting():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    username = developer.username
    if username is None or username == '':
        first_login = True
    else:
        first_login = False
    return render_template('auth/setting.html', **locals())


def generate_file_name(file_type):
    return uuid.uuid1().__str__() + '.' + file_type


@auth.route('/github/create/<string:channel>', methods=['GET'])
def create_github_integration(channel):
    new_integration_id = generate_integration_id()
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    github_channel = Channel.query.filter_by(developer=developer, channel=channel).first()
    if github_channel is None:
        github_channel = Channel(developer=developer, channel=channel)
        db.session.add(github_channel)
        db.session.commit()
    integration = Integration(developer=developer,
                              integration_id=new_integration_id,
                              channel=github_channel,
                              name='github',
                              description='',
                              icon='',
                              type='github',
                              token=generate_auth_token(),
                              owner=session['user'])
    db.session.add(integration)
    db.session.commit()
    return redirect(url_for('auth.edit_github_integration', integration_id=new_integration_id))


@auth.route('/send_message', methods=['GET'])
def send_message():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    channels = get_channel_list()
    return render_template('auth/send_to_channel.html', **locals())


@auth.route('/send_to_channel/<string:channel>', methods=['GET'])
def send_to_channel(channel):
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    channels = get_channel_list()
    return render_template('auth/send_to_channel.html', **locals())


@github.tokengetter
def get_github_oauth_token():
    if 'github_token' in session:
        return session.get('github_token')
    else:
        developer = get_developer()
        if developer is None:
            return redirect(url_for('main.login'))
        authorization = Authorization.query.filter_by(developer_id=developer.id, type='github').first()
        if authorization is None:
            return None
        else:
            return (authorization.oauth_token, '')


@auth.route('/github/authorize')
def github_authorize():
    return github.authorize(callback=url_for('auth.github_authorize_callback', _external=True))


@auth.route('/github/authorize/callback')
def github_authorize_callback():
    developer = get_developer()
    if developer is None:
        return redirect(url_for('main.login'))
    resp = github.authorized_response()
    if resp is None:
        return 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
    session['github_token'] = (resp['access_token'], '')
    me = github.get('user')
    user = me.data['login']
    session['user'] = user
    authorization = Authorization(developer=developer, oauth_token=session['github_token'][0], type='github')
    try:
        db.session.add(authorization)
        db.session.commit()
        return redirect(url_for('auth.github_integration'))
    except:
        db.session.rollback()
        abort(500)
