from productivity_tracker import db
from datetime import datetime

class Reward(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    description = db.Column(db.String(500))
    points_cost = db.Column(db.Integer, nullable=False, default=100)
    is_redeemed = db.Column(db.Boolean, default=False)
    redeemed_date = db.Column(db.DateTime)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    def __repr__(self):
        return f'<Reward {self.name}>'
    
    def redeem(self, points_available):
        """Redeem the reward if enough points are available"""
        if points_available >= self.points_cost and not self.is_redeemed:
            self.is_redeemed = True
            self.redeemed_date = datetime.utcnow()
            return True, points_available - self.points_cost
        return False, points_available