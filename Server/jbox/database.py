# import sqlite3
# from flask import g
# from jbox import jbox
#
#
# DATABASE = '/jbox/database.db'
#
#
# def connect_db():
#     return sqlite3.connect(DATABASE)
#
#
# @jbox.before_request
# def before_request():
#     g.db = connect_db()
#
#
# @jbox.teardown_request
# def teardown_request(exception):
#     if hasattr(g, 'db'):
#         g.db.close()
#
# with jbox.test_request_context():
#     jbox.preprocess_request()
#
#
# def query_db(query, args=(), one=False):
#     cur = g.db.execute(query, args)
#     rv = [dict((cur.description[idx][0], value)
#                for idx, value in enumerate(row)) for row in cur.fetchall()]
#     return (rv[0] if rv[0] else None) if one else rv