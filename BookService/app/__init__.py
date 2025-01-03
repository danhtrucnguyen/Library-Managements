from flask import Flask
from app.config import Config
from app.db import db
from app.routes import init_routes
from flask_cors import CORS


def create_app():
    app = Flask(__name__)
    CORS(app)
    app.config.from_object(Config)

    db.init_app(app)
    init_routes(app)

    return app
