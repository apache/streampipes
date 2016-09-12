FROM alpine
RUN apk --update add openjdk7-jre	bash  && \
		wget "http://artfiles.org/apache.org/kafka/0.10.0.1/kafka_2.10-0.10.0.1.tgz" && \
		tar -zxvf kafka_2.10-0.10.0.1.tgz && \
		rm kafka_2.10-0.10.0.1.tgz  && \
		apk del wget

ADD /server.properties ./server.properties

EXPOSE 9092

ENTRYPOINT ["./kafka_2.10-0.10.0.1/bin/kafka-server-start.sh", "./server.properties"]
