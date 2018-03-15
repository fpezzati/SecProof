FROM tomcat:8-jre8

ARG webapp

ADD settings.xml /usr/local/tomcat/conf/
ADD tomcat-users.xml /usr/local/tomcat/conf/

ADD $webapp $CATALINA_HOME/webapps

EXPOSE 8080