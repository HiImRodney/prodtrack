from flask import Blueprint, render_template, request, redirect, url_for, flash
from datetime import datetime, date
from productivity_tracker import db
from productivity_tracker.models.reward import Reward
from sqlalchemy import desc
from productivity_tracker.utils import calculate_total_points

rewards = Blueprint('rewards', __name__, url_prefix='/rewards')

@rewards.route('/')
def reward_list():
    """List all rewards"""
    # Get total points
    total_points = calculate_total_points()
    
    # Get available rewards (not redeemed)
    available_rewards = Reward.query.filter_by(is_redeemed=False).order_by(Reward.points_cost).all()
    
    # Get redeemed rewards
    redeemed_rewards = Reward.query.filter_by(is_redeemed=True).order_by(desc(Reward.redeemed_date)).all()
    
    return render_template('rewards/list.html', 
                          total_points=total_points,
                          available_rewards=available_rewards,
                          redeemed_rewards=redeemed_rewards,
                          today=date.today())

@rewards.route('/new', methods=['GET', 'POST'])
def new_reward():
    """Create a new reward"""
    if request.method == 'POST':
        name = request.form.get('name')
        description = request.form.get('description')
        points_cost = int(request.form.get('points_cost', 100))
        
        # Create new reward
        reward = Reward(
            name=name,
            description=description,
            points_cost=points_cost
        )
        
        # Save to database
        db.session.add(reward)
        db.session.commit()
        
        flash(f'Reward "{name}" created successfully!', 'success')
        return redirect(url_for('rewards.reward_list'))
    
    # GET request - show form
    return render_template('rewards/new.html', today=date.today())

@rewards.route('/<int:reward_id>')
def reward_detail(reward_id):
    """View details of a specific reward"""
    reward = Reward.query.get_or_404(reward_id)
    total_points = calculate_total_points()
    
    return render_template('rewards/detail.html',
                          reward=reward,
                          total_points=total_points,
                          today=date.today())

@rewards.route('/<int:reward_id>/edit', methods=['GET', 'POST'])
def edit_reward(reward_id):
    """Edit an existing reward"""
    reward = Reward.query.get_or_404(reward_id)
    
    # Don't allow editing redeemed rewards
    if reward.is_redeemed:
        flash('Cannot edit a reward that has already been redeemed.', 'warning')
        return redirect(url_for('rewards.reward_detail', reward_id=reward.id))
    
    if request.method == 'POST':
        reward.name = request.form.get('name')
        reward.description = request.form.get('description')
        reward.points_cost = int(request.form.get('points_cost', 100))
        
        # Save changes
        db.session.commit()
        
        flash(f'Reward "{reward.name}" updated successfully!', 'success')
        return redirect(url_for('rewards.reward_detail', reward_id=reward.id))
    
    # GET request - show form
    total_points = calculate_total_points()
    return render_template('rewards/edit.html',
                          reward=reward,
                          total_points=total_points,
                          today=date.today())

@rewards.route('/<int:reward_id>/redeem', methods=['POST'])
def redeem_reward(reward_id):
    """Redeem a reward using available points"""
    reward = Reward.query.get_or_404(reward_id)
    
    # Check if already redeemed
    if reward.is_redeemed:
        flash('This reward has already been redeemed.', 'warning')
        return redirect(url_for('rewards.reward_detail', reward_id=reward.id))
    
    # Get total points
    total_points = calculate_total_points()
    
    # Attempt to redeem
    if reward.redeem(total_points):
        db.session.commit()
        flash(f'Congratulations! You have redeemed "{reward.name}" successfully.', 'success')
    else:
        flash(f'Not enough points to redeem this reward. You need {reward.points_cost} points but have {total_points}.', 'danger')
    
    return redirect(url_for('rewards.reward_detail', reward_id=reward.id))

@rewards.route('/<int:reward_id>/delete', methods=['POST'])
def delete_reward(reward_id):
    """Delete a reward"""
    reward = Reward.query.get_or_404(reward_id)
    
    # Store name for flash message
    reward_name = reward.name
    
    # Delete from database
    db.session.delete(reward)
    db.session.commit()
    
    flash(f'Reward "{reward_name}" has been deleted.', 'success')
    return redirect(url_for('rewards.reward_list'))