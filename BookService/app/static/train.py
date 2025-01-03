import numpy as np
import tensorflow as tf
from tensorflow.keras.applications.resnet50 import ResNet50, preprocess_input
from tensorflow.keras.preprocessing import image
import faiss
import os
import aiosqlite
import asyncio
import logging

# Thiết lập logging
logging.basicConfig(level=logging.WARNING, format='%(asctime)s - %(levelname)s - %(message)s')

# Định nghĩa kích thước ảnh
img_width, img_height, _ = 224, 224, 3

# Khởi tạo mô hình ResNet50
base_model = ResNet50(weights='imagenet', include_top=False, input_shape=(img_width, img_height, 3))
base_model.trainable = False

# Thêm Layer Embedding
model = tf.keras.Sequential([
    base_model,
    tf.keras.layers.GlobalMaxPooling2D()
])

# Hàm để lấy embedding của ảnh
def get_embedding(model, img_path):
    try:
        img = image.load_img(img_path, target_size=(img_width, img_height))
        x = image.img_to_array(img)
        x = np.expand_dims(x, axis=0)
        x = preprocess_input(x)
        embedding = model.predict(x).reshape(-1)
        return embedding
    except Exception as e:
        logging.error(f"Error processing image {img_path}: {e}")
        return None


# Hàm để chuẩn hóa embedding
def normalize_embedding(embedding):
    norm = np.linalg.norm(embedding)
    return embedding / norm

# Khởi tạo FAISS index
d = 2048  # Kích thước của embedding từ ResNet50 với GlobalMaxPooling2D
index = faiss.IndexFlatIP(d)  # Sử dụng Inner Product thay vì L2

# Hàm để lưu embedding vào SQLite
async def insert_embedding(image_name, embedding, faiss_id, db_conn):
    try:
        await db_conn.execute("INSERT INTO embeddings (image_name, embedding, faiss_id) VALUES (?, ?, ?)",
                              (image_name, embedding.tobytes(), faiss_id))
        await db_conn.commit()
    except Exception as e:
        logging.error(f"Failed to insert embedding for image {image_name}: {e}")

# Hàm bất đồng bộ để xử lý ảnh
async def process_image(img_name, model, index, db_conn, image_folder):
    img_path = os.path.join(image_folder, img_name)

    # Kiểm tra xem tệp ảnh có tồn tại không
    if not os.path.exists(img_path):
        logging.warning(f"File {img_name} does not exist, skipping...")
        return

    embedding = get_embedding(model, img_path)
    if embedding is not None:
        normalized_embedding = normalize_embedding(embedding)
        # Thêm embedding vào FAISS index
        faiss_id = index.ntotal
        index.add(np.array([normalized_embedding], dtype=np.float32))

        # Lưu thông tin meta và embedding vào SQLite
        await insert_embedding(img_name, normalized_embedding, faiss_id, db_conn)

# Hàm chính để xử lý tất cả ảnh và lưu trữ embedding
async def main():
    # Kết nối SQLite
    async with aiosqlite.connect('image_embeddings.db') as db_conn:
        await db_conn.execute('''
            CREATE TABLE IF NOT EXISTS embeddings (
                id INTEGER PRIMARY KEY,
                image_name TEXT,
                embedding BLOB,
                faiss_id INTEGER
            )
        ''')
        await db_conn.commit()

        # Đường dẫn đến thư mục chứa các hình ảnh
        image_folder = 'book_img'

        # Lấy danh sách các tệp ảnh
        image_files = [f for f in os.listdir(image_folder) if f.lower().endswith(('png', 'jpg', 'jpeg', 'webp'))]

        tasks = [process_image(img_name, model, index, db_conn, image_folder) for img_name in image_files]
        await asyncio.gather(*tasks)

        # Lưu FAISS index
        faiss.write_index(index, 'faiss_index.bin')

        # Lưu model
        model.save('embedding_model.h5')

# Chạy chương trình chính
if __name__ == "__main__":
    asyncio.run(main())
