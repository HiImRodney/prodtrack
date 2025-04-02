from flask import Blueprint, render_template, redirect, url_for, request, flash
from productivity_tracker.models import Habit
from productivity_tracker import db
from datetime import date, timedelta
from sqlalchemy import desc

habits = Blueprint('habits', __name__, url_prefix='/habits')

@habits.route('/')
def habit_list():
    """List all habits"""
    # Get all habits, sorted by current streak (highest first)
    all_habits = Habit.query.order_by(desc(Habit.current_streak)).all()
    today = date.today()
    
    return render_template('habits/list.html', habits=all_habits, today=today)

@habits.route('/new', methods=['GET', 'POST'])
def new_habit():
    """Create a new habit"""
    if request.method == 'POST':
        title = request.form.get('title')
        description = request.form.get('description')
        frequency = request.form.get('frequency')
        
        habit = Habit(
            title=title,
            description=description,
            frequency=frequency
        )
        
        db.session.add(habit)
        db.session.commit()
        
        flash('Habit created successfully!', 'success')
        return redirect(url_for('habits.habit_list'))
    
    return render_template('habits/new.html')

@habits.route('/<int:habit_id>')
def habit_detail(habit_id):
    """View details of a specific habit"""
    habit = Habit.query.get_or_404(habit_id)
    today = date.today()
    return render_template('habits/detail.html', habit=habit, today=today)

@habits.route('/<int:habit_id>/edit', methods=['GET', 'POST'])
def edit_habit(habit_id):
    """Edit an existing habit"""
    habit = Habit.query.get_or_404(habit_id)
    
    if request.method == 'POST':
        habit.title = request.form.get('title')
        habit.description = request.form.get('description')
        habit.frequency = request.form.get('frequency')
        
        db.session.commit()
        
        flash('Habit updated successfully!', 'success')
        return redirect(url_for('habits.habit_detail', habit_id=habit.id))
    
    return render_template('habits/edit.html', habit=habit)

@habits.route('/<int:habit_id>/complete', methods=['POST'])
def complete_habit(habit_id):
    """Mark a habit as completed for today"""
    habit = Habit.query.get_or_404(habit_id)
    if habit.mark_completed():
        db.session.commit()
        
        # Calculate streak bonus points
        streak_milestone = habit.current_streak % 5 == 0 and habit.current_streak > 0
        if streak_milestone:
            bonus_points = (habit.current_streak // 5) * 5
            message = f'Habit completed! Your streak is now {habit.current_streak}. Streak milestone: +{bonus_points} points!'
        else:
            message = f'Habit completed! Your streak is now {habit.current_streak}.'
            
        flash(message, 'success')
    else:
        flash('Habit already completed today.', 'info')
    
    return redirect(url_for('habits.habit_list'))

@habits.route('/<int:habit_id>/use_nidge_card', methods=['POST'])
def use_nidge_card(habit_id):
    """Use a nidge card to skip a day without breaking streak"""
    habit = Habit.query.get_or_404(habit_id)
    
    if habit.nidge_card_used:
        flash('Nidge Card already used this week. It will reset soon.', 'warning')
        return redirect(url_for('habits.habit_detail', habit_id=habit.id))
    
    # Reset the nidge card status if needed before using it
    habit.reset_nidge_card()
    
    # Use the nidge card
    habit.nidge_card_used = True
    habit.nidge_card_reset_date = date.today() + timedelta(days=7)
    db.session.commit()
    
    flash('Nidge Card used successfully. You can skip today without breaking your streak!', 'success')
    return redirect(url_for('habits.habit_detail', habit_id=habit.id))

@habits.route('/<int:habit_id>/delete', methods=['POST'])
def delete_habit(habit_id):
    """Delete a habit"""
    habit = Habit.query.get_or_404(habit_id)
    db.session.delete(habit)
    db.session.commit()
    
    flash('Habit deleted successfully.', 'success')
    return redirect(url_for('habits.habit_list'))