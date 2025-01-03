from app.routes.data_routes import data_bp

def init_routes(app):
    app.register_blueprint(data_bp)
