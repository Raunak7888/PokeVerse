# Authentication Service Configuration

This file contains the necessary configuration properties for setting up the **Authentication Service** in a Spring Boot application. Ensure you provide valid values before running the service.

---
```
spring.application.name=authentication

spring.security.oauth2.client.registration.google.client-id=
spring.security.oauth2.client.registration.google.client-secret=
spring.security.oauth2.client.registration.google.scope= openid, profile, email

spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

secret=

```
---
## 🔧 Application Settings

| Property | Description | Example |
|----------|------------|---------|
| `spring.application.name` | Defines the name of the Spring Boot application. | `authentication` |

---

## 🔐 Google OAuth 2.0 Configuration

| Property | Description | Example |
|----------|------------|---------|
| `spring.security.oauth2.client.registration.google.client-id` | Google OAuth Client ID. | `your-google-client-id` |
| `spring.security.oauth2.client.registration.google.client-secret` | Google OAuth Client Secret. | `your-google-client-secret` |
| `spring.security.oauth2.client.registration.google.scope` | Permissions required for authentication. | `openid, profile, email` |

Make sure you obtain the Client ID and Secret from the [Google Developer Console](https://console.cloud.google.com/).

---

## 🗄️ Database Configuration (PostgreSQL)

| Property | Description | Example |
|----------|------------|---------|
| `spring.datasource.url` | JDBC URL for the PostgreSQL database. | `jdbc:postgresql://localhost:5432/authdb` |
| `spring.datasource.username` | Database username. | `your-db-username` |
| `spring.datasource.password` | Database password. | `your-db-password` |
| `spring.datasource.driver-class-name` | PostgreSQL driver class. | `org.postgresql.Driver` |
| `spring.jpa.database-platform` | Hibernate dialect for PostgreSQL. | `org.hibernate.dialect.PostgreSQLDialect` |
| `spring.jpa.hibernate.ddl-auto` | Strategy for handling database schema updates. | `update` |
| `spring.jpa.show-sql` | Enables SQL query logging. | `true` |

Ensure your PostgreSQL database is running and accessible with the provided credentials.

---

## 🔑 Secret Key

| Property | Description | Example |
|----------|------------|---------|
| `secret` | A secure key used for JWT Token operations. | `your-secure-secret-key` |

💡 **Tip:** Use environment variables or a `.env` file to store sensitive data securely instead of hardcoding them.

---

## 🚀 Running the Application

To start the Spring Boot application:

```sh
mvn spring-boot:run
```

