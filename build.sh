#!/usr/bin/env bash
./gradlew clean assemble
docker build -t kafka-monitoring .
docker tag kafka-monitoring:latest xushikuan/kafka-monitoring:$1
docker push xushikuan/kafka-monitoring:$1