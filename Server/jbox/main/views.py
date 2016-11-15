from flask import render_template, session, redirect, url_for, flash, request, Flask, jsonify, Markup
from flask_oauthlib.client import OAuth
from . import main
from ..models import Developer, Integration, generate_dev_key

# QQ Oauth2
QQ_APP_ID = "101348155"
QQ_APP_KEY = "59a307794c8955a6abfaa30c9c87c737"

app = Flask(__name__)
app.debug = True
app.secret_key = 'development'
oauth = OAuth(app)

qq = oauth.remote_app(
    'qq',
    consumer_key=QQ_APP_ID,
    consumer_secret=QQ_APP_KEY,
    base_url='https://graph.qq.com',
    request_token_url=None,
    request_token_params={'scope': 'get_user_info'},
    access_token_url='/oauth2.0/token',
    authorize_url='/oauth2.0/authorize',
)

def json_to_dict(x):
    '''OAuthResponse class can't not parse the JSON data with content-type
    text/html, so we need reload the JSON data manually'''
    print(x)
    x = x.decode()
    print(x)
    if x.find('callback') > -1:
        pos_lb = x.find('{')
        pos_rb = x.find('}')
        x = x[pos_lb:pos_rb + 1]
    try:
        print(x)
        # return json.loads(x, encoding='utf-8')
        return eval(x)
    except:
        return x


def update_qq_api_request_data(data={}):
    '''Update some required parameters for OAuth2.0 API calls'''
    defaults = {
        'openid': session.get('qq_openid'),
        'access_token': session.get('qq_token')[0],
        'oauth_consumer_key': QQ_APP_ID,
    }
    defaults.update(data)
    return defaults


@main.route('/user_info')
def get_user_info():
    if 'qq_token' in session:
        data = update_qq_api_request_data()
        resp = qq.get('/user/get_user_info', data=data)
        return jsonify(status=resp.status, data=resp.data.decode())
    return redirect(url_for('main.login'))


@main.route('/login')
def login():
    return qq.authorize(callback=url_for('main.authorized', _external=True))


@main.route('/logout')
def logout():
    session.pop('qq_token', None)
    return redirect(url_for('main.index'))


@main.route('/login/authorized')
def authorized():
    resp = qq.authorized_response()
    if resp is None:
        return 'Access denied: reason=%s error=%s' % (
            request.args['error_reason'],
            request.args['error_description']
        )
    session['qq_token'] = (resp['access_token'], '')

    # Get openid via access_token, openid and access_token are needed for API calls
    respMe = qq.get('/oauth2.0/me', {'access_token': session['qq_token'][0]})
    openid = json_to_dict(respMe.data)['openid']

    print(openid)

    resp = qq.get('/user/get_user_info', {'access_token': session['qq_token'][0],
                                                 'openid': openid,
                                                 'oauth_consumer_key': QQ_APP_ID})

    resp = eval(resp.data.decode())
    developer = None
    if isinstance(resp, dict):
        session['qq_openid'] = resp.get('openid')
        developer = Developer.query.filter_by(platform_id=openid,
                                              platform='qq').first()

        if developer is None:
            dev_key = generate_dev_key()
            developer = Developer(dev_key=dev_key,
                                  platform='qq',
                                  platform_id=openid,
                                  description='')
            developer.insert_to_db()
    if developer is None:
        developer = get_developer()
    username = developer.username
    if username is None or username == '':
        print('login first time. redirect to setting')
        return redirect(url_for('auth.setting'))
    return redirect(url_for('auth.manage'))


@qq.tokengetter
def get_qq_oauth_token():
    return session.get('qq_token')


@main.route('/', methods=['GET', 'POST'])
def index():
    developer = get_developer()
    return render_template('index.html', developer=developer)


# @main.route('/login', methods=['GET', 'POST'])
# def login():
#     form = FakeUserForm()
#     if form is None:
#         flash('Should input username and password')
#     else:
#         if form.validate_on_submit():
#             developer = Developer.query.filter_by(platform=form.platform.data,
#                           platform_id=form.platform_id.data).first()
#             if developer is not None:
#                 login_user(developer, remember=True)
#                 integrations = Developer.query.filter_by(dev_key=current_user.dev_key).first().integrations
#                 return render_template('auth/manage.html', integrations=integrations, dev_key=current_user.dev_key)
#             else:
#                 flash('Can find user by platform:' + form.platform.data + ' platform_id' + form.platform_id.data)
#     return render_template('login.html', form=form)


@main.route('/document', methods=['GET'])
def document():
    developer = get_developer()
    # if developer is None:
    #     return redirect(url_for('main.login'))

    username = None
    if developer is None:
        return render_template('document.html', developer=developer)
    else:
        username = developer.username
        if (username is None) or (username == ''):
            return redirect(url_for('auth.setting'))
        return render_template('document.html', developer=developer)


@main.route('/guide', methods=['GET'])
def guide():
    developer = get_developer()
    if developer is None:
        return render_template('guide.html', developer=developer)
    else:
        username = developer.username
        if username is None or username == '':
            return redirect(url_for('auth.setting'))
        return render_template('guide.html', developer=developer)


@main.route('/application', methods=['GET'])
def application():
    developer = get_developer()
    if developer is None:
        return render_template('application.html', developer=developer)
    else:
        username = developer.username
        if username is None or username == '':
            return redirect(url_for('auth.setting'))
        return render_template('application.html', developer=developer)



def get_developer():
    if 'qq_token' in session:
        respMe = qq.get('/oauth2.0/me', {'access_token': session['qq_token'][0]})
        openid = json_to_dict(respMe.data)['openid']
        developer = Developer.query.filter_by(platform_id=openid).first()
        return developer
    return None
