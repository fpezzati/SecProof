# Security Proof of Concept
This is a sandbox to use AOP in a javaSE webapp to run on Tomcat. I also add auth by Google and JWT.

## Invalid tokens will be stored in a blacklist
In this token scenario back-end is responsible to keep a blacklist about tokens who become invalid because of a logout or password change.

## Token expiration
Back-end will generate long-living tokens. Token refresh is out of scope here.

## Logout and password change
Blacklist should provide consistent tool to explicit invalidate token when user call for a logout or do a password change. Solution drawback is blacklist burden of course. You have to make it HA if you want blacklist works in a clustered environment.