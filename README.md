# Security Proof of Concept
This is a sandbox to use AOP in a javaSE webapp to run on Tomcat. I also add auth by Google and JWT.

## Token will not be stored. Back-end is responsible about refreshing tokens
In this scenario back-end check if given token is about to expire. A token is about to expire when current time is after token's expiration time minus *idle-time*. *Idle-time* represent how long front-end can stay without interact with back-end. The idea is that while front-end interact with back-end it's token will be refreshed by back-end. If front-end holds on token won't be refresh and expires forcing front-end to go to login. So you don't need to store tokens but you have the burden to check if the given token is about to expire. Front-end will have the burden to update it's token on every interaction with back-end.
Well, I must confess I don't known if this is a good idea.. 

## Logout and password reset 
Short live token (should) ensure that user who would like to log out or reset password will not get a refreshed token, so his/her token will expire shortly forcing a new login.

### Notice
Remember to specify the following properties:
1. jwt.shared.secret (a string)
2. jwt.token.lifetime (a number representing how long in seconds a token should last before expire)
3. jwt.token.idletime (max time in seconds a token can live before being refreshed)
 
example: ... -Djwt.shared.secret=supersecretpassphrase -Djwt.token.lifetime=30 -Djwt.token.idletime=15