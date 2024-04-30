import os
from flask import Flask, request, jsonify
import requests

app = Flask(__name__)

OPENAI_API_KEY = "sk-proj-TkbgS5DiXueXRs1Y7ZtMT3BlbkFJXYy1tmPL0qMzlOh1Sa8w"

@app.route('/transcribe', methods=['POST'])
def transcribe_audio():
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file and allowed_file(file.filename):
        file_path = "/tmp/uploaded_audio.mp3"
        file.save(file_path)

        files = {
            'file': ('file', open(file_path, 'rb')),
            'model': (None, 'whisper-1')
        }
        
        response = requests.post(
            'https://api.openai.com/v1/audio/transcriptions',
            headers={'Authorization': f'Bearer {OPENAI_API_KEY}'},
            files=files
        )
        
        result = response.json()

        print("Received response from OpenAI:")
        print("Response Status Code:", response.status_code)
        print("Response Body:", result)
        
        os.remove(file_path)

        if response.status_code == 200:
            return jsonify(result), 200
        else:
            return jsonify({"error": "Failed to transcribe audio", "details": result}), response.status_code
    else:
        return jsonify({"error": "File type not allowed"}), 400

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() == 'mp3'

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)