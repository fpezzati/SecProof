# Security Proof of Concept
This is a sandbox to use AOP in a javaSE webapp to run on Tomcat

## Docker notes
Go to root of this project. Build the image by this command:
```
docker build -t secproof -f ./src/main/resources/docker/tomcat/Dockerfile --build-arg webapp=SecProof.war .
```
then run with:
```
docker run -p 8080:8080 secproof
```
Here you go. You have a tomcat image with the app deployed.
