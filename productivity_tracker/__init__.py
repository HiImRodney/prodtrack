from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os
from markupsafe import Markup

# Initialize SQLAlchemy
db = SQLAlchemy()

def create_app():
    app = Flask(__name__)
    app.config['SECRET_KEY'] = os.environ.get('SECRET_KEY', 'default_secret_key')
    app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URI', 'sqlite:///productivity.db')
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    
    # Initialize database with app
    db.init_app(app)
    
    # Register custom Jinja2 filters
    app.jinja_env.filters['nl2br'] = nl2br
    
    # Import and register blueprints
    from productivity_tracker.routes.home import home
    from productivity_tracker.routes.tasks import tasks
    from productivity_tracker.routes.habits import habits
    from productivity_tracker.routes.skills import skills
    from productivity_tracker.routes.steps import steps
    from productivity_tracker.routes.rewards import rewards
    
    app.register_blueprint(home)
    app.register_blueprint(tasks)
    app.register_blueprint(habits)
    app.register_blueprint(skills)
    app.register_blueprint(steps)
    app.register_blueprint(rewards)
    
    # Create database tables if they don't exist
    with app.app_context():
        db.create_all()
    
    return app

# Custom Jinja2 filters
def nl2br(value):
    """Convert newlines to <br> tags."""
    if value:
        return Markup(value.replace('\n', '<br>'))