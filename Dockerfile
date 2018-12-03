FROM java:8-jdk
LABEL MAINTAINER="heixiushamao@gmail.com"

ENV JAVA_HOME              /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH                   $PATH:$JAVA_HOME/bin
ENV JAVA_OPTIONS    -Xmx1024m

ENV TIME_ZONE              Asia/Singapore

RUN echo "$TIME_ZONE" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

WORKDIR /app

RUN mkdir database

EXPOSE 8100

ADD build/libs/kafka-monitoring-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT java $JAVA_OPTIONS -Djava.security.egd=file:/dev/./urandom -jar app.jar