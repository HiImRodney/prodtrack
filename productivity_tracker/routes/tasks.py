from flask import Blueprint, render_template, redirect, url_for, request, flash
from productivity_tracker.models import Task
from productivity_tracker import db
from datetime import datetime, date
from sqlalchemy import desc

tasks = Blueprint('tasks', __name__, url_prefix='/tasks')

@tasks.route('/')
def task_list():
    """List all tasks with filtering options"""
    # Sort tasks by due date, with uncompleted tasks first
    upcoming_tasks = Task.query.filter_by(completed=False).order_by(Task.due_date).all()
    completed_tasks = Task.query.filter_by(completed=True).order_by(desc(Task.completed_date)).limit(10).all()
    
    return render_template('tasks/list.html', 
                           upcoming_tasks=upcoming_tasks,
                           completed_tasks=completed_tasks)

@tasks.route('/new', methods=['GET', 'POST'])
def new_task():
    """Create a new task"""
    if request.method == 'POST':
        title = request.form.get('title')
        description = request.form.get('description')
        duration = int(request.form.get('duration'))
        due_date_str = request.form.get('due_date')
        
        if due_date_str:
            due_date = datetime.strptime(due_date_str, '%Y-%m-%d').date()
        else:
            due_date = None
        
        task = Task(
            title=title,
            description=description,
            duration=duration,
            due_date=due_date
        )
        
        db.session.add(task)
        db.session.commit()
        
        flash('Task created successfully!', 'success')
        return redirect(url_for('tasks.task_list'))
    
    return render_template('tasks/new.html')

@tasks.route('/<int:task_id>')
def task_detail(task_id):
    """View details of a specific task"""
    task = Task.query.get_or_404(task_id)
    return render_template('tasks/detail.html', task=task)

@tasks.route('/<int:task_id>/edit', methods=['GET', 'POST'])
def edit_task(task_id):
    """Edit an existing task"""
    task = Task.query.get_or_404(task_id)
    
    if request.method == 'POST':
        task.title = request.form.get('title')
        task.description = request.form.get('description')
        task.duration = int(request.form.get('duration'))
        
        due_date_str = request.form.get('due_date')
        if due_date_str:
            task.due_date = datetime.strptime(due_date_str, '%Y-%m-%d').date()
        else:
            task.due_date = None
        
        db.session.commit()
        
        flash('Task updated successfully!', 'success')
        return redirect(url_for('tasks.task_detail', task_id=task.id))
    
    return render_template('tasks/edit.html', task=task)

@tasks.route('/<int:task_id>/complete', methods=['POST'])
def complete_task(task_id):
    """Mark a task as completed"""
    task = Task.query.get_or_404(task_id)
    if task.mark_completed():
        db.session.commit()
        flash(f'Task completed! You earned {task.points} points.', 'success')
    else:
        flash('Task was already completed.', 'info')
    
    return redirect(url_for('tasks.task_list'))

@tasks.route('/<int:task_id>/delete', methods=['POST'])
def delete_task(task_id):
    """Delete a task"""
    task = Task.query.get_or_404(task_id)
    db.session.delete(task)
    db.session.commit()
    
    flash('Task deleted successfully.', 'success')
    return redirect(url_for('tasks.task_list'))