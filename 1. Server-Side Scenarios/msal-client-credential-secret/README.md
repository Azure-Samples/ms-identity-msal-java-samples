---
services: active-directory
platforms: Java
endpoint: Microsoft identity platform
page_type: sample
author: ramya25
level: 200
client: Java console daemon app
service: Microsoft Graph
languages:
  - java
products:
  - azure
  - azure-active-directory
  - java
  - office-ms-graph
description: "This sample demonstrates how a daemon console app can use a secret configured in Azure get an access token to call Microsoft Graph using MSAL4J."
---

# MSAL Java sample demonstrating how a daemon console application can call Microsoft Graph using its own identity

## About this sample

### Overview

This app demonstrates how to use the [Microsoft identity platform](http://aka.ms/aadv2) to access the data of Microsoft business customers in a long-running, non-interactive process. It uses the [Microsoft Authentication Library (MSAL) for Java](https://github.com/AzureAD/microsoft-authentication-library-for-java) to acquire an [access token](https://docs.microsoft.com/azure/active-directory/develop/access-tokens), which it then uses to call [Microsoft Graph](https://graph.microsoft.io) and accesses organizational data. The sample utilizes the [OAuth 2 client credentials grant](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow) and a secret value configured in Azure to obtain an access token for calls to Microsoft Graph.

## Scenario

The console application:

- Acquires an access token from Azure AD using its own identity (without a user).
- It then calls the Microsoft Graph `/users` endpoint to retrieve a list of users, which it then displays on the screen (as a Json blob).

![Topology](../ReadmeImages/topology.png)

For more information on the concepts used in this sample, be sure to read the [Microsoft identity platform endpoint client credentials protocol documentation](https://azure.microsoft.com/documentation/articles/active-directory-v2-protocols-oauth-client-creds).

## How to run this sample

To run this sample, you'll need:

- Working installation of [Java 8 or greater](https://openjdk.java.net/install/) and [Maven](https://maven.apache.org/).
- An Azure Active Directory (Azure AD) tenant. For more information on how to get an Azure AD tenant, see [How to get an Azure AD tenant](https://azure.microsoft.com/en-us/documentation/articles/active-directory-howto-tenant/).
- One or more user accounts in your Azure AD tenant.

### Step 1:  Clone or download this repository

From your shell or command line:

```Shell
git clone https://github.com/Azure-Samples/ms-identity-java-daemon.git
```

or download and extract the repository .zip file.

### Step 2:  Register the sample with your Azure Active Directory tenant

To register the project, you can:

- either follow the steps [Step 2: Register the sample with your Azure Active Directory tenant](#step-2-register-the-sample-with-your-azure-active-directory-tenant) and [Step 3: Configure the sample to use your Azure AD tenant](#choose-the-azure-ad-tenant-where-you-want-to-create-your-applications)
- or use PowerShell scripts that:
  - **automatically** creates the Azure AD applications and related objects (passwords, permissions, dependencies) for you.
  - modify the project's configuration files.

If you want to use this automation, read the instructions in [App Creation Scripts](../AppCreationScripts/AppCreationScripts.md)
Please note that the configuration of your code (Step 3) still needs to be done manually.

Follow the steps below to manually walk through the steps to register and configure the applications.

#### Choose the Azure AD tenant where you want to create your applications

As a first step you'll need to:

1. Sign in to the [Azure portal](https://portal.azure.com) using either a work or school account or a personal Microsoft account.
1. If your account is present in more than one Azure AD tenant, select your profile at the top right corner in the menu on top of the page, and then **switch directory**.
   Change your portal session to the desired Azure AD tenant.
1. In the portal menu, select the **Azure Active Directory** service, and then select **App registrations**.

#### Register the client app (java-daemon-console)

1. Navigate to the Microsoft identity platform for developers [App registrations](https://go.microsoft.com/fwlink/?linkid=2083908) page.
1. Select **New registration**.
   - In the **Name** section, enter a meaningful application name that will be displayed to users of the app, for example `java-daemon-console`.
   - In the **Supported account types** section, select **Accounts in this organizational directory only ({tenant name})**.
   - Click **Register** button at the bottom to create the application.
1. On the application **Overview** page, find the **Application (client) ID** and **Directory (tenant) ID** values and record it for later. You'll need it to configure the configuration file(s) later in your code.
1. In the Application menu blade, click on the **Certificates & secrets**, in the **Client secrets** section, choose **New client secret**:
   - Type a key description (for instance `app secret`),
   - Select a key duration of either **In 1 year**, **In 2 years**, or **Never Expires** as per your security concerns.
   - The generated key value will be displayed when you click the **Add** button. Copy the generated value for use in the steps later.
   - You'll need this key later in your code's configuration files. This key value will not be displayed again, and is not retrievable by any other means, so make sure to note it from the Azure portal before navigating to any other screen or blade.
1. In the Application menu blade, click on the **API permissions** in the left to open the page where we add access to the Apis that your application needs.
   - Click the **Add a permission** button and then,
   - Ensure that the **Microsoft APIs** tab is selected
   - In the *Commonly used Microsoft APIs* section, click on **Microsoft Graph**
   - In the **Application permissions** section, ensure that the right permissions are checked: **User.Read.All**
   - Select the **Add permissions** button at the bottom.

1. At this stage, the permissions are assigned correctly but since the client app does not allow users to interact, the user's themselves cannot consent to these permissions.
   To get around this problem, we'd let the [tenant administrator consent on behalf of all users in the tenant](https://docs.microsoft.com/azure/active-directory/develop/v2-admin-consent).
   Click the **Grant admin consent for {tenant}** button, and then select **Yes** when you are asked if you want to grant consent for the requested permissions for all account in the tenant.
   You need to be the tenant admin to be able to carry out this operation.

### Step 3:  Configure the client app (java-daemon-console) to use your app registration

Open the project in your IDE to configure the code.
>In the steps below, "ClientID" is the same as "Application ID" or "AppId" and "Tenant ID" is same as "Directory ID".

1. Open the `msal-client-credential-secret\src\main\resources\application.properties` class
1. Set the `CLIENT_ID` property to the application/client ID value you recorded earlier
1. Replace `Tenant_Info_Here` in the `AUTHORITY` property with the directory/tenant ID value you recorded earlier 
1. Set the `SECRET` property to the secret value you recorded earlier

### Step 4: Run the sample

You can test the sample directly by running the main method of ClientCredentialGrant.java from your IDE.

From your shell or command line:

- `$ mvn clean compile assembly:single`

This will generate a `msal-client-credential-secret-1.0.0.jar` file in your /targets directory. Run this using your Java executable like below:

- `$ java -jar msal-client-credential-secret-1.0.0.jar`


After running, the application should display the list of user in the configured tenant.

## About the code

The relevant code for this sample is in the `ClientCredentialGrant.java` file.

1. Create the MSAL confidential client application.

    Important note: even if we are building a console application, it is a daemon, and therefore a confidential client application, as it does not
    access Web APIs on behalf of a user, but on its own application behalf.

    ```Java
       ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                       clientId,
                       ClientCredentialFactory.createFromSecret(secret))
                       .authority(authority)
                       .build();
    ```

2. Define the scopes.

   Specific to client credentials, you don't specify the individual scopes you want to access. You have statically declared them during the application registration step. Therefore the only possible scope is "resource/.default" (here "https://graph.microsoft.com/.default")
   which means "the static permissions defined in the application"

    ```Java
    // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
    // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
    ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
       Collections.singleton(scope))
       .build();
    ```

3. Acquire the token

    Uses the ConfidentialClientApplication, built with the configured authority/client ID/secret, to acquire an access token
    
   ```Java
       CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
       return future.get();    
    ```

4. Call the API and read result

    In this case calling "https://graph.microsoft.com/v1.0/users" with the access token as a bearer token.

    ```Java
       URL url = new URL("https://graph.microsoft.com/v1.0/users");
       HttpURLConnection conn = (HttpURLConnection) url.openConnection();

       //...
   
       try(BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))){

           String inputLine;
           response = new StringBuilder();
           while (( inputLine = in.readLine()) != null) {
                       response.append(inputLine);
           }
       }
       return response.toString();
    ```

## Troubleshooting

### Did you forget to provide admin consent? This is needed for daemon apps

If you get an `Forbidden` error when calling the API, this is because the tenant administrator has not granted permissions
to the application. Check the steps in [Register the client app (daemon-console)](#register-the-client-app-daemon-console) above.

## Community Help and Support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) to get support from the community.
Ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before.
Make sure that your questions or comments are tagged with [`msal` `java`].

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
- [Quickstart: Configure a client application to access web APIs](https://docs.microsoft.com/azure/active-directory/develop/quickstart-configure-app-access-web-apis)
- The documentation for Microsoft identity platform is available from [https://aka.ms/aadv2](https://aka.ms/aadv2)
- Other samples for Microsoft identity platform are available from [https://aka.ms/aaddevsamplesv2](https://aka.ms/aaddevsamplesv2)