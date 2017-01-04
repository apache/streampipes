FROM alpine

RUN apk add --update openjdk7-jre wget ca-certificates && \
	cd /tmp && \
	 wget    "https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.21-r2/glibc-2.21-r2.apk" \
    	 	 "https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.21-r2/glibc-bin-2.21-r2.apk" && \
	apk add --allow-untrusted glibc-2.21-r2.apk glibc-bin-2.21-r2.apk && \
	/usr/glibc/usr/bin/ldconfig /lib /usr/glibc/usr/lib && \
	echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf
	
RUN wget http://www.eu.apache.org/dist/activemq/5.14.3/apache-activemq-5.14.3-bin.tar.gz
RUN	tar -zxvf apache-activemq-5.14.3-bin.tar.gz

RUN rm /tmp/* /var/cache/apk/* && \
	apk del wget ca-certificates


ADD /activemq.xml ./apache-activemq-5.14.3/conf/activemq.xml

EXPOSE 61616


ENTRYPOINT ["./apache-activemq-5.14.3/bin/activemq",  "console"]
