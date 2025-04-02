from productivity_tracker import db
from datetime import datetime, date, timedelta

class Habit(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(500))
    frequency = db.Column(db.String(50), default='daily')  # daily, weekly, etc.
    current_streak = db.Column(db.Integer, default=0)
    best_streak = db.Column(db.Integer, default=0)
    last_completed = db.Column(db.Date)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    nidge_card_used = db.Column(db.Boolean, default=False)  # Track if nidge card was used this week
    nidge_card_reset_date = db.Column(db.Date)  # When to reset the nidge card
    
    def __repr__(self):
        return f"Habit('{self.title}', streak={self.current_streak})"
    
    def mark_completed(self):
        """Mark the habit as completed for today"""
        today = date.today()
        
        # Check if already completed today
        if self.last_completed and self.last_completed == today:
            return False
            
        # Check for streak continuation
        expected_date = None
        if self.frequency == 'daily':
            expected_date = date.today() - timedelta(days=1)
        elif self.frequency == 'weekly':
            expected_date = date.today() - timedelta(days=7)
        elif self.frequency == 'monthly':
            # Approximate month by 30 days
            expected_date = date.today() - timedelta(days=30)
        
        # If last_completed is not expected_date, check nidge card
        if self.last_completed and self.last_completed != expected_date:
            # Allow one missed day per week with nidge card
            if not self.nidge_card_used:
                self.nidge_card_used = True
                self.nidge_card_reset_date = today + timedelta(days=7)
            else:
                # Break streak
                self.current_streak = 0
        
        # Increment streak
        self.current_streak += 1
        
        # Update best streak if needed
        if self.current_streak > self.best_streak:
            self.best_streak = self.current_streak
            
        self.last_completed = today
        
        # Check if we should reset nidge card
        if self.nidge_card_reset_date and today >= self.nidge_card_reset_date:
            self.reset_nidge_card()
            
        return True
    
    def reset_nidge_card(self):
        """Reset the nidge card usage on a weekly basis"""
        self.nidge_card_used = False
        self.nidge_card_reset_date = date.today() + timedelta(days=7)