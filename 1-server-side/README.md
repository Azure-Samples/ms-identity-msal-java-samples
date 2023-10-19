# MSAL Java samples demonstrating server-side scenarios

## About these samples

### Overview

In MSAL server-side applications include web apps, web APIs, and long-running daemon services.

These scenarios are generally considered secure enough for MSAL to store credentials, allowing a single MSAL instance to make token requests on behalf of multiple users using the same basic credentials.

A group of web authentication samples using OpenId Connect and the Microsoft Identity platform

1. Use MSAL Java in a web application to sign in users with Azure AD
    - Source code can be found in the [msal-java-webapp-sample](msal-java-webapp-sample) directory, as well as the [README](msal-java-webapp-sample/README.md) for configuring and running the sample
1. Use MSAL Java in a web application to sign in users Azure AD B2C
    - Source code can be found in the [msal-b2c-web-sample](msal-b2c-web-sample) directory, as well as the [README](msal-b2c-web-sample/README.md) for configuring and running the sample
1. Use MSAL Java alongside Spring Security to sign in users with Azure AD
    - Source code can be found in the [msal-spring-security-web-app](msal-spring-security-web-app) directory, as well as the [README](spring-security-web-app/README.md) for configuring and running the sample
1. Use MSAL Java in a web application to sign in users with Azure AD, and obtain an access token for a separate web API
    - Source code can be found in the [msal-web-api-sample](msal-web-api-sample) directory, as well as the [README](msal-web-api-sample/README.md) for configuring and running the sample

How to use the Microsoft Identity platform to access user data in a long-running, non-interactive process:

1. An application which uses the client credentials flow with a certificate to obtain an access token for Microsoft Graph
    - Source code can be found in the [msal-client-credential-certificate](msal-client-credential-certificate) directory, as well as the [README](msal-client-credential-certificate/README.md) for configuring and running the sample
1. An application which uses the client credentials flow with a secret to obtain an access token for Microsoft Graph
    - Source code can be found in the [msal-client-credential-secret](msal-client-credential-secret) directory, as well as the [README](msal-client-credential-secret/README.md) for configuring and running the sample
