from flask import Blueprint, request, jsonify
from app.services.data_service import (
    get_embedding_from_file,
    search_similar_images,
    get_image_names_from_db,
    get_books_from_images,
)
import tensorflow as tf
import faiss

data_bp = Blueprint('data', __name__, url_prefix='/data')

@data_bp.route('/search', methods=['POST'])
def search_books():
    if 'image' not in request.files:
        return jsonify({"error": "Image file is required"}), 400

    image_file = request.files['image']

    if image_file.mimetype not in ['image/jpeg', 'image/png']:
        return jsonify({"error": "Invalid image format. Only JPEG and PNG are allowed."}), 400

    model = tf.keras.models.load_model('app/static/embedding_model.h5')
    index = faiss.read_index('app/static/faiss_index.bin')

    embedding = get_embedding_from_file(model, image_file)

    indices = search_similar_images(embedding, index, k=5)

    image_names = get_image_names_from_db(indices)

    books = get_books_from_images(image_names)

    return jsonify(books)
