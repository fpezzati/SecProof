FROM tomcat:8-jre8

ARG webapp

ADD ./src/main/resources/docker/tomcat/settings.xml /usr/local/tomcat/conf/
ADD ./src/main/resources/docker/tomcat/tomcat-users.xml /usr/local/tomcat/conf/

ADD ./target/$webapp $CATALINA_HOME/webapps

EXPOSE 8080