# Security Proof of Concept
Here I am exploring Apache Shiro to do authentication and authorization. My aim is to provide a Shiro based solution who can authenticate users who provide the proper JWT token and give them permissions to interact with resources based on their role.

## Authenticating by a JWT token

## Authorize users
Shiro provides a great permission based system to provide grants to users in a very fine grained way. Consider this shiro.ini snippet:

```
[main]
authc = org.apache.shiro.web.filter.authc.FormAuthenticationFilter
rest = org.apache.shiro.web.filter.authz.HttpMethodPermissionFilter
authc.loginUrl = /login.html

[users]
user1 = pwd1, admin
user2 = pwd2, dataprovider
user3 = pwd3, mantainer
user4 = pwd4, jrmantainer

[roles]
admin = *
dataprovider = resource:read
mantainer = resource:*
jrmantainer = resource:read, resource:update

[urls]
/index.html = anon
/srv/login = anon
/srv/boundary/resourceA = authc, perms[resource:read]
/srv/boundary/resourceB = authc, perms[resource:write]
/srv/boundary/resourceC = authc, perms[critical:write]

/srv/dummy = authc, rest[resource]

/srv/boundary/** = authc
/login.html = authc
```

Permissions are expressed by semicolon separated values, where the most left value is gerarchically more important than the right one. For example the `mantainer` is able to do everithing about `resource` permission while the `jrmantainer` can only read and update resources.

Shiro's `authc` filter implements a first check where only authenticated users can pass.

Thanks to the Shiro's `rest` filter we can map permissions to HTTP verbs. In the `shiro.ini`, url `/srv/dummy` is bound to Shiro's `authc` and `rest` filters. `rest` checks about user's request HTTP verb mapping to grants this way (I only mention the most relevant ones. see [docs] (https://shiro.apache.org/static/1.3.2/apidocs/org/apache/shiro/web/filter/authz/HttpMethodPermissionFilter.html) for a complete map):

| HTTP VERB | GRANT's NAME |
|:----------|:-------------|
| POST      | create       |
| GET       | read         |
| PUT       | update       |
| DELETE    | delete       |

So `mantainer` users will be able to do GET, POST, PUT and DELETE while `jrmantainer` users will be able to do only GET and PUT because they are allowed to do `resource:read` and `resource:write` only.

