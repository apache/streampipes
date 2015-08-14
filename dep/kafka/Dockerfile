FROM alpine
RUN apk --update add openjdk7-jre	bash  && \
		wget "http://artfiles.org/apache.org/kafka/0.8.2.0/kafka_2.10-0.8.2.0.tgz" && \
		tar -zxvf kafka_2.10-0.8.2.0.tgz && \
		rm kafka_2.10-0.8.2.0.tgz  && \
		apk del wget


EXPOSE 9092

ENTRYPOINT ["./kafka_2.10-0.8.2.0/bin/kafka-server-start.sh", "./kafka_2.10-0.8.2.0/config/server.properties"]
