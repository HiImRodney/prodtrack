from flask import Blueprint, render_template, redirect, url_for, request, flash
from productivity_tracker.models import Skill
from productivity_tracker import db
from sqlalchemy import desc

skills = Blueprint('skills', __name__, url_prefix='/skills')

@skills.route('/')
def skill_list():
    """List all skills"""
    all_skills = Skill.query.order_by(desc(Skill.level)).all()
    return render_template('skills/list.html', skills=all_skills)

@skills.route('/new', methods=['GET', 'POST'])
def new_skill():
    """Create a new skill"""
    if request.method == 'POST':
        name = request.form.get('name')
        description = request.form.get('description')
        
        skill = Skill(
            name=name,
            description=description
        )
        
        db.session.add(skill)
        db.session.commit()
        
        flash('Skill created successfully!', 'success')
        return redirect(url_for('skills.skill_list'))
    
    return render_template('skills/new.html')

@skills.route('/<int:skill_id>')
def skill_detail(skill_id):
    """View details of a specific skill"""
    skill = Skill.query.get_or_404(skill_id)
    return render_template('skills/detail.html', skill=skill)

@skills.route('/<int:skill_id>/edit', methods=['GET', 'POST'])
def edit_skill(skill_id):
    """Edit an existing skill"""
    skill = Skill.query.get_or_404(skill_id)
    
    if request.method == 'POST':
        skill.name = request.form.get('name')
        skill.description = request.form.get('description')
        
        db.session.commit()
        
        flash('Skill updated successfully!', 'success')
        return redirect(url_for('skills.skill_detail', skill_id=skill.id))
    
    return render_template('skills/edit.html', skill=skill)

@skills.route('/<int:skill_id>/practice', methods=['GET', 'POST'])
def practice_skill(skill_id):
    """Log practice time for a skill"""
    skill = Skill.query.get_or_404(skill_id)
    
    if request.method == 'POST':
        hours = float(request.form.get('hours'))
        
        if hours <= 0:
            flash('Please enter a positive number of hours.', 'danger')
            return redirect(url_for('skills.practice_skill', skill_id=skill.id))
        
        previous_level = skill.level
        new_total = skill.add_practice_time(hours)
        
        # Check if level increased
        if skill.level > previous_level:
            flash(f'Great job! You practiced for {hours} hours. '
                  f'Your skill level increased to level {skill.level}! '
                  f'Total practice: {new_total:.1f} hours', 'success')
        else:
            flash(f'You practiced for {hours} hours. '
                  f'Total practice: {new_total:.1f} hours', 'success')
            
        db.session.commit()
        return redirect(url_for('skills.skill_detail', skill_id=skill.id))
    
    return render_template('skills/practice.html', skill=skill)

@skills.route('/<int:skill_id>/delete', methods=['POST'])
def delete_skill(skill_id):
    """Delete a skill"""
    skill = Skill.query.get_or_404(skill_id)
    db.session.delete(skill)
    db.session.commit()
    
    flash('Skill deleted successfully.', 'success')
    return redirect(url_for('skills.skill_list'))