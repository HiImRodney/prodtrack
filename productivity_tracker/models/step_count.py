from productivity_tracker import db
from datetime import datetime, date

class StepCount(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.Date, default=date.today, nullable=False)
    steps = db.Column(db.Integer, default=0)
    goal = db.Column(db.Integer, default=10000)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f"StepCount(date: {self.date}, steps: {self.steps}/{self.goal})"
    
    def add_steps(self, count):
        """Add steps to today's count"""
        if count <= 0:
            return False
            
        self.steps += count
        return True
        
    def goal_achieved(self):
        """Check if daily goal is achieved"""
        return self.steps >= self.goal
        
    def progress_percentage(self):
        """Calculate progress as percentage"""
        if self.goal <= 0:
            return 100
        return min(100, int((self.steps / self.goal) * 100))