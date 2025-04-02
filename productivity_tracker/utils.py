from productivity_tracker.models import Task, Habit, Skill, StepCount
from productivity_tracker.models.workout import Workout
from datetime import date, timedelta

def calculate_total_points():
    """Calculate the total points from all completed tasks"""
    completed_tasks = Task.query.filter_by(completed=True).all()
    return sum(task.points for task in completed_tasks)

def calculate_streak_points():
    """Calculate additional points from habit streaks"""
    habits = Habit.query.all()
    streak_points = 0
    
    for habit in habits:
        # Bonus points for streaks: 5 points per 5 days of streak
        streak_points += (habit.current_streak // 5) * 5
    
    return streak_points

def calculate_skill_points():
    """Calculate points from skill levels"""
    skills = Skill.query.all()
    # 10 points per skill level
    return sum(skill.level * 10 for skill in skills)

def calculate_step_points():
    """Calculate points from step counts"""
    # Get step counts for the last 7 days
    week_ago = date.today() - timedelta(days=7)
    step_counts = StepCount.query.filter(StepCount.date >= week_ago).all()
    
    step_points = 0
    for step_count in step_counts:
        # 5 points for reaching daily goal
        if step_count.goal_achieved():
            step_points += 5
        # 1 additional point for every 10% above goal
        if step_count.steps > step_count.goal:
            step_points += min(5, (step_count.steps - step_count.goal) // (step_count.goal // 10))
    
    return step_points

def calculate_workout_points():
    """Calculate points from workouts"""
    # Get all workouts
    workouts = Workout.query.all()
    return sum(workout.get_points() for workout in workouts)

def get_total_stats():
    """Get total stats for the user's dashboard"""
    # Task stats
    total_tasks = Task.query.count()
    completed_tasks = Task.query.filter_by(completed=True).count()
    task_completion_rate = int((completed_tasks / total_tasks * 100) if total_tasks > 0 else 0)
    
    # Habit stats
    total_habits = Habit.query.count()
    active_streaks = sum(1 for habit in Habit.query.all() if habit.current_streak > 0)
    longest_streak = max([habit.best_streak for habit in Habit.query.all()], default=0)
    
    # Skill stats
    total_skills = Skill.query.count()
    total_practice_hours = sum(skill.total_hours for skill in Skill.query.all())
    highest_skill_level = max([skill.level for skill in Skill.query.all()], default=0)
    
    # Step stats
    last_week = date.today() - timedelta(days=7)
    weekly_steps = sum(step.steps for step in StepCount.query.filter(StepCount.date >= last_week).all())
    goals_achieved = sum(1 for step in StepCount.query.filter(StepCount.date >= last_week).all() if step.goal_achieved())
    
    # Workout stats
    total_workouts = Workout.query.count()
    last_month = date.today() - timedelta(days=30)
    monthly_workouts = Workout.query.filter(Workout.date >= last_month).count()
    
    # Points
    task_points = calculate_total_points()
    streak_points = calculate_streak_points()
    skill_points = calculate_skill_points()
    step_points = calculate_step_points()
    workout_points = calculate_workout_points()
    total_points = task_points + streak_points + skill_points + step_points + workout_points
    
    return {
        'total_tasks': total_tasks,
        'completed_tasks': completed_tasks,
        'task_completion_rate': task_completion_rate,
        'total_habits': total_habits,
        'active_streaks': active_streaks,
        'longest_streak': longest_streak,
        'total_skills': total_skills,
        'total_practice_hours': total_practice_hours,
        'highest_skill_level': highest_skill_level,
        'weekly_steps': weekly_steps,
        'goals_achieved': goals_achieved,
        'total_workouts': total_workouts,
        'monthly_workouts': monthly_workouts,
        'task_points': task_points,
        'streak_points': streak_points,
        'skill_points': skill_points,
        'step_points': step_points,
        'workout_points': workout_points,
        'total_points': total_points
    }