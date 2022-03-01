# Deploy your Java applications to Azure Cloud and use Azure App Service to manage your operations

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Registration](#registration)
  - [Register the web app](#register-the-web-app)
- [Deployment](#deployment)
  - [Step 1: Create a new app on Azure App Service](#step-1-create-a-new-app-on-azure-app-service)
  - [Step 2: Prepare the web app for deployment](#step-2-prepare-the-web-app-for-deployment)
  - [Step 3: Deploy the web app](#step-3-deploy-the-web-app)
  - [Step 4: Update your Azure AD App Registration](#step-4-update-your-azure-ad-app-registration)
- [We'd love your feedback!](#wed-love-your-feedback)
- [More information](#more-information)
- [Community Help and Support](#community-help-and-support)
- [Contributing](#contributing)
- [Code of Conduct](#code-of-conduct)

## Overview

This readme demonstrates how to use [Azure App Service](https://docs.microsoft.com/azure/app-service/) to deploy to **Azure Cloud** a Java Spring Boot web application that utilizes the [Azure AD Spring Boot Starter client library for Java](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/spring/azure-spring-boot-starter-active-directory) to sign in users and call Graph. It is recommended that you clone the repository [Tutorial: Enable your Java Spring Boot web app to sign in users and call APIs with the Microsoft identity platform](https://github.com/azure-samples/ms-identity-java-spring-tutorial) and use the sample in the `2-Authorization-I/call-graph` directory for deployment. You may choose to use these steps to help you deploy a different sample or your own project, noting that the instructions here are specific to the sample listed.

## Prerequisites

- An Azure Active Directory (Azure AD) tenant. For more information on how to get an Azure AD tenant, see [How to get an Azure AD tenant](https://azure.microsoft.com/documentation/articles/active-directory-howto-tenant/)
- A [user account](https://docs.microsoft.com/azure/active-directory/fundamentals/add-users-azure-active-directory) in your **Azure AD** tenant.
- [Visual Studio Code](https://code.visualstudio.com/download) is recommended for running and editing this sample.
- [VS Code Azure Tools Extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode.vscode-node-azure-pack) extension is recommended for interacting with **Azure** through VS Code interface.
- An **Azure subscription**. This sample uses the free tier of **Azure App Service**.

- [Maven 3.6](https://maven.apache.org/download.cgi) or higher installed and set up in path environment (i.e., callable via `mvn` command)
- [Maven extension for VSCode](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven)

## Setup

Follow the setup instructions in [Enable your Java Spring Boot web app to sign in users and call Microsoft Graph with the Microsoft identity platform](https://github.com/Azure-Samples/ms-identity-java-spring-tutorial/tree/main/2-Authorization-I/call-graph) sample.

## Registration

### Register the web app

Use an Azure AD application registration and its matching sample that that you have completed previously.
If you have not completed a sample yet, we recommend you proceed to complete [Enable your Java Spring Boot web app to sign in users and call Microsoft Graph with the Microsoft identity platform](https://github.com/Azure-Samples/ms-identity-java-spring-tutorial/tree/main/2-Authorization-I/call-graph) sample and use the app registration from it.

## Deployment

This guide is for deploying to **Azure App Service** via **VS Code Azure Tools Extension**. Follow these steps in a VSCode window with the workspace set to your copy of the [Enable your Java Spring Boot web app to sign in users and call Microsoft Graph with the Microsoft identity platform](https://github.com/Azure-Samples/ms-identity-java-spring-tutorial/tree/main/2-Authorization-I/call-graph).

In order to deploy your app, you must:

1. Prepare the app service and obtain a website URI in the form of `https://example-domain.azurewebsites.net.`
2. Update your **Azure AD App Registration**'s redirect URIs from the **Azure portal**, in order to include the redirect URI of your Azure App Service hosted Java web application.
3. Prepare your web app for deployment.
4. Deploy to Azure App Service.

### Step 1: Create a new app on Azure App Service

1. Open the VSCode command palette (ctrl+shift+P on Windows and command+shift+P on Mac).
1. Choose  `Azure App Service: Create New Web App...`
1. Enter a globally unique name for your web app (e.g. `example-domain`) and press enter. Make a note of this name. If you chose `example-domain` for your app name, your app's domain name will be `https://example-domain.azurewebsites.net`
1. Select `Java 11` for your runtime stack.
1. Select `Java SE (Embedded Web Server)` for your Java web server stack.
1. If you are asked for an OS, choose `Linux`.
1. Select `Free` or any other option for your pricing tier.

### Step 2: Prepare the web app for deployment

You must first modify the configuration files in your application. Go to your app's properties file(`src/main/resources/application.yml`).

- Change the value of `post-logout-redirect-uri: http://localhost:8080` to your deployed app's domain name. For example, if you chose `example-domain` for your app name in [Step 1: Create a new app on Azure App Service](#step-1-create-a-new-app-on-azure-app-service), you must now use the value  `post-logout-redirect-uri=https://example-domain.azurewebsites.net`. Be sure that you have also changed the protocol from `http` to `https`.

    ```yml
    # the default value was:
    # post-logout-redirect-uri: http://localhost:8080
    # the correct format for the new value is as follows:
    azure:
      activedirectory:
        # ...
        post-logout-redirect-uri: https://example-domain.azurewebsites.net
        # ...
    ```

- Add the following values for server configuration in order to properly handle the redirect URI. This tells the embedded Tomcat server that it is behind a reverse proxy (e.g., Azure App Service) and to correctly identify incoming requests as https.

    ```yml
    server:
      forward-headers-strategy: native
      tomcat:
        remoteip:
          protocol-header: "X-Forwarded-Proto"
          remote-ip-header: "X-Forwarded-For"
          internal-proxies: ".*"
    ```

You **may skip the rest of this step** if you are doing a test deployment with a development Azure Active Directory App registration that does not have any sensitive data. **It is not secure to deploy secrets in a config file to a production application**. To deploy your app more securely, you must:

1. Supply a config file that omits secrets (i.e., `application.yml` that does not contain `azure.activedirectory.client-secret`)
2. You may import the secrets from a secure location such as:
   1. **Azure Key Vault**. You may use the [Azure Key Vault Secrets Spring Boot starter client library for Java](https://github.com/Azure/azure-sdk-for-java/tree/azure-spring-boot-starter-keyvault-secrets_3.4.0/sdk/spring/azure-spring-boot-starter-keyvault-secrets). Set the client secret value in vault, naming it `azure.activedirectory.client-secret`.

   2. **Environment Variables** You may configure an environment variable from Azure portal or use the Azure Tools extension for VSCode.
      - Azure portal: *Azure Portal > App Services > YourAppName (e.g. example-domain) > Configuration.*
      - VSCode: *Azure Tools tab > App Service Blade > Your subscription > YourAppName (e.g. example-domain) > Application Settings > Right Click > New setting...* .

      Set an environment variable named `azure.activedirectory.client_secret`. Once you deploy your app, the secret will be loaded automatically. Note that the dash (`-`) from the config file must be replaced with an underscore (`_`) as follows:

         ```ini
         azure.activedirectory.client_secret=`YOUR CLIENT SECRET VALUE`
         ```

3. If you are sure you want to continue, proceed to [Deploy the web app](#step-3-deploy-the-web-app)

### Step 3: Deploy the web app

This guide is for deploying to **Azure App Service** via **Azure Maven web app plugin**.

1. Set up the configuration for the azure webapp maven plugin.

    1. Open a terminal window in the base directory of your Java Spring 5 project and enter the following command:

        ```bash
        mvn com.microsoft.azure:azure-webapp-maven-plugin:1.13.0:config
        ```

    1. You will be asked to choose a Java SE Web App. Enter the option number corresponding to the app you created in the section [Create a new app on Azure App Service](#step-1-create-a-new-app-on-azure-app-service), e.g., the option number corresponding to `example-domain (linux, java 11-java11)`. Press enter. Confirm that the details are correct and press enter again to continue. This will add deployment configuration settings in your `pom.xml` file.

2. Deploy the web app using the azure webapp maven plugin.

    1. In the terminal window, enter the following command:

        ```bash
        mvn clean package azure-webapp:deploy
        ```

        Note that the above **package** step requires the `spring-boot-maven-plugin` plugin and its `repackage` goal defined in the `project/build/plugins` section of the `pom.xml`. If you don't have this plugin and goal defined in your project, use this command instead: `mvn clean package spring-boot:repackage azure-webapp:deploy`.

3. The deployment should be finished in a few minutes. A status message will appear at the bottom right of your VSCode window.You will be notified when the deployment completes.

### Step 4: Update your Azure AD App Registration

- Navigate to the home page of your deployed app; take note of and copy the **redirect_uri** displayed on the home page.
- Navigate back to to the [Azure Portal](https://portal.azure.com).
- In the left-hand navigation pane, select the **Azure Active Directory** service, and then select **App registrations**.
- In the resulting screen, select the name of your application.
- In the Authentication blade, paste the URI you copied earlier from your deployed app instance. If the app had multiple redirect URIs, make sure to add new corresponding entries using the App service's full domain in lieu of `http://localhost:8080` for each redirect URI. For example, this might be `https://example-domain.azurewebsites.net/login/oauth2/code/`. Save the configuration.
- From the *Branding* menu, update the **Home page URL**, to the address of your service, for example `https://example-domain.azurewebsites.net/`. Save the configuration.
- Disable App Service's default authentication:

    Navigate to the **Azure App Service** Portal and locate your project. Once you do, click on the **Authentication/Authorization** blade. There, make sure that the **App Services Authentication** is switched off (and nothing else is checked), as this sample is using MSAL for authentication.

    ![disable_easy_auth](./ReadmeFiles/disable_easy_auth.png)
- You're done! Try navigating to the hosted app (e.g., `https://example-domain.azurewebsites.net/`!

## We'd love your feedback!

Were we successful in addressing your learning objective? Consider taking a moment to [share your experience with us](https://forms.office.com/r/iTZtCTrZrH).

## More information

- [Azure App Services](https://docs.microsoft.com/azure/app-service/)

For more information about how OAuth 2.0 protocols work in this scenario and other scenarios, see [Authentication Scenarios for Azure AD](https://docs.microsoft.com/azure/active-directory/develop/authentication-flows-app-scenarios).

## Community Help and Support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) to get support from the community.
Ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before.
Make sure that your questions or comments are tagged with [`azure-ad` `azure-ad-b2c` `ms-identity` `msal`, `java`].

If you find a bug in the sample, please raise the issue on [GitHub Issues](../../../../issues).

To provide a recommendation, visit the following [User Voice page](https://feedback.azure.com/forums/169401-azure-active-directory).

## Contributing

If you'd like to contribute to this sample, see [CONTRIBUTING.MD](../../CONTRIBUTING.md).

## Code of Conduct

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.
