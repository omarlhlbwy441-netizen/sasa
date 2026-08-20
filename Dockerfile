# Multi-stage Dockerfile for Android APK build and Python Autonomous Agent Backend Server

# Stage 1: Build Android APK
FROM eclipse-temurin:17-jdk AS android-builder

ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

RUN apt-get update && apt-get install -y --no-install-recommends \
    wget unzip git curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace
COPY . .

RUN chmod +x ./gradlew || true

# Stage 2: Lean Autonomous Python Backend Server
FROM python:3.10-slim AS runner

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt || true

COPY app/ ./app/
COPY metadata.json .

ENV WORKSPACE_DIR=/app
ENV PORT=5000
EXPOSE 5000

CMD ["python3", "app/server.py"]

