from datetime import datetime, date, timedelta
from productivity_tracker import db
from productivity_tracker.models import Task, Habit, Skill, StepCount

def calculate_total_points():
    """Calculate the total points from all completed tasks"""
    tasks = Task.query.filter_by(completed=True).all()
    return sum(task.points for task in tasks)

def calculate_streak_points():
    """Calculate additional points from habit streaks"""
    habits = Habit.query.all()
    streak_points = 0
    
    for habit in habits:
        # Points based on streak length
        if habit.current_streak >= 30:
            streak_points += 50  # 30+ day streak
        elif habit.current_streak >= 21:
            streak_points += 35  # 21+ day streak
        elif habit.current_streak >= 14:
            streak_points += 25  # 14+ day streak
        elif habit.current_streak >= 7:
            streak_points += 15  # 7+ day streak
        elif habit.current_streak >= 3:
            streak_points += 5   # 3+ day streak
    
    return streak_points

def calculate_skill_points():
    """Calculate points from skill levels"""
    skills = Skill.query.all()
    skill_points = 0
    
    for skill in skills:
        # Points based on skill level
        skill_points += skill.level * 10
    
    return skill_points

def calculate_step_points():
    """Calculate points from step counts"""
    today = date.today()
    step_count = StepCount.query.filter_by(date=today).first()
    
    if not step_count:
        return 0
        
    # Points based on percentage of daily goal
    percentage = step_count.progress_percentage()
    
    if percentage >= 100:
        return 20  # Full goal reached
    elif percentage >= 75:
        return 15  # 75% of goal
    elif percentage >= 50:
        return 10  # 50% of goal
    elif percentage >= 25:
        return 5   # 25% of goal
    else:
        return 0

def get_total_stats():
    """Get total stats for the user's dashboard"""
    task_points = calculate_total_points()
    streak_points = calculate_streak_points()
    skill_points = calculate_skill_points()
    step_points = calculate_step_points()
    
    total_points = task_points + streak_points + skill_points + step_points
    
    completed_tasks = Task.query.filter_by(completed=True).count()
    active_habits = Habit.query.filter(Habit.current_streak > 0).count()
    
    # Get the habit with the longest streak
    best_habit = Habit.query.order_by(db.desc(Habit.current_streak)).first()
    best_streak = best_habit.current_streak if best_habit else 0
    
    # Get the skill with the highest level
    best_skill = Skill.query.order_by(db.desc(Skill.level)).first()
    highest_level = best_skill.level if best_skill else 0
    
    return {
        'total_points': total_points,
        'task_points': task_points,
        'streak_points': streak_points,
        'skill_points': skill_points,
        'step_points': step_points,
        'completed_tasks': completed_tasks,
        'active_habits': active_habits,
        'best_streak': best_streak,
        'highest_skill_level': highest_level
    }