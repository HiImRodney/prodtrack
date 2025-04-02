from productivity_tracker import db
from datetime import datetime, date

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
        return f"Habit('{self.title}', streak: {self.current_streak}, best: {self.best_streak})"
    
    def mark_completed(self):
        """Mark the habit as completed for today"""
        today = date.today()
        
        # Skip if already completed today
        if self.last_completed == today:
            return False
            
        # Calculate streak
        if self.last_completed:
            days_difference = (today - self.last_completed).days
            
            if days_difference == 1:
                # Consecutive day, streak continues
                self.current_streak += 1
            elif days_difference == 2 and not self.nidge_card_used:
                # Missed one day but can use nidge card
                self.current_streak += 1
                self.nidge_card_used = True
                self.nidge_card_reset_date = today  # Reset nidge card in a week
            else:
                # Streak broken
                self.current_streak = 1
        else:
            # First completion
            self.current_streak = 1
            
        # Update best streak if current is better
        if self.current_streak > self.best_streak:
            self.best_streak = self.current_streak
            
        self.last_completed = today
        return True
        
    def reset_nidge_card(self):
        """Reset the nidge card usage on a weekly basis"""
        today = date.today()
        if self.nidge_card_reset_date and (today - self.nidge_card_reset_date).days >= 7:
            self.nidge_card_used = False
            self.nidge_card_reset_date = None
            return True
        return False