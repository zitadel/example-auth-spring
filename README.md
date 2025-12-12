# Spring Boot with ZITADEL

[Spring Boot](https://spring.io/projects/spring-boot) is a powerful, production-ready framework for building Java applications. It adopts an opinionated view of the Spring platform and third-party libraries so you can get started with minimum fuss. Spring Boot provides the tools, libraries, and patterns to build secure web applications efficiently.

To secure such an application, you need a reliable way to handle user logins. For Spring Boot applications, [Spring Security](https://spring.io/projects/spring-security) is the standard and recommended framework for authentication and access control. Think of it as a flexible security guard for your app. This guide demonstrates how to use Spring Security with a Spring Boot application to implement a secure login with ZITADEL.

We'll be using the **OpenID Connect (OIDC)** protocol with the **Authorization Code Flow + PKCE**. This is the industry-best practice for security, ensuring that the login process is safe from start to finish. You can learn more in our [guide to OAuth 2.0 recommended flows](https://zitadel.com/docs/guides/integrate/login/oidc/oauth-recommended-flows).

This example uses **Spring Security**, the standard for Java application security. While ZITADEL doesn't offer a specific SDK for every framework, Spring Security is highly modular and supports OIDC natively. It handles communication with ZITADEL using the powerful OIDC standard to manage the secure PKCE flow automatically.

Check out our Example Application to see it in action.

## Example Application

The example repository includes a complete Spring Boot application, ready to run, that demonstrates how to integrate ZITADEL for user authentication.

This example application showcases a typical web app authentication pattern: users start on a public landing page, click a login button to authenticate with ZITADEL, and are then redirected to a protected profile page displaying their user information. The app also includes secure logout functionality that clears the session and redirects users back to ZITADEL's logout endpoint. All protected routes are automatically secured using Spring Security's filter chain and session management, ensuring only authenticated users can access sensitive areas of your application.

### Prerequisites

Before you begin, ensure you have the following:

#### System Requirements

- Java Development Kit (JDK) 17 or later
- Maven (or use the included `mvnw` wrapper)

#### Account Setup

You'll need a ZITADEL account and application configured. Follow the [ZITADEL documentation on creating applications](https://zitadel.com/docs/guides/integrate/login/oidc/web-app) to set up your account and create a Web application with Authorization Code + PKCE flow.

> **Important:** Configure the following URLs in your ZITADEL application settings:
>
>- **Redirect URIs:** Add `http://localhost:3000/auth/callback` (for development)
>- **Post Logout Redirect URIs:** Add `http://localhost:3000/auth/logout/success` (for development)
>
>  These URLs must exactly match what your Spring Boot application uses. For production, add your production URLs.

 ### Configuration

 To run the application, you first need to copy the `.env.example` file to a new file named `.env` and fill in your ZITADEL application credentials.

 ```dotenv
 # Port number where your Spring Boot server will listen for incoming HTTP requests.
 PORT=3000

 # Session timeout in seconds. Users will be automatically logged out after this
 # duration of inactivity. 3600 seconds = 1 hour.
 SESSION_DURATION=3600

 # Your ZITADEL instance domain URL. Found in your ZITADEL console under
 # instance settings. Include the full https:// URL.
 ZITADEL_DOMAIN="https://your-zitadel-domain"

 # Application Client ID from your ZITADEL application settings.
 ZITADEL_CLIENT_ID="your-client-id"

 # While the Authorization Code Flow with PKCE for public clients
 # does not strictly require a client secret for OIDC specification compliance,
 # Spring Security's default client configuration often expects one.
 # Therefore, please provide a randomly generated string here.
 ZITADEL_CLIENT_SECRET="your-randomly-generated-client-secret"
 ```

 *Note: Unlike the Python example, this Java application derives the callback and logout URLs automatically from the base URL and does not require them to be set in the `.env` file.*

 ### Installation and Running

 Follow these steps to get the application running:

 ```bash
 # 1. Clone the repository
 git clone git@github.com:zitadel/example-spring-boot-auth.git
 cd example-spring-boot-auth

 # 2. Build the project and download dependencies
 mvn clean install

 # 3. Start the development server
 mvn spring-boot:run
 ```

 The application will now be running at `http://localhost:3000`.

 ## Key Features

 ### PKCE Authentication Flow

 The application implements the secure Authorization Code Flow with PKCE (Proof Key for Code Exchange), which is the recommended approach for modern web applications. Spring Security handles the code verifier and challenge generation automatically.

 ### Session Management

 Built-in session management with the servlet container (Tomcat) and Spring Security handles user authentication state across your application, with secure cookie storage.

 ### Route Protection

 Protected routes automatically redirect unauthenticated users to the login flow using the `SecurityFilterChain` configuration, ensuring sensitive areas of your application remain secure.

 ### Logout Flow

 Complete logout implementation that properly terminates both the local session (invalidating the `JSESSIONID`) and the ZITADEL session, with proper redirect handling to the configured success page.

 ## TODOs

 ### 1. Security headers (Spring Security Config)

 **Partially enabled.** Spring Security adds many security headers (like X-Frame-Options and X-Content-Type-Options) by default. However, Content Security Policy (CSP) is often application-specific. Consider configuring it in your `SecurityConfig.java`:

 ```java
 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     http
         // ... existing configuration ...
         .headers(headers -> headers
             .contentSecurityPolicy(csp -> csp
                 .policyDirectives("default-src 'self'; script-src 'self' [https://trusted.cdn.com](https://trusted.cdn.com); object-src 'none'")
             )
         );
     return http.build();
 }
 ```

 At minimum, ensure you have configured:

- `Content-Security-Policy` (CSP)
- `Referrer-Policy`
- `Permissions-Policy`

## Resources

- **Spring Boot Documentation:** <https://docs.spring.io/spring-boot/index.html>
- **Spring Security Documentation:** <https://docs.spring.io/spring-security/reference/index.html>
- **ZITADEL Documentation:** <https://zitadel.com/docs>
