#!/bin/bash
cwd=$(pwd)

echo "Installing Python dependencies..."
pip3 install -r requirements.txt > /dev/null

echo "Starting Backend [⏳]..."

cmd1="sudo npx flowise start"

SpeechToTextServer="FlaskAssemblyAi.py"

cmd2="python3 $cwd/$SpeechToTextServer"

run_in_new_terminal() {
    local cmd="$1"
    osascript -e "tell app \"Terminal\"
        activate
        do script \"$cmd; exit\"
    end tell"
}

run_in_new_terminal "$cmd1" &
run_in_new_terminal "$cmd2" &

echo "Backend Started [✅]"