# Security Proof of Concept
This is a sandbox to use AOP in a javaSE webapp to run on Tomcat. I also add auth by Google and JWT.

## No token store. Let the front-end does refresh the token
This is a web token scenario. As title says, back-end does not store the token on logout or password change. Token has a very short life expectation, this will ensure (maybe) that front-end will behave consistently when user change password or perform logout.

### Token expiration
Back-end generates short living tokens and expose an endpoint about refreshing them. It is up to the front-end to refresh it's token. Short live tokens means fast token rotation.

### Logout and password change
Back-end does not provide any token blacklist. Token will early goes invalid by expiration and user will be forced to login again.

### Notice
Remember to provide front-end (the index.html file) with your google-signin-client-id. You must also specify the following properties:
1. jwt.shared.secret (a string)
2. jwt.token.lifetime (a number representing how long in seconds a token should last before expire)
 
example: ... -Djwt.shared.secret=supersecretpassphrase -Djwt.token.lifetime=30