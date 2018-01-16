# Architecture

It's a standalone Java application basic multi-tier architecture with REST and repository tiers. The service tier
is missing, because there is no business logic in the application. There is a slight deviation to the basic model -
read-only repository interfaces (data access) are part of service layer API. This avoids API duplication and delegating
methods for reading data, while important data-modifying methods are placed in service components.

## Libraries

### Spring

Spring Boot and several other Spring modules are used to build all parts of the application - security, REST API, service layer,
data access layer.

### Java Persistence API (JPA) as ORM

Java Persistence API (Hibernate implementation) is a mature a rather easy way for accessing data from relational
databases. It's very good for fetching associations. Although, it has few design limitation:
* the entity classes must have getter and setter methods which makes them mutable which use to be a cause of bugs,
* the JPA specification doesn't support java.util.Optional<> attributes,
* there is no standard way to turn off the lazy loading feature

There is a viable alternative - Jooq library which works as a type safe SQL with a nice functional fluid API.
But it takes much more code to build a query with several associations.

## Database

### Database engine

The application uses the in-memory H2 database with a web console running at /db path (with "jdbc:h2:mem:appdb"
as JDBC uri). The database requires no installation or configuration, which makes it ideal for demo projects and testing.
To replace it with something more robust, it's necessary to change the JDBC URI and to check SQL scripts (see the chapter about configuration).

### DB Schema

The database schema is maintained by the Flyway migration scripts. The Flyway library takes care of the schema versioning.
The migrations are simple SQL scripts what makes them DB server specific. To support multiple databases, it's necessary
to keep several versions of the scripts or to use Java classes as migrations (with some abstraction layer).

There is an alternative library called Liquibase, which uses DB server agnostic XML for DB migrations, but it's more
difficult to write for simple projects supporting just one DB server.

# Source code

## Formatting

Checkstyle is used to keep a consistent source code style and formatting. The checkstyle configuration is a slightly
modified [Google config](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml).

## Testing

### Unit tests

I tried to cover custom implemented algorithms with JUnit test.

### End to end tests

Each REST endpoint is covered by at least one end-to-end test which proves to correct functionality of the API with valid input. There are
also negative tests for some endpoints (especially for the `/products` endpoints).

### Documentation tests

There are also Spring REST Docs test which generate API documentation. When executed, they make sure all request and response fields
are documented.

# Build system

The application can be built with Maven. Commands to build the application:
```
mvnw clean package
```

# Configuration

The application uses Spring Boot support for [externalized configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
which makes it easy to override the default configuration with a custom one. The default configuration is in the `application.yml`
file. The easiest way to customize it, is to copy it to the directory of the project (the directory you run the application from) and
modify its values. Spring Boot will give it a higher priority.

## JWT RSA keys

To generate new key pair for JWT signing and verification, use openssl:
Generate a 2048-bit RSA private key
`$ openssl genrsa -out rsa-private-key.pem 2048`
Convert private Key to PKCS#8 format (so Java can read it)
`$ openssl pkcs8 -topk8 -inform PEM -outform DER -in rsa-private-key.pem -out rsa-private-key.der -nocrypt`
Output public key portion in DER format (so Java can read it)
`$ openssl rsa -in private_key.pem -pubout -outform DER -out rsa-public-key.der`

# Running the application

Is's a standalone Java application with an embedded Tomcat HTTP server (servlet container). To run it type
```
./mvnw spring-boot:run
```

# REST API

## Conventions

The application uses a common convention for mapping URIs and request methods (GET = read/search, POST = create, PUT = update, DELETE = remove).
Associated objects can be accessed at extended URIs of the base resources, eg. `/products/123/picture`. The application doesn't support
all methods (CRUD) for the associations. The API doesn't support the [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) in the responses,
because I don't find it that useful.

Success response codes:
* 200 OK - for read/search/update responses
* 201 Created - for resource creation - the created resource is returned
* 204 No Content - for deletion responses

Error response codes:
* 400 Bad Request - for validation or invalid input data errors
* 401 Unauthorized - authentication is required
* 403 Forbidden - the caller is not authorized to perform the requested operation
* 404 Not Found - for resource requests with non-existing identifiers
* 500 Internal Server Error - for unexpected system failures

## Querying

The REST API contains advanced querying support. But it's implemented only at the `GET /product` endpoint. It supports
[RSQL](https://github.com/jirutka/rsql-parser) filters, pagination (limit, offset), ordering and association fetching.

## Validation

For basic validation, the beans validation annotations are used (such as `@NotNull`). More complex validations are in controllers
or in services.

## Picture upload

The REST API supports picture upload in form of a Base64 encoded string. Pictures can be uploaded either as associated objects of products
and customers or separately at `POST /pictures`. When uploaded separately, a reference code is returned, which can be used to associate
the uploaded picture with a product or a customer. When successfully used, the reference code gets removed from the picture and cannot
be used anymore. The picture size is limited by the Tomcat settings `server.tomcat.max-http-post-size` (number in bytes).

## Documentation

The application build system generates an HTML documentation available at [target/generated-docs/index.html](target/generated-docs/index.html).

## Design decisions

For REST API, DTO classes are used. The downside of this choice is that it's needed to maintain two sets of similar classes (DTOs
and persistent entities), but there are several advantages:
* Request and response classes are part of REST API, so they should not expose internal details (such as DB design or mapping).
* In case of a need for multiple API versions, it's probably a necessity to use DTOs.
* They can keep REST-specific JSON mapping and persisten entities may contain their own mapping (if needed). For example for auditing.
* They can contain documentation (Swagger), which is not appropriate for entities.

# Security

The application accepts two kinds of JWTs - custom ones and OpenID Connect ID tokens.

## Custom tokens

A custom tokens gets you an ADMIN role, so you can access all parts of the REST API. To get a JWT, you need to use
the authentication endpoint `/auth/token` with valid HTTP Basic Authentication credentials (`admin:admin123`).

## External ID tokens

Only ID tokens from Google are supported. Only `profile` and `email` scopes are required.  All external ID tokens grant only the CUSTOMER role.
A new Customer record is created (if not already present in the database) whenever a new valid ID token is used for authentication.

## Design considerations

It would be probably better (easier concept) not to support external ID tokens directly, but to always exchange an ID token
for an equivalent custom JWT (using the token renew endpoint).

The API doesn't support token invalidation, because

# Possible improvements

## Modules

The source code could be split into modules to set boundaries for dependencies:
 - app - application assembly - main jar as a result
 - security - security filters, services and models
 - rest - REST endpoints
 - api - REST endpoint DTOs for possible Java clients
 - service - services and data repositories

I didn't split the code into these modules to keep the project simple.

## Data history

The domain of the application deals with mutable data and it's usually good to keep some kind of history records to be
able to find out who modified what and when. I would probably use some form of Event sourcing and kept a log of
data modifying events.

## Fulltext search

The application could use [Hibernate search](http://hibernate.org/search/) for fulltext search capability. The library returns
whole DB entities as results, not just texts, which makes it easy to use.

# Disclaimer

There are no bigger chunks of copied code except the JpaUtils class taken from the project linked in the source code.
