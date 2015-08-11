FROM tomcat:8-jre8
EXPOSE 8080

ADD /semantic-epa-backend.war /usr/local/tomcat/webapps/semantic-epa-backend.war
ADD /openrdf-sesame.war /usr/local/tomcat/webapps/openrdf-sesame.war
ADD /openrdf-workbench.war /usr/local/tomcat/webapps/openrdf-workbench.war

ADD /catalina.properties /usr/local/tomcat/conf/catalina.properties
ADD /server.xml /usr/local/tomcat/conf/server.xml

CMD ["catalina.sh", "run"]
