from flask import render_template, Blueprint
from productivity_tracker.utils import get_total_stats
from datetime import datetime

home = Blueprint('home', __name__)

@home.route('/')
def index():
    stats = get_total_stats()
    return render_template('home.html', stats=stats, title='Dashboard', now=datetime.now())