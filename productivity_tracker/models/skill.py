from productivity_tracker import db
from datetime import datetime

class Skill(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(500))
    total_hours = db.Column(db.Float, default=0.0)  # Total hours spent learning
    level = db.Column(db.Integer, default=1)  # Skill level (calculated based on hours)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f"Skill('{self.name}', level={self.level}, hours={self.total_hours})"
    
    def add_practice_time(self, hours):
        """Add practice time and update level"""
        self.total_hours += float(hours)
        self.calculate_level()
        return self.total_hours
    
    def calculate_level(self):
        """Calculate skill level based on total hours
        Using a simplified version of the 10,000 hour rule:
        Level 1: 0-10 hours
        Level 2: 10-25 hours
        Level 3: 25-50 hours
        Level 4: 50-100 hours
        Level 5: 100-200 hours
        Level 6: 200-400 hours
        Level 7: 400-700 hours
        Level 8: 700-1000 hours
        Level 9: 1000-5000 hours
        Level 10: 5000+ hours
        """
        hours = self.total_hours
        
        if hours < 10:
            self.level = 1
        elif hours < 25:
            self.level = 2
        elif hours < 50:
            self.level = 3
        elif hours < 100:
            self.level = 4
        elif hours < 200:
            self.level = 5
        elif hours < 400:
            self.level = 6
        elif hours < 700:
            self.level = 7
        elif hours < 1000:
            self.level = 8
        elif hours < 5000:
            self.level = 9
        else:
            self.level = 10
        
        return self.level