import os
from flask import Flask, request, jsonify
import assemblyai as aai

# Initialize Flask app
app = Flask(__name__)

# Configure AssemblyAI API key
aai.settings.api_key = "a955f1cb5e9e4a00b5051df1dd2d746f"
transcriber = aai.Transcriber()

# Route for handling audio file upload
@app.route('/transcribe', methods=['POST'])
def transcribe_audio():
    # Print request data to console
    print("Received request:")
    print("Request Method:", request.method)
    print("Request Headers:", request.headers)
    print("Request Files:", request.files)
    print("Request Form Data:", request.form)

    # Check if the POST request has the file part
    if 'file' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['file']

    # Check if the file is an MP3
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file and allowed_file(file.filename):
        # Save the uploaded file to a temporary location
        file_path = "/tmp/uploaded_audio.mp3"
        file.save(file_path)

        # Perform transcription
        config = aai.TranscriptionConfig(speaker_labels=True)
        transcript = transcriber.transcribe(file_path, config)

        # Format the transcription response
        result = {
            "transcript": transcript.text,
            "speaker_labels": [{"speaker": utterance.speaker, "text": utterance.text} for utterance in transcript.utterances]
        }

        # Remove the temporary file
        os.remove(file_path)

        return jsonify(result), 200

    else:
        return jsonify({"error": "File type not allowed"}), 400

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() == 'mp3'

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)


