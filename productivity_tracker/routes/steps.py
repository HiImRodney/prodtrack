from flask import render_template, url_for, flash, redirect, request, Blueprint
from productivity_tracker import db
from productivity_tracker.models import StepCount
from datetime import datetime, date

steps = Blueprint('steps', __name__)

@steps.route('/steps')
def step_history():
    step_counts = StepCount.query.order_by(StepCount.date.desc()).all()
    
    # Get today's step count or create one if it doesn't exist
    today = date.today()
    today_count = StepCount.query.filter_by(date=today).first()
    
    if not today_count:
        today_count = StepCount(date=today)
        db.session.add(today_count)
        db.session.commit()
    
    return render_template('steps/history.html', step_counts=step_counts, 
                          today_count=today_count, title='Step Tracker')

@steps.route('/steps/add', methods=['POST'])
def add_steps():
    steps = int(request.form.get('steps', 0))
    
    if steps <= 0:
        flash('Please enter a valid step count.', 'danger')
        return redirect(url_for('steps.step_history'))
    
    today = date.today()
    step_count = StepCount.query.filter_by(date=today).first()
    
    if not step_count:
        step_count = StepCount(date=today, steps=steps)
        db.session.add(step_count)
    else:
        step_count.add_steps(steps)
    
    db.session.commit()
    
    if step_count.goal_achieved():
        flash(f'Added {steps} steps! Congratulations on reaching your daily goal!', 'success')
    else:
        progress = step_count.progress_percentage()
        flash(f'Added {steps} steps! You are at {progress}% of your daily goal.', 'info')
    
    return redirect(url_for('steps.step_history'))

@steps.route('/steps/set-goal', methods=['POST'])
def set_goal():
    goal = int(request.form.get('goal', 10000))
    
    if goal <= 0:
        flash('Please enter a valid step goal.', 'danger')
        return redirect(url_for('steps.step_history'))
    
    today = date.today()
    step_count = StepCount.query.filter_by(date=today).first()
    
    if not step_count:
        step_count = StepCount(date=today, goal=goal)
        db.session.add(step_count)
    else:
        step_count.goal = goal
    
    db.session.commit()
    
    flash(f'Daily step goal updated to {goal} steps!', 'success')
    return redirect(url_for('steps.step_history'))