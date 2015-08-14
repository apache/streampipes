FROM alpine

RUN apk add --update openjdk7-jre wget ca-certificates && \
	cd /tmp && \
	wget 	"https://circle-artifacts.com/gh/andyshinn/alpine-pkg-glibc/6/artifacts/0/home/ubuntu/alpine-pkg-glibc/packages/x86_64/glibc-2.21-r2.apk" \
				"https://circle-artifacts.com/gh/andyshinn/alpine-pkg-glibc/6/artifacts/0/home/ubuntu/alpine-pkg-glibc/packages/x86_64/glibc-bin-2.21-r2.apk" && \
	apk add --allow-untrusted glibc-2.21-r2.apk glibc-bin-2.21-r2.apk && \
	/usr/glibc/usr/bin/ldconfig /lib /usr/glibc/usr/lib && \
	echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf

RUN wget http://ftp.fau.de/apache/zookeeper/zookeeper-3.3.6/zookeeper-3.3.6.tar.gz
RUN rm /tmp/* /var/cache/apk/*
RUN apk del wget ca-certificates

RUN tar -zxvf zookeeper-3.3.6.tar.gz

EXPOSE 2181

ADD /zoo.cfg ./zookeeper-3.3.6/conf/zoo.cfg

ENTRYPOINT ["./zookeeper-3.3.6/bin/zkServer.sh", "start-foreground"]
