from flask import Blueprint, render_template
from productivity_tracker.models import Task, Habit, Skill, StepCount
from productivity_tracker.utils import get_total_stats
from datetime import date, timedelta, datetime
from sqlalchemy import desc

home = Blueprint('home', __name__)

@home.route('/')
def index():
    """Dashboard/home page showing overview of all productivity metrics"""
    # Get total stats
    stats = get_total_stats()
    
    # Get recent activities for dashboard
    # Recent tasks (limit to 5)
    recent_tasks = Task.query.order_by(desc(Task.created_at)).limit(5).all()
    
    # Recent habits with streaks (limit to 5)
    recent_habits = Habit.query.order_by(desc(Habit.current_streak)).limit(5).all()
    
    # Top skills by level (limit to 3)
    top_skills = Skill.query.order_by(desc(Skill.level)).limit(3).all()
    
    # Step counts for the last 7 days
    last_week = date.today() - timedelta(days=7)
    step_history = StepCount.query.filter(StepCount.date >= last_week).order_by(StepCount.date).all()
    
    # Today's date for checking habit completion
    today = date.today()
    # Current year for footer
    now = datetime.now()
    
    return render_template('home.html', 
                           stats=stats,
                           recent_tasks=recent_tasks,
                           recent_habits=recent_habits,
                           top_skills=top_skills,
                           step_history=step_history,
                           today=today,
                           now=now)