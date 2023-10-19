---
languages:
  - java
products:
  - msal-java
  - azure
  - azure-active-directory
  - entra
page_type: sample
urlFragment: introduction-msal-java
description: "Securing Java apps using the Microsoft Identity platform and MSAL Java"
---

# Securing Java apps using the Microsoft Identity platform and MSAL Java

## About these samples

### Scenarios

The Microsoft Authentication Library (MSAL) enables developers to acquire tokens from the Microsoft identity platform, allowing applications to authenticate users and access secured web APIs. It can be used to provide secure access to Microsoft Graph, other Microsoft APIs, third-party web APIs, or your own web API.

This collection of samples covers a number of scenarios where MSAL Java can be used to secure Java applications, and is meant to build an understanding of [MSAL Java](https://learn.microsoft.com/entra/msal/java/) and demonstrate how to integrate the library into your applications.

### Prerequisites

- [JDK Version 8](https://jdk.java.net/8/)
- [Maven 3](https://maven.apache.org/download.cgi)

These are the basic prerequisites for each sample, though each sample may specify more in their own `README`.

### Structure of the repository

This repository contains scenario-specific samples and comprehensive tutorials. We recommend starting with the [MSAL Java documentation](https://learn.microsoft.com/entra/msal/java/) to get familiar with MSAL Java, and then try out the samples that best fit your use case.

Chapters 1 and 2 are collections of samples covering specific use cases which demonstrate how to integrate MSAL Java into your application, and chapters 3 and 4 are more comprehensive tutorials demonstrating how to create simple and secure web apps from scratch.

## Samples and Documentation

### [1. Server-Side Scenarios](1-server-side/README.md)

This chapter offers samples covering server-side scenarios, in which the app runs on a server and serves multiple users. These samples demonstrate securing web apps and accessing web APIs, and creating secure daemon services that can access resources on behalf of your users.

### [2. Client-Side Scenarios](2-client-side/README.md)

This chapter offers samples covering client-side scenarios where the app runs on a user's device, such as with desktop or mobile apps.

### [3. Java Servlet Web App Tutorial](3-java-servlet-web-app/README.md)

A tutorial demonstrating how to create a web app using MSAL Java and Java servlets.

### [4. Spring Framework Web App Tutorial](4-spring-web-app/README.md)

A tutorial demonstrating how to create an MVC web app using MSAL Java alongside the Spring framework.

## Community Help and Support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) to get support from the community. Ask your questions on Stack Overflow first and browse [existing issues](https://github.com/Azure-Samples/ms-identity-msal-java-samples/issues) to see if someone has asked your question before. Tag your questions or comments with `msal` and `java`.

If you find a bug in the sample, please [open an issue](https://github.com/Azure-Samples/ms-identity-msal-java-samples/issues).

## Contributing

Thank you for your interest in contributing to Azure samples!

You can contribute to [Azure samples](https://azure.microsoft.com/documentation/samples/) in a few different ways:

- Submit issues through [issue tracker](https://github.com/Azure-Samples/ms-identity-msal-java-samples/issues) on GitHub. We are actively monitoring the issues and improving our samples.
- If you wish to make code changes to samples or contribute something new, please follow the [GitHub forks/pull request model](https://help.github.com/articles/fork-a-repo/) - fork the sample repo, make the change, and propose it back by submitting a pull request.

## Code of Conduct

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information, see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
