import os
import hashlib
import requests

# 服务器接口URL
BASE_URL = "http://localhost:9090/upload"  # 替换为实际的服务器URL

# 文件路径
file_path = "C:\\Users\\17692\\Downloads\\GamePP_International.exe"  # 替换为实际的文件路径

# 文件信息
file_name = os.path.basename(file_path)
file_size = os.path.getsize(file_path)
file_ext = file_name.split(".")[-1]
mimetype = "application/octet-stream"  # 根据文件类型设置mimetype

# 分块大小
chunk_size = 2 * 1024 * 1024  # 2MB

# 计算文件的MD5
def calculate_md5(file_path):
    hash_md5 = hashlib.md5()
    with open(file_path, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()

file_md5 = calculate_md5(file_path)

# 注册文件
def register_file():
    url = f"{BASE_URL}/register"
    data = {
        "fileMd5": file_md5,
        "fileName": file_name,
        "fileSize": file_size,
        "mimetype": mimetype,
        "fileExt": file_ext,
    }
    response = requests.post(url, data=data)
    return response.json()

# 检查分块
def check_chunk(chunk_index):
    url = f"{BASE_URL}/checkchunk"
    data = {
        "fileMd5": file_md5,
        "chunk": chunk_index,
        "chunkSize": chunk_size,
    }
    response = requests.post(url, data=data)
    return response.json()

# 上传分块
def upload_chunk(chunk_index):
    url = f"{BASE_URL}/uploadchunk"
    with open(file_path, "rb") as f:
        f.seek(chunk_index * chunk_size)
        chunk_data = f.read(chunk_size)

    files = {
        "file": (f"chunk_{chunk_index}", chunk_data, mimetype),
    }
    data = {
        "fileMd5": file_md5,
        "chunk": chunk_index,
    }
    response = requests.post(url, files=files, data=data)
    return response.json()

# 合并分块
def merge_chunks():
    url = f"{BASE_URL}/mergechunks"
    data = {
        "fileMd5": file_md5,
        "fileName": file_name,
        "fileSize": file_size,
        "mimetype": mimetype,
        "fileExt": file_ext,
    }
    response = requests.post(url, data=data)
    return response.json()

if __name__ == "__main__":
    # Step 1: 注册文件
    print("Registering file...")
    register_response = register_file()
    print(register_response)

    # Step 2: 上传文件分块
    total_chunks = (file_size + chunk_size - 1) // chunk_size
    for chunk_index in range(total_chunks):
        print(f"Checking chunk {chunk_index}...")
        check_response = check_chunk(chunk_index)
        print(check_response)

        if not check_response.get("id"):
            print(f"Uploading chunk {chunk_index}...")
            upload_response = upload_chunk(chunk_index)
            print(upload_response)

    # Step 3: 合并文件
    print("Merging chunks...")
    merge_response = merge_chunks()
    print(merge_response)
