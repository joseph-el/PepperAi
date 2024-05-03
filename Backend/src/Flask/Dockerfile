FROM python:3.8-slim

WORKDIR /app

COPY FlaskAssemblyAi.py FlaskOpenAi.py requirements.txt /app/

RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 5000

CMD ["python3", "FlaskAssemblyAi.py"]