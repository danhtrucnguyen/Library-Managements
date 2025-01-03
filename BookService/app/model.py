from app.db import db

class Book(db.Model):
    __tablename__ = 'book'

    id = db.Column(db.Integer, primary_key=True)
    author = db.Column(db.String(255), nullable=True)
    average_rating = db.Column(db.Float, nullable=True)
    book_name = db.Column(db.String(255), nullable=True)
    category = db.Column(db.String(255), nullable=True)
    created_date = db.Column(db.DateTime, nullable=True)
    description = db.Column(db.Text, nullable=True)
    discount = db.Column(db.Integer, nullable=True)
    discount_price = db.Column(db.Float, nullable=True)
    formatted_discount_price = db.Column(db.String(255), nullable=True)
    formatted_price = db.Column(db.String(255), nullable=True)
    image = db.Column(db.String(255), nullable=True)
    isbn = db.Column(db.String(255), nullable=True)
    is_active = db.Column(db.Boolean, nullable=True)
    price = db.Column(db.Float, nullable=True)
    publisher = db.Column(db.String(255), nullable=True)
    sold = db.Column(db.Integer, nullable=True)
    stock = db.Column(db.Integer, nullable=True)

    def to_dict(self):
        return {
            "id": self.id,
            "author": self.author,
            "average_rating": self.average_rating,
            "book_name": self.book_name,
            "category": self.category,
            "created_date": self.created_date.isoformat() if self.created_date else None,
            "description": self.description,
            "discount": self.discount,
            "discount_price": self.discount_price,
            "formatted_discount_price": self.formatted_discount_price,
            "formatted_price": self.formatted_price,
            "image": self.image,
            "isbn": self.isbn,
            "is_active": self.is_active,
            "price": self.price,
            "publisher": self.publisher,
            "sold": self.sold,
            "stock": self.stock,
        }
