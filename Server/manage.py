import os
from jbox import create_app, db
from jbox.models import User, App
from flask_script import Manager, Shell
from flask_migrate import Migrate, MigrateCommand

jbox = create_app(os.getenv('FLASK_CONFIG') or 'default')
manager = Manager(jbox)
migrate = Migrate(jbox, db)


def make_shell_context():
    return dict(jbox=jbox, db=db, User=User, App=App)
manager.add_command("shell", Shell(make_context=make_shell_context()))
manager.add_command('db', MigrateCommand)

if __name__ == '__main__':
    manager.run()


@manager.command
def test():
    """Run the unit tests"""
    import unittest
    tests = unittest.TestLoader.discover('tests')
    unittest.TextTestRunner(verbosity=2).run(tests)
