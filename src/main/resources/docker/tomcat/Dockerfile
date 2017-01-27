FROM tomcat:8-jre8

ARG WEBAPP

ADD settings.xml /usr/local/tomcat/conf/
ADD tomcat-users.xml /usr/local/tomcat/conf/

COPY $WEBAPP $CATALINA_HOME/webapps
COPY setenv.sh $CATALINA_HOME/bin