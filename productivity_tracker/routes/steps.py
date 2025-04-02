from flask import Blueprint, render_template, redirect, url_for, request, flash
from productivity_tracker.models import StepCount
from productivity_tracker import db
from datetime import date, timedelta, datetime
from sqlalchemy import desc

steps = Blueprint('steps', __name__, url_prefix='/steps')

@steps.route('/')
def step_history():
    """View step count history"""
    # Get step data for the last 30 days
    month_ago = date.today() - timedelta(days=30)
    step_history = StepCount.query.filter(StepCount.date >= month_ago).order_by(StepCount.date).all()
    
    # Make sure we have today's record
    today = date.today()
    today_record = StepCount.query.filter_by(date=today).first()
    
    if not today_record:
        # Create a new record for today
        today_record = StepCount(date=today)
        db.session.add(today_record)
        db.session.commit()
        step_history.append(today_record)
    
    return render_template('steps/history.html', 
                           step_history=step_history,
                           today_record=today_record)

@steps.route('/add', methods=['GET', 'POST'])
def add_steps():
    """Add steps to today's count"""
    today = date.today()
    today_record = StepCount.query.filter_by(date=today).first()
    
    if not today_record:
        today_record = StepCount(date=today)
        db.session.add(today_record)
        db.session.commit()
    
    if request.method == 'POST':
        steps_to_add = int(request.form.get('steps', 0))
        
        if steps_to_add <= 0:
            flash('Please enter a positive number of steps.', 'danger')
            return redirect(url_for('steps.add_steps'))
        
        today_record.add_steps(steps_to_add)
        db.session.commit()
        
        if today_record.goal_achieved() and today_record.steps - steps_to_add < today_record.goal:
            flash(f'Congratulations! You added {steps_to_add} steps and reached your daily goal of {today_record.goal} steps!', 'success')
        else:
            flash(f'Added {steps_to_add} steps. Current total: {today_record.steps} steps.', 'success')
            
        return redirect(url_for('steps.step_history'))
    
    return render_template('steps/add.html', today_record=today_record)

@steps.route('/set_goal', methods=['GET', 'POST'])
def set_goal():
    """Set or update the daily step goal"""
    today = date.today()
    today_record = StepCount.query.filter_by(date=today).first()
    
    if not today_record:
        today_record = StepCount(date=today)
        db.session.add(today_record)
        db.session.commit()
    
    if request.method == 'POST':
        new_goal = int(request.form.get('goal', 10000))
        
        if new_goal <= 0:
            flash('Please enter a positive number of steps.', 'danger')
            return redirect(url_for('steps.set_goal'))
        
        # Update the goal for today and future entries
        today_record.goal = new_goal
        db.session.commit()
        
        flash(f'Daily step goal updated to {new_goal} steps.', 'success')
        return redirect(url_for('steps.step_history'))
    
    return render_template('steps/set_goal.html', today_record=today_record)