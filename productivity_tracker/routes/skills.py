from flask import render_template, url_for, flash, redirect, request, Blueprint
from productivity_tracker import db
from productivity_tracker.models import Skill
from datetime import datetime

skills = Blueprint('skills', __name__)

@skills.route('/skills')
def skill_list():
    from productivity_tracker import db
    skills = Skill.query.order_by(db.desc(Skill.level)).all()
    return render_template('skills/list.html', skills=skills, title='Skills')

@skills.route('/skills/new', methods=['GET', 'POST'])
def new_skill():
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
        
    return render_template('skills/new.html', title='New Skill')

@skills.route('/skills/<int:skill_id>')
def skill_detail(skill_id):
    skill = Skill.query.get_or_404(skill_id)
    return render_template('skills/detail.html', skill=skill, title=skill.name)

@skills.route('/skills/<int:skill_id>/edit', methods=['GET', 'POST'])
def edit_skill(skill_id):
    skill = Skill.query.get_or_404(skill_id)
    
    if request.method == 'POST':
        skill.name = request.form.get('name')
        skill.description = request.form.get('description')
        
        db.session.commit()
        
        flash('Skill updated successfully!', 'success')
        return redirect(url_for('skills.skill_detail', skill_id=skill.id))
        
    return render_template('skills/edit.html', skill=skill, title='Edit Skill')

@skills.route('/skills/<int:skill_id>/practice', methods=['GET', 'POST'])
def practice_skill(skill_id):
    skill = Skill.query.get_or_404(skill_id)
    
    if request.method == 'POST':
        hours = float(request.form.get('hours'))
        
        if skill.add_practice_time(hours):
            db.session.commit()
            flash(f'Added {hours} hours to {skill.name}. Current level: {skill.level}', 'success')
        else:
            flash('Please enter a valid practice time.', 'danger')
            
        return redirect(url_for('skills.skill_detail', skill_id=skill.id))
        
    return render_template('skills/practice.html', skill=skill, title='Practice Skill')

@skills.route('/skills/<int:skill_id>/delete', methods=['POST'])
def delete_skill(skill_id):
    skill = Skill.query.get_or_404(skill_id)
    db.session.delete(skill)
    db.session.commit()
    
    flash('Skill deleted successfully!', 'success')
    return redirect(url_for('skills.skill_list'))