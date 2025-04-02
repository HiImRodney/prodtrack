from datetime import date, datetime
from productivity_tracker import db

class Workout(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    date = db.Column(db.Date, default=date.today, nullable=False)
    workout_type = db.Column(db.String(100), nullable=False)  # e.g., Cardio, Strength, Yoga, etc.
    duration = db.Column(db.Integer, nullable=False)  # Duration in minutes
    intensity = db.Column(db.String(50), default='medium')  # low, medium, high
    calories_burned = db.Column(db.Integer, nullable=True)  # Optional
    notes = db.Column(db.String(500))
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f"<Workout {self.workout_type} on {self.date}>"
    
    def get_points(self):
        """Calculate points based on workout duration and intensity
        
        Points system:
        - Base points: 5 points per workout
        - Duration: 1 point per 10 minutes
        - Intensity bonus:
            Low: no bonus
            Medium: +25% points
            High: +50% points
        """
        # Base points
        points = 5
        
        # Duration points
        duration_points = self.duration // 10
        points += duration_points
        
        # Intensity multiplier
        if self.intensity == 'medium':
            points = int(points * 1.25)
        elif self.intensity == 'high':
            points = int(points * 1.5)
            
        return points