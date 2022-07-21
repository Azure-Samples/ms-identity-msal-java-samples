---
languages:
  - java
products:
  - msal-java
  - azure
  - azure-active-directory
page_type: sample
urlFragment: ms-identity-msal-java-samples
description: "Securing Java apps using the Microsoft Identity platform and MSAL Java"
---
# Securing Java apps using the Microsoft Identity platform and MSAL Java

## About these samples

### Scenarios

The Microsoft Authentication Library (MSAL) enables developers to acquire tokens from the Microsoft identity platform, allowing applications to authenticate users and access secured web APIs. It can be used to provide secure access to Microsoft Graph, other Microsoft APIs, third-party web APIs, or your own web API.

This collection of samples covers a number of scenarios where MSAL Java can be used to secure Java applications, and is meant to build an understanding of MSAL Java and demonstrate how to integrate the library into your applications.
### Prerequisites

- [JDK Version 8](https://jdk.java.net/8/)
- [Maven 3](https://maven.apache.org/download.cgi)

These are the basic prerequisites for each sample, though each sample may specify more in their own README.

### Structure of the repository

This repository contains a number of scenario-specific samples and more comprehensive tutorials. We recommend starting with the [MSAL Java wiki](https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki) to get familiar with MSAL Java, and then try out the samples that best fit your use case. 

Chapters 1 and 2 are collections of samples covering specific use cases which demonstrate how to integrate MSAL Java into your application, and chapters 3 and 4 are more comprehensive tutorials demonstrating how to create simple and secure web apps from scratch.

## Samples and Documentation

[1. Server-Side Scenarios](1.%20Server-Side%20Scenarios/README.md)

This chapter offers samples covering server-side scenarios, in which the app runs on a server and serves multiple users. These samples demonstrate securing web apps and accessing web APIs, and creating secure daemon services that can access resources on behalf of your users.

[2. Client-Side Scenarios](2.%20Client-Side%20Scenarios/README.md)

This chapter offers samples covering client-side scenarios where the app runs on a user's device, such as with desktop or mobile apps.

[3. Java Servlet Web App Tutorial](3.%20Java%20Servlet%20Web%20App%20Tutorial/README.md)

A tutorial demonstrating how to create a web app using MSAL Java and Java servlets.

[4. Spring Framework Web App Tutorial](4.%20Spring%20Framework%20Web%20App%20Tutorial/README.md)

A tutorial demonstrating how to create an MVC web app using MSAL Java alongside the Spring framework.

## Community Help and Support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) to get support from the community.
Ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before.
Tag your questions or comments with [`msal` `java`].

If you find a bug in the sample, please open an issue on [GitHub Issues](TBD).

## Contributing

Thank you for your interest in contributing to Azure samples!

You can contribute to [Azure samples](https://azure.microsoft.com/documentation/samples/) in a few different ways:

- Submit feedback on [this sample page](https://github.com/Azure-Samples/ms-identity-msal-java-samples) whether it was helpful or not.  
- Submit issues through [issue tracker](https://github.com/Azure-Samples/ms-identity-msal-java-samples/issues) on GitHub. We are actively monitoring the issues and improving our samples.
- If you wish to make code changes to samples, or contribute something new, please follow the [GitHub Forks / Pull requests model](https://help.github.com/articles/fork-a-repo/): Fork the sample repo, make the change and propose it back by submitting a pull request.

## Code of Conduct
This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information, see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
