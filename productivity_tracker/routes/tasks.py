from flask import render_template, url_for, flash, redirect, request, Blueprint
from productivity_tracker import db
from productivity_tracker.models import Task
from datetime import datetime, date

tasks = Blueprint('tasks', __name__)

@tasks.route('/tasks')
def task_list():
    # Get incomplete tasks first, then completed tasks
    incomplete_tasks = Task.query.filter_by(completed=False).order_by(Task.due_date).all()
    completed_tasks = Task.query.filter_by(completed=True).order_by(Task.completed_date.desc()).all()
    
    return render_template('tasks/list.html', 
                          incomplete_tasks=incomplete_tasks,
                          completed_tasks=completed_tasks, 
                          title='Tasks')

@tasks.route('/tasks/new', methods=['GET', 'POST'])
def new_task():
    if request.method == 'POST':
        title = request.form.get('title')
        description = request.form.get('description')
        duration = int(request.form.get('duration', 30))
        due_date_str = request.form.get('due_date')
        
        # Parse due date if provided
        due_date = None
        if due_date_str:
            try:
                due_date = datetime.strptime(due_date_str, '%Y-%m-%d').date()
            except ValueError:
                flash('Invalid date format. Please use YYYY-MM-DD format.', 'danger')
                return redirect(url_for('tasks.new_task'))
        
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
        
    return render_template('tasks/new.html', title='New Task')

@tasks.route('/tasks/<int:task_id>')
def task_detail(task_id):
    task = Task.query.get_or_404(task_id)
    return render_template('tasks/detail.html', task=task, title=task.title)

@tasks.route('/tasks/<int:task_id>/edit', methods=['GET', 'POST'])
def edit_task(task_id):
    task = Task.query.get_or_404(task_id)
    
    if request.method == 'POST':
        task.title = request.form.get('title')
        task.description = request.form.get('description')
        task.duration = int(request.form.get('duration', 30))
        
        due_date_str = request.form.get('due_date')
        if due_date_str:
            try:
                task.due_date = datetime.strptime(due_date_str, '%Y-%m-%d').date()
            except ValueError:
                flash('Invalid date format. Please use YYYY-MM-DD format.', 'danger')
                return redirect(url_for('tasks.edit_task', task_id=task.id))
        else:
            task.due_date = None
        
        db.session.commit()
        
        flash('Task updated successfully!', 'success')
        return redirect(url_for('tasks.task_detail', task_id=task.id))
        
    return render_template('tasks/edit.html', task=task, title='Edit Task')

@tasks.route('/tasks/<int:task_id>/complete', methods=['POST'])
def complete_task(task_id):
    task = Task.query.get_or_404(task_id)
    
    if not task.completed:
        task.mark_completed()
        db.session.commit()
        
        flash(f'Task completed! You earned {task.points} points.', 'success')
    else:
        flash('Task is already completed.', 'info')
        
    return redirect(url_for('tasks.task_list'))

@tasks.route('/tasks/<int:task_id>/delete', methods=['POST'])
def delete_task(task_id):
    task = Task.query.get_or_404(task_id)
    db.session.delete(task)
    db.session.commit()
    
    flash('Task deleted successfully!', 'success')
    return redirect(url_for('tasks.task_list'))