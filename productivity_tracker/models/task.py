from productivity_tracker import db
from datetime import datetime

class Task(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(500))
    duration = db.Column(db.Integer, nullable=False)  # Duration in minutes
    due_date = db.Column(db.Date)
    completed = db.Column(db.Boolean, default=False)
    completed_date = db.Column(db.DateTime)
    points = db.Column(db.Integer, default=0)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f"Task('{self.title}', due_date='{self.due_date}', completed={self.completed})"
    
    def mark_completed(self):
        """Mark the task as completed and calculate points"""
        if not self.completed:
            self.completed = True
            self.completed_date = datetime.utcnow()
            
            # Calculate points based on task duration
            # 15 min = 5 points, 30 min = 15 points, 60 min = 30 points
            if self.duration == 15:
                self.points = 5
            elif self.duration == 30:
                self.points = 15
            elif self.duration == 60:
                self.points = 30
            else:
                # For custom durations, calculate points proportionally
                self.points = max(1, int(self.duration * 0.5))
                
            return True
        return False