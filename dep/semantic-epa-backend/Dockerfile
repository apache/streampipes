FROM tomcat:8-jre8
EXPOSE 8080

ADD ./de.fzi.cep.sepa/semantic-epa-server/0.0.1-SNAPSHOT/semantic-epa-server-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/semantic-epa-server.war
ADD /openrdf-sesame.war /usr/local/tomcat/webapps/openrdf-sesame.war
ADD /openrdf-workbench.war /usr/local/tomcat/webapps/openrdf-workbench.war

ADD /catalina.properties /usr/local/tomcat/conf/catalina.properties
ADD /server.xml /usr/local/tomcat/conf/server.xml

CMD ["catalina.sh", "run"]
