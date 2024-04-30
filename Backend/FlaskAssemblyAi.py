import os
from flask import Flask, request, jsonify
import assemblyai as aai

# Server Speech To text Using Flask and Assemblyai IP #

app = Flask(__name__)

aai.settings.api_key = "a955f1cb5e9e4a00b5051df1dd2d746f"
transcriber = aai.Transcriber()

@app.route('/transcribe', methods=['POST'])
def transcribe_audio():
    
    #print("Received request:")
    #print("Request Method:", request.method)
    #print("Request Headers:", request.headers)
    #print("Request Files:", request.files)
    #print("Request Form Data:", request.form)
    
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file and allowed_file(file.filename):
        file_path = "/tmp/uploaded_audio.mp3"
        file.save(file_path)
        config = aai.TranscriptionConfig(speaker_labels=True)
        transcript = transcriber.transcribe(file_path, config)
        result = {
            "transcript": transcript.text,
            "speaker_labels": [{"speaker": utterance.speaker, "text": utterance.text} for utterance in transcript.utterances]
        }
        os.remove(file_path)
        return jsonify(result), 200
    else:
        return jsonify({"error": "File type not allowed"}), 400
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() == 'mp3'
if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)


