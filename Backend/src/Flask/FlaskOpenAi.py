import os
from flask import Flask, request, jsonify
from openai import OpenAI

app = Flask(__name__)

client = OpenAI(api_key="OPENAI_API_KEY")

@app.route('/transcribe', methods=['POST'])
def transcribe_audio():
    try:
        print("Received request:")
        print("Request Method:", request.method)
        print("Request Headers:", request.headers)
        print("Request Files:", request.files)
        print("Request Form Data:", request.form)
        
        if 'file' not in request.files:
            return jsonify({"error": "No file part"}), 400
    
        file = request.files['file']
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400
    
        if file and allowed_file(file.filename):
            file_path = "/tmp/uploaded_audio.m4a"
            file.save(file_path)

            with open(file_path, "rb") as audio_file:
                transcription = client.audio.transcriptions.create(
                    model="whisper-1", 
                    file=audio_file
                )
                result = {"transcript": transcription.text}
            
            os.remove(file_path)
            return jsonify(result), 200
        else:
            return jsonify({"error": "File type not allowed"}), 400
    except Exception as e:
        return jsonify({"error": str(e)}), 500    

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() == 'm4a'

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)