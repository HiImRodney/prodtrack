from flask import render_template, url_for, flash, redirect, request, Blueprint
from productivity_tracker import db
from productivity_tracker.models import Habit
from datetime import datetime, date

habits = Blueprint('habits', __name__)

@habits.route('/habits')
def habit_list():
    from productivity_tracker import db
    habits = Habit.query.order_by(db.desc(Habit.current_streak)).all()
    return render_template('habits/list.html', habits=habits, title='Habits')

@habits.route('/habits/new', methods=['GET', 'POST'])
def new_habit():
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
        
    return render_template('habits/new.html', title='New Habit')

@habits.route('/habits/<int:habit_id>')
def habit_detail(habit_id):
    habit = Habit.query.get_or_404(habit_id)
    return render_template('habits/detail.html', habit=habit, title=habit.title)

@habits.route('/habits/<int:habit_id>/edit', methods=['GET', 'POST'])
def edit_habit(habit_id):
    habit = Habit.query.get_or_404(habit_id)
    
    if request.method == 'POST':
        habit.title = request.form.get('title')
        habit.description = request.form.get('description')
        habit.frequency = request.form.get('frequency')
        
        db.session.commit()
        
        flash('Habit updated successfully!', 'success')
        return redirect(url_for('habits.habit_detail', habit_id=habit.id))
        
    return render_template('habits/edit.html', habit=habit, title='Edit Habit')

@habits.route('/habits/<int:habit_id>/complete', methods=['POST'])
def complete_habit(habit_id):
    habit = Habit.query.get_or_404(habit_id)
    completed = habit.mark_completed()
    
    # Reset nidge card if needed
    habit.reset_nidge_card()
    
    db.session.commit()
    
    if completed:
        flash(f'Habit completed! Current streak: {habit.current_streak}', 'success')
    else:
        flash('Habit already completed for today.', 'info')
        
    return redirect(url_for('habits.habit_list'))

@habits.route('/habits/<int:habit_id>/delete', methods=['POST'])
def delete_habit(habit_id):
    habit = Habit.query.get_or_404(habit_id)
    db.session.delete(habit)
    db.session.commit()
    
    flash('Habit deleted successfully!', 'success')
    return redirect(url_for('habits.habit_list'))