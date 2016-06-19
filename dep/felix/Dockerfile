FROM anapsix/alpine-java

ENV felix_version 5.0.0
ENV felix_package=org.apache.felix.main.distribution-${felix_version}.tar.gz
ENV felix_base http://repo1.maven.org/maven2/org/apache/felix
ENV felix_configadmin 1.8.8
ENV felix_eventadmin 1.4.2
ENV felix_fileinstall 3.5.0
ENV felix_http_api 2.3.2
ENV felix_http_jetty 3.0.2
ENV felix_http_servlet 1.1.0
ENV felix_http_whiteboard 2.3.2
ENV felix_metatype 1.0.12
ENV felix_log 1.0.1
ENV felix_scr 2.0.2
ENV felix_webconsole 4.2.8
ENV felix_webconsole_ds 2.0.2
ENV felix_webconsole_event 1.1.2

ADD ${felix_base}/org.apache.felix.main.distribution/${felix_version}/${felix_package} /tmp/
RUN mkdir -p /opt/felix && \
    cd /opt/felix && \
    tar xvzf /tmp/${felix_package} && \
    ln -s /opt/felix/felix-framework-${felix_version} /opt/felix/current

## Initial plugin set, this will get us up with a webconle, logging, scr, and web
ADD ${felix_base}/org.apache.felix.configadmin/${felix_configadmin}/org.apache.felix.configadmin-${felix_configadmin}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.eventadmin/${felix_eventadmin}/org.apache.felix.eventadmin-${felix_eventadmin}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.fileinstall/${felix_fileinstall}/org.apache.felix.fileinstall-${felix_fileinstall}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.http.api/${felix_http_api}/org.apache.felix.http.api-${felix_http_api}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.http.jetty/${felix_http_jetty}/org.apache.felix.http.jetty-${felix_http_jetty}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.http.servlet-api/${felix_http_servlet}/org.apache.felix.http.servlet-api-${felix_http_servlet}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.http.whiteboard/${felix_http_whiteboard}/org.apache.felix.http.whiteboard-${felix_http_whiteboard}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.metatype/${felix_metatype}/org.apache.felix.metatype-${felix_metatype}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.log/${felix_log}/org.apache.felix.log-${felix_log}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.scr/${felix_scr}/org.apache.felix.scr-${felix_scr}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.webconsole/${felix_webconsole}/org.apache.felix.webconsole-${felix_webconsole}-all.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.webconsole.plugins.ds/${felix_webconsole_ds}/org.apache.felix.webconsole.plugins.ds-${felix_webconsole_ds}.jar /opt/felix/current/bundle/
ADD ${felix_base}/org.apache.felix.webconsole.plugins.event/${felix_webconsole_event}/org.apache.felix.webconsole.plugins.event-${felix_webconsole_event}.jar /opt/felix/current/bundle/

ADD http://central.maven.org/maven2/com/eclipsesource/jaxrs/jersey-all/2.22.1/jersey-all-2.22.1.jar /opt/felix/current/bundle/
ADD ./de.fzi.cep.sepa/semantic-epa-appstore/0.0.2-SNAPSHOT/semantic-epa-appstore-0.0.2-SNAPSHOT.jar  /opt/felix/current/bundle/
ADD ./de.fzi.cep.sepa/semantic-epa-appstore-deployer/0.0.2-SNAPSHOT/semantic-epa-appstore-deployer-0.0.2-SNAPSHOT.jar  /opt/felix/current/bundle/


RUN echo 'felix.cm.dir=/opt/felix/current/configs' >> /opt/felix/current/conf/config.properties
RUN echo 'felix.fileinstall.start.level=2' >> /opt/felix/current/conf/config.properties
RUN echo 'org.osgi.framework.startlevel.beginning=2' >> /opt/felix/current/conf/config.properties
RUN echo 'org.osgi.framework.bootdelegation=sun.*,com.sun.*' >> /opt/felix/current/conf/config.properties
RUN mkdir -p /opt/felix/current/configs

#
# Finally expose our webports so you can use this with something like
# https://github.com/jwilder/nginx-proxy
#

EXPOSE 8080 8000
VOLUME ["/opt/felix/current/configs", "/opt/felix/current/load" ]

ENV JVM_OPTIONS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

WORKDIR /opt/felix/current

CMD exec java $JVM_OPTIONS -jar bin/felix.jar