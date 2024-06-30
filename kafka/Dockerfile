FROM openjdk:17-bullseye

WORKDIR /

RUN apt update \
    && apt install -y wget tar vim supervisor \
    && apt-get clean
RUN wget https://dlcdn.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz
RUN tar -xzf kafka_2.13-3.7.0.tgz
RUN mv /kafka_2.13-3.7.0 /kafka

WORKDIR /kafka

COPY ./server.properties /kafka/config/server.properties
COPY ./supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 9092

ENTRYPOINT [ "/usr/bin/supervisord" ]