from flask import Blueprint, render_template, request, redirect, url_for, flash
from datetime import date, datetime, timedelta
from productivity_tracker.models.workout import Workout
from productivity_tracker import db
from sqlalchemy import func

workouts = Blueprint('workouts', __name__, url_prefix='/workouts')

@workouts.route('/')
def workout_list():
    """List all workouts with summary statistics"""
    today = date.today()
    workouts = Workout.query.order_by(Workout.date.desc()).all()
    
    # Get stats for the sidebar
    workout_stats = calculate_workout_stats()
    
    return render_template('workouts/list.html', 
                           workouts=workouts, 
                           stats=workout_stats,
                           today=today)

@workouts.route('/new', methods=['GET', 'POST'])
def new_workout():
    """Add a new workout"""
    today = date.today()
    
    if request.method == 'POST':
        workout_type = request.form.get('workout_type')
        duration = request.form.get('duration')
        intensity = request.form.get('intensity')
        calories_burned = request.form.get('calories_burned') or None
        notes = request.form.get('notes')
        workout_date = datetime.strptime(request.form.get('date'), '%Y-%m-%d').date()
        
        # Validate form inputs
        if not workout_type or not duration:
            flash('Please fill in required fields.', 'danger')
            return render_template('workouts/new.html', today=today)
        
        # Create new workout
        workout = Workout(
            workout_type=workout_type,
            duration=int(duration),
            intensity=intensity,
            calories_burned=int(calories_burned) if calories_burned else None,
            notes=notes,
            date=workout_date
        )
        
        # Save to database
        db.session.add(workout)
        db.session.commit()
        
        flash('Workout added successfully!', 'success')
        return redirect(url_for('workouts.workout_list'))
    
    return render_template('workouts/new.html', today=today)

@workouts.route('/<int:workout_id>')
def workout_detail(workout_id):
    """View details of a specific workout"""
    today = date.today()
    workout = Workout.query.get_or_404(workout_id)
    return render_template('workouts/detail.html', workout=workout, today=today)

@workouts.route('/<int:workout_id>/edit', methods=['GET', 'POST'])
def edit_workout(workout_id):
    """Edit an existing workout"""
    today = date.today()
    workout = Workout.query.get_or_404(workout_id)
    
    if request.method == 'POST':
        workout.workout_type = request.form.get('workout_type')
        workout.duration = int(request.form.get('duration'))
        workout.intensity = request.form.get('intensity')
        
        calories_burned = request.form.get('calories_burned')
        workout.calories_burned = int(calories_burned) if calories_burned else None
        
        workout.notes = request.form.get('notes')
        workout.date = datetime.strptime(request.form.get('date'), '%Y-%m-%d').date()
        
        db.session.commit()
        flash('Workout updated successfully!', 'success')
        return redirect(url_for('workouts.workout_detail', workout_id=workout.id))
    
    return render_template('workouts/edit.html', workout=workout, today=today)

@workouts.route('/<int:workout_id>/delete', methods=['POST'])
def delete_workout(workout_id):
    """Delete a workout"""
    workout = Workout.query.get_or_404(workout_id)
    db.session.delete(workout)
    db.session.commit()
    flash('Workout deleted successfully!', 'success')
    return redirect(url_for('workouts.workout_list'))

@workouts.route('/stats')
def workout_stats():
    """View detailed workout statistics"""
    today = date.today()
    stats = calculate_workout_stats()
    return render_template('workouts/stats.html', stats=stats, today=today)

def calculate_workout_stats():
    """Calculate workout statistics"""
    # Time periods
    today = date.today()
    week_ago = today - timedelta(days=7)
    month_ago = today - timedelta(days=30)
    year_ago = today - timedelta(days=365)
    
    # Basic counts
    total_workouts = Workout.query.count()
    weekly_workouts = Workout.query.filter(Workout.date >= week_ago).count()
    monthly_workouts = Workout.query.filter(Workout.date >= month_ago).count()
    yearly_workouts = Workout.query.filter(Workout.date >= year_ago).count()
    
    # Total durations
    total_duration = db.session.query(func.sum(Workout.duration)).scalar() or 0
    weekly_duration = db.session.query(func.sum(Workout.duration))\
        .filter(Workout.date >= week_ago).scalar() or 0
    monthly_duration = db.session.query(func.sum(Workout.duration))\
        .filter(Workout.date >= month_ago).scalar() or 0
    
    # Average duration
    avg_duration = total_duration / total_workouts if total_workouts > 0 else 0
    
    # Workout type distribution
    workout_types = db.session.query(Workout.workout_type, func.count(Workout.id))\
        .group_by(Workout.workout_type).all()
    
    # Calculate streaks
    current_streak = calculate_current_streak()
    longest_streak = calculate_longest_streak()
    
    # Calculate total points from workouts
    total_points = sum(workout.get_points() for workout in Workout.query.all())
    
    return {
        'total_workouts': total_workouts,
        'weekly_workouts': weekly_workouts,
        'monthly_workouts': monthly_workouts,
        'yearly_workouts': yearly_workouts,
        'total_duration': total_duration,
        'weekly_duration': weekly_duration,
        'monthly_duration': monthly_duration,
        'avg_duration': round(avg_duration, 1),
        'workout_types': workout_types,
        'current_streak': current_streak,
        'longest_streak': longest_streak,
        'total_points': total_points
    }

def calculate_longest_streak():
    """Calculate the longest streak of consecutive days with workouts"""
    workouts = Workout.query.order_by(Workout.date).all()
    if not workouts:
        return 0
    
    dates = [w.date for w in workouts]
    longest_streak = 1
    current_streak = 1
    
    for i in range(1, len(dates)):
        # If consecutive days
        if (dates[i] - dates[i-1]).days == 1:
            current_streak += 1
        # If same day (multiple workouts)
        elif (dates[i] - dates[i-1]).days == 0:
            continue
        # Break in streak
        else:
            longest_streak = max(longest_streak, current_streak)
            current_streak = 1
    
    return max(longest_streak, current_streak)

def calculate_current_streak():
    """Calculate the current streak of consecutive days with workouts"""
    today = date.today()
    
    # Get the most recent workout
    most_recent = Workout.query.order_by(Workout.date.desc()).first()
    if not most_recent:
        return 0
    
    # If most recent workout is older than yesterday, no current streak
    if (today - most_recent.date).days > 1:
        return 0
    
    # Count backwards from most recent workout
    current_date = most_recent.date
    streak = 1
    
    while True:
        previous_date = current_date - timedelta(days=1)
        workout_on_date = Workout.query.filter(Workout.date == previous_date).first()
        
        if not workout_on_date:
            break
            
        streak += 1
        current_date = previous_date
    
    return streak