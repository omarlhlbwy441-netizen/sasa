# Multi-stage Dockerfile for Android APK build and Python Autonomous Agent Backend Server

# Stage 1: Build Android APK
FROM eclipse-temurin:17-jdk AS android-builder

ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

RUN apt-get update && apt-get install -y --no-install-recommends \
    wget unzip git python3 python3-pip curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace
COPY . .

# Install Python requirements
RUN pip3 install --no-cache-dir -r requirements.txt || true

# Environment setup
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=\"-Xmx1536m -XX:MaxMetaspaceSize=512m\" -Dorg.gradle.parallel=false"

EXPOSE 5000

CMD ["python3", "app/server.py"]
