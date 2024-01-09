---
page_type: sample
extensions:
    services: active-directory
    platforms: Java
    endpoint: Microsoft identity platform
    author: bgavrilMS
    level: 300
    client: Java console daemon app
    service: Microsoft Graph
languages:
  - java
products:
  - azure
  - ms-graph
  - entra
description: "This sample demonstrates how run a daemon console app, with an in-memory token cache with size eviction, to get an access token for many tenants to call Microsoft Graph using MSAL4J."
urlFragment: msal-java-client-credential-secret
---

# MSAL Java sample demonstrating how a daemon console application can call Microsoft Graph using its own identity

## About this sample

### Overview

This app demonstrates how to use the [Microsoft identity platform](http://aka.ms/aadv2) to access the data of Microsoft business customers in a long-running, non-interactive process. It uses the [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) to acquire an [access token](https://learn.microsoft.com/azure/active-directory/develop/access-tokens), which it then uses to call [Microsoft Graph](https://learn.microsoft.com/graph/overview) and accesses organizational data. The sample utilizes the [OAuth 2 client credentials grant](https://learn.microsoft.com/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow) and a secret value configured in Azure to obtain an access token for calls to Microsoft Graph.

This sample builds upon the [MSAL Java daemon sample](../msal-client-credential-secret) and adds the following feature:

- A custom token cache implementation that uses an in-memory cache with size eviction, based on [Google Guava cache](https://github.com/google/guava/wiki/CachesExplained).

## Prerequisites

To use the sample, make sure that you have the following tools installed:

- [JDK Version 8](https://jdk.java.net/8/) (or later)
- [Maven 3](https://maven.apache.org/download.cgi)

## Running the sample

Prior to running the sample, ensure that you've configured your application properties in `src/main/resources/application.properties`. The following are required:

| Property | Description |
|:---------|:------------|
| `TENANT_ID` | The unique GUID for your Microsoft Entra tenant. |
| `CLIENT_ID` | The unique GUID for your application, registered in your Microsoft Entra tenant. |
| `SECRET` | The secret configured for the client through the Microsoft Entra admin center or via the Azure CLI. Make sure to use the client secret **value** and not the ID. |

>**Note**
>If you do not have an application registered, refer to [Quickstart: Register an application with the Microsoft identity platform](https://learn.microsoft.com/azure/active-directory/develop/quickstart-register-app).

To run the sample, navigate to the folder after cloning the repository:

```powershell
cd '.\1. Server-Side Scenarios\msal-client-credential-secret-high-availability\'
```

Build the project using Maven:

```powershell
mvn clean package
```

>**Note**
>If you are getting a "No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?", make sure that your `JAVA_HOME` environment variable points to the installed JDK and that you have installed the **Java Development Kit (JDK)** and not the Java Runtime Environment (JRE).

After the build is complete, navigate to the `target` folder and execute the sample with:

```powershell
java -jar  .\msal-client-credential-secret-1.0.0.jar
```

## Community help and support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) to get support from the community.
Ask your questions on Stack Overflow first and [browse existing issues](https://github.com/Azure-Samples/ms-identity-msal-java-samples/issues) to see if someone has asked your question before.

Make sure that your questions or comments are tagged with `msal` and `java`.

If you find a bug in the sample, please raise the issue on [GitHub Issues](../../issues).

If you find a bug in msal4j, please raise the issue on [MSAL4J GitHub Issues](https://github.com/AzureAD/microsoft-authentication-library-for-java/issues).

To provide a recommendation, visit the following [User Voice page](https://feedback.azure.com/forums/169401-azure-active-directory).

## Contributing

If you'd like to contribute to this sample, see [CONTRIBUTING.MD](/CONTRIBUTING.md).

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information, see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## More information

For more information, see:

- [MSAL4J documentation](https://learn.microsoft.com/entra/msal/java/).
- [Permissions and Consent](https://learn.microsoft.com/azure/active-directory/develop/v2-permissions-and-consent)
- [OAuth 2 client credentials grant](https://learn.microsoft.com/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow)
- [Quickstart: Register an application with the Microsoft identity platform](https://learn.microsoft.com/azure/active-directory/develop/quickstart-register-app)
- [Quickstart: Configure a client application to access web APIs](https://learn.microsoft.com/azure/active-directory/develop/quickstart-configure-app-access-web-apis)
- [Documentation for Microsoft identity platform](https://aka.ms/aadv2)
- [Other samples for Microsoft identity platform](https://aka.ms/aaddevsamplesv2)
