---
services: active-directory
platforms: Java
endpoint: Microsoft identity platform
page_type: sample
author: bogavril
level: 300
client: Java console daemon app
service: Microsoft Graph
languages:
  - java
products:
  - azure
  - azure-active-directory
  - java
  - office-ms-graph
description: "This sample demonstrates how run a daemon console app, with an in-memory token cache with size eviction, to get an access token for many tenants to call Microsoft Graph using MSAL4J."
---

# MSAL Java sample demonstrating how a daemon console application can call Microsoft Graph using its own identity

## About this sample

### Overview

This app demonstrates how to use the [Microsoft identity platform](http://aka.ms/aadv2) to access the data of Microsoft business customers in a long-running, non-interactive process. It uses the [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) to acquire an [access token](https://learn.microsoft.com/azure/active-directory/develop/access-tokens), which it then uses to call [Microsoft Graph](https://learn.microsoft.com/graph/overview) and accesses organizational data. The sample utilizes the [OAuth 2 client credentials grant](https://learn.microsoft.com/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow) and a secret value configured in Azure to obtain an access token for calls to Microsoft Graph.

This sample builds upon the [MSAL Java daemon sample](../msal-client-credential-secret) and adds the following feature:

- A custom token cache implementation that uses an in-memory cache with size eviction, based on [Google Guava cache](https://github.com/google/guava/wiki/CachesExplained).


## Community Help and Support

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

- MSAL4J [conceptual documentation](https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki).
- [Permissions and Consent](https://docs.microsoft.com/azure/active-directory/develop/v2-permissions-and-consent)
- [OAuth 2 client credentials grant](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow)
- [Quickstart: Register an application with the Microsoft identity platform](https://docs.microsoft.com/azure/active-directory/develop/quickstart-register-app)
- [Quickstart: Configure a client application to access web APIs](https://learn.microsoft.com/azure/active-directory/develop/quickstart-configure-app-access-web-apis)
- [Documentation for Microsoft identity platform](https://aka.ms/aadv2)
- [Other samples for Microsoft identity platform](https://aka.ms/aaddevsamplesv2)