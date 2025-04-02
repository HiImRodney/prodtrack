from productivity_tracker import db
from datetime import datetime, date, timedelta

class NidgeCard(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    used = db.Column(db.Boolean, default=False)
    used_date = db.Column(db.Date)
    reset_date = db.Column(db.Date)
    habit_id = db.Column(db.Integer, db.ForeignKey('habit.id'), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f"NidgeCard(habit_id={self.habit_id}, used={self.used})"
    
    def use_card(self):
        """Use the nidge card if available"""
        if not self.used:
            self.used = True
            self.used_date = date.today()
            self.reset_date = date.today() + timedelta(days=7)
            return True
        return False
    
    def check_reset(self):
        """Check if the nidge card should be reset"""
        today = date.today()
        if self.reset_date and today >= self.reset_date:
            self.used = False
            self.reset_date = today + timedelta(days=7)
            return True
        return False