from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import os

# Initialize SQLAlchemy
db = SQLAlchemy()

def create_app():
    # Initialize Flask application
    app = Flask(__name__)
    
    # Configure SQLite database
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///productivity.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config['SECRET_KEY'] = os.urandom(24)
    
    # Initialize extensions with app
    db.init_app(app)
    
    # Register blueprints
    from productivity_tracker.routes.home import home
    from productivity_tracker.routes.tasks import tasks
    from productivity_tracker.routes.habits import habits
    from productivity_tracker.routes.skills import skills
    from productivity_tracker.routes.steps import steps
    
    app.register_blueprint(home)
    app.register_blueprint(tasks)
    app.register_blueprint(habits)
    app.register_blueprint(skills)
    app.register_blueprint(steps)
    
    # Create database tables
    with app.app_context():
        db.create_all()
    
    return app

# Create app instance for direct imports
app = create_app()