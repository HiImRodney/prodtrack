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
        return f"Task('{self.title}', due: {self.due_date}, completed: {self.completed})"
    
    def mark_completed(self):
        """Mark the task as completed and calculate points"""
        self.completed = True
        self.completed_date = datetime.utcnow()
        
        # Calculate points based on duration
        if self.duration == 15:
            self.points = 5
        elif self.duration == 30:
            self.points = 10
        elif self.duration == 60:
            self.points = 20
        else:
            self.points = self.duration // 5  # Default formula for other durations