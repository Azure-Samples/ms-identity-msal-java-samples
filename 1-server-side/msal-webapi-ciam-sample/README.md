---
extensions:
    services: active-directory
    platforms: java
    author: ramya25
    level: 300
    client: Java web app
    service: Java Web API
    endpoint: Microsoft identity platform
page_type: sample
languages:
  - java  
products:
  - azure
  - msal-java
  - ms-graph
  - entra
description: "This sample demonstrates a Java web app application calling a Java Web API that is secured using Microsoft Entra ID using the On-Behalf-Of flow"
urlFragment: msal-java-obo-flow
---

# A Java Web API that calls another web API with the Microsoft identity platform using the On-Behalf-Of flow

## About this sample

### Overview

This sample of a Java web application demonstrates how to sign in a user in a Microsoft Entra CIAM tenant and obtain an [access token](https://aka.ms/access-tokens) for a Web API. The Web API is then used to call the [Microsoft Graph](https://graph.microsoft.com) using an [access token](https://docs.microsoft.com/azure/active-directory/develop/access-tokens) obtained using the [on-behalf-of](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow) flow. All these are secured using the [Microsoft identity platform (formerly Microsoft Entra ID for developers)](https://docs.microsoft.com/azure/active-directory/develop/).

### Scenario

1. The Java web application uses the [Microsoft Authentication Library for Java (MSAL4J)](https://github.com/AzureAD/microsoft-authentication-library-for-java) to obtain an Access token from the Microsoft identity platform for the authenticated user.
1. The access token is then used as a bearer token to the request to the Java web API. The web API validates the access token using Spring Security, exchanges the incoming access token for a Microsoft Graph access token using OAuth2.0 On-behalf-of flow. This new access token is used to request information from a Graph endpoint.

The flow is as follows:

1. Sign-in the user in the client(web) application.
1. Acquire an access token for the Java Web API and call it.
1. The Java Web API validates the access token using Spring Security and then calls another downstream Web API ([The Microsoft Graph](https://graph.microsoft.com)) after obtaining another [access token](https://docs.microsoft.com/azure/active-directory/develop/access-tokens) using the [on-behalf-of](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow) flow.

## Prerequisites

- [JDK Version 8 or higher](https://jdk.java.net/8/)
- [Maven 3](https://maven.apache.org/download.cgi)
- A Microsoft Entra CIAM tenant. For instructions on creating this tenant, see [Create a customer identity and access management (CIAM) tenant](https://learn.microsoft.com/en-us/entra/external-id/customers/how-to-create-customer-tenant-portal)
- A user account in your own Microsoft Entra tenant if you want to work with **accounts in your organizational directory only** (single-tenant mode). If have not yet [created a user account](https://docs.microsoft.com/azure/active-directory/fundamentals/add-users-azure-active-directory) in your AD tenant yet, you should do so before proceeding.
- A user account in any organization's Microsoft Entra tenant if you want to work with **accounts in any organizational directory** (multi-tenant mode).  This sample must be modified to work with a **personal Microsoft account**. If have not yet [created a user account](https://docs.microsoft.com/azure/active-directory/fundamentals/add-users-azure-active-directory) in your AD tenant yet, you should do so before proceeding.
- A personal Microsoft account (e.g., Xbox, Hotmail, Live, etc) if you want to work with **personal Microsoft accounts**

### Step 1: Download Java (8 and above) for your platform

To successfully use this sample, you need a working installation of [Java](https://openjdk.java.net/install/) and [Maven](https://maven.apache.org/).

### Step 2:  Clone or download this repository 

From your shell or command line:

```Shell
git clone https://github.com/Azure-Samples/ms-identity-java-webapi.git
```

### Step 3:  Register the sample with your Microsoft Entra tenant

There are two projects in this sample. Each needs to be registered separately in your Microsoft Entra tenant. To register these projects:

#### First step: choose the Microsoft Entra tenant where you want to create your applications

As a first step you'll need to:

1. Sign in to the [Microsoft Entra admin center](https://entra.microsoft.com) using either a work or school account or a personal Microsoft account.
1. If your account is present in more than one Microsoft Entra tenant, select your profile at the top right corner in the menu on top of the page, and then switch directory. Change your portal session to the desired Microsoft Entra tenant.
1. In the portal menu, select the Microsoft Entra ID service, and then select App registrations.
> In the next steps, you might need the tenant name (or directory name) or the tenant ID (or directory ID). These are presented in the **Properties** of the Microsoft Entra ID window respectively as *Name* and *Directory ID*

#### Register an app in Azure for the API

1. Navigate to the app registrations page of the [Microsoft Entra admin center](https://entra.microsoft.com) (Identity > Applications > App registrations)
1. Select **New registration**.
1. In the **Register an application page** that appears, enter your application's registration information:
    - In the **Name** section, enter a meaningful application name that will be displayed to users of the app (something as straightforward as `sample-ciam-web-api` will work for this sample)
    - Ensure **Supported account types** is **Accounts in this organizational directory only (`your-tenant-domain-name` only - Single tenant)**.
1. Click on the **Register** button to create the application.
1. In the app registration's Overview tab, find and note the **Application (client) ID** and **Directory (tenant) ID** values. These will be needed in later steps.
1. Select the **Certificates & secrets** tab in the left to open the page where we can generate secrets and upload certificates.
1. In the **Client secrets** section, click on **New client secret**:
    - Type a meaningful description (for instance `ciam sample app secret`)
    - Select a key duration that best suits your security needs (for testing with this sample, the quickest expiration option is likely enough)
    - The generated key value will be displayed when you click the **Add** button. Copy the generated **value** for use in later steps.
        - **Be sure to save this key value!** This key value will not be displayed again, and is not retrievable by any other means, so make sure to note it from the Microsoft Entra admin center before navigating to any other screen or blade.
1. In the Application menu blade, click on **API permissions** to open the page where we add access to the Apis that your application needs.
   - Click the **Add a permission** button and then,
   - Ensure that the **Microsoft APIs** tab is selected.
   - In the *Commonly used Microsoft APIs* section, click on **Microsoft Graph**
   - In the **Delegated permissions** section, select the **User.Read** in the list. Use the search box if necessary.
   - Click on the **Add permissions** button in the bottom.
1. In the Application menu blade, click on **Expose an API** to open the page where declare the parameters to expose this app as an Api for which client applications can obtain [access tokens](https://docs.microsoft.com/azure/active-directory/develop/access-tokens) for.
The first thing that we need to do is to declare the unique [resource](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-auth-code-flow) URI that the clients will be using to obtain access tokens for this Api. To declare an resource URI, follow the following steps:
   - Click `Set` next to the **Application ID URI** to generate a URI that is unique for this app.
   - For this sample, accept the proposed Application ID URI (api://{clientId}) by selecting **Save**, and record the URI for later reference.
1. All Apis have to publish a minimum of one [scope](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-auth-code-flow#request-an-authorization-code) for the client's to obtain an access token successfully. To publish a scope, follow the following steps:
   - Select **Add a scope** button open the **Add a scope** screen and Enter the values as indicated below:  
      - For **Scope name**, use `access_as_user`.
      - Select **Admins and users** options for **Who can consent?**
      - For **Admin consent display name** type `Access API`
      - For **Admin consent description** type `Allows the app to access java-ciam-web-api as the signed-in user.`
      - For **User consent display name** type `Access API`
      - For **User consent description** type `Allow the application to access the API on your behalf.`
      - Keep **State** as **Enabled**
      - Click on the **Add scope** button on the bottom to save this scope.
      - Record the scope's URI (`api://{clientid}/access_as_user`) for later reference.
1. Finally, in the Application menu blade, click on **Manifest** to open the application's manifest editor
    - Find the `accessTokenAcceptedVersion` field and change the value to `2`
    - The version of access tokens is determined by the resource, so a value of `2` will result in v2.0 access tokens rather than the default v1.0

#### Configure **msal-web-api** to use your app registration

Open `application.properties` in the msal-web-api/src/main/resources folder, and fill in the following placeholders:
- Replace *Enter-Your-Tenant-Domain-Here* with the **directory (tenant) domain name**.
  - For example, if your directory domain is contoso.onmicrosoft.com, you would use just 'contoso' here
- Replace *Enter_the_Tenant_Id_Here* with the **directory (tenant) ID**.
- Replace *Enter_the_Application_Id_here* with the **application (client) ID**.
- Replace *Enter_the_Client_Secret_Here* with the **secret key value**.

#### Register an app in Azure for the client web app

1. Navigate to the Microsoft identity platform for developers [App registrations](https://go.microsoft.com/fwlink/?linkid=2083908) page.
1. Click **New registration**.
1. In the **Register an application page** that appears, enter your application's registration information:
   - In the **Name** section, enter a meaningful application name that will be displayed to users of the app (something as straightforward as `sample-ciam-web-app` will work for this sample)
   - Ensure **Supported account types** is **Accounts in this organizational directory only (`your-tenant-domain-name` only - Single tenant)**.
1. Click on the **Register** button to create the application.
1. In the app's registration **Overview** page, find the **Application (client) ID** value and record it for later. You'll need it to configure the configuration file(s) later in your code.
1. In the app's registration screen, click on the **Authentication** blade in the left and:
   - In the **Platform configurations** section select **Add a platform** and create a new **Web** application
   - Enter the following as the redirect URI: `http://localhost:8080/msal4jsample/secure/aad`
   - Click on **Configure** to save your changes.
   - Click the **Save** button to save the redirect URI changes.
1. In the Application menu blade, click on the **Certificates & secrets** to open the page where we can generate secrets and upload certificates.
1. Select the **Certificates & secrets** tab in the left to open the page where we can generate secrets and upload certificates.
1. In the **Client secrets** section, click on **New client secret**:
    - Type a meaningful description (for instance `ciam sample app secret`)
    - Select a key duration that best suits your security needs (for testing with this sample, the quickest expiration option is likely enough)
    - The generated key value will be displayed when you click the **Add** button. Copy the generated **value** for use in later steps.
        - **Be sure to save this key value!** This key value will not be displayed again, and is not retrievable by any other means, so make sure to note it from the Microsoft Entra admin center before navigating to any other screen or blade.
1. In the Application menu blade, click on the **API permissions** to open the page where we add access to the Apis that your application needs.
   - Click the **Add a permission** button
   - Go to the **My APIs** tab
   - In the list of APIs, select the API you created previously (the example name was `sample-ciam-web-api`)
   - In the **Delegated permissions** section, select the **access_as_user** in the list
   - Click on the **Add permissions** button in the bottom

#### Configure **msal-web-app** to use your app registration

Open `application.properties` in the msal-web-app/src/main/resources folder. Fill in with your tenant and app registration information noted in registration step.

- Replace *Enter-Your-Tenant-Domain-Here* with the **directory (tenant) domain name**.
    - For example, if your directory domain is contoso.onmicrosoft.com, you would use just 'contoso' here
- Replace *Enter_the_Tenant_Id_Here* with the **directory (tenant) ID**.
- Replace *Enter_the_Application_Id_here* with the **application (client) ID**.
- Replace *Enter_the_Client_Secret_Here* with the **secret key value**.
- Replace *Enter_the_Api_Scope_Here* with the API you exposed, which should have the format of **(api://{clientId}/access_as_user)**

#### Configure known client applications for the API

For a middle tier web API to be able to call a downstream web API, the middle tier app needs to be granted the required permissions as well.
However, since the middle tier cannot interact with the signed-in user, it needs to be explicitly bound to the client app in its Microsoft Entra ID registration.
This binding merges the permissions required by both the client and the middle tier WebApi and presents it to the end user in a single consent dialog. The user than then consent to this combined set of permissions.

To achieve this, you need to add the "Client ID" of the client app, in the manifest of the web API in the **knownClientApplications** property. Here's how:

In the [Microsoft Entra admin center](https://entra.microsoft.com), navigate to the app registration you created for the API (sample-ciam-web-api):

- In the Application menu blade, select **Manifest**.
- Find the attribute **knownClientApplications** and add your client application's(`sample-ciam-web-app`) **Application (client) Id** as its element.
- Click **Save**.

### Step 4: Run the applications

To run the project, you can either:

Run it directly from your IDE by using the embedded spring boot server or package it to a WAR file using [maven](https://maven.apache.org/plugins/maven-war-plugin/usage.html) and deploy it a J2EE container solution for example [Tomcat](https://tomcat.apache.org/maven-plugin-trunk/tomcat6-maven-plugin/examples/deployment.html)

#### Running from IDE

If you are running the application from an IDE, follow the steps below.

The following steps are for IntelliJ IDEA. But you can choose and work with any editor of your choice.

1. Navigate to *Run* --> *Edit Configurations* from menu bar.
2. Click on '+' (Add new configuration) and select *Application*.
3. Enter name of the application for example `webapp`
4. Go to main class and select from the dropdown, for example `MsalWebSampleApplication` also go to *Use classpath of the module* and select from the dropdown, for example `msal-web-sample`.
5. Click on *Apply*. Follow the same instructions for adding the other application.
6. Click on '+' (Add new configuration) and select *Compound*.
7. Enter a friendly name for in the *Name* for example `Msal-webapi-sample`.
8. Click on '+' and select the application names you have created in the above steps one at a time.
9. Click on *Apply*. Select the created configuration and click **Run**. Now both the projects will run at a time.

- Now navigate to the home page of the project. For this sample, the standard home page URL is <https://localhost:8080/msal4jsample>

#### Packaging and deploying to container

If you would like to deploy the sample to Tomcat, you will need to make a couple of changes to the source code in both modules.

1. Open msal-web-app/pom.xml
    - Under `<name>msal-web-app</name>` add `<packaging>war</packaging>`
    - Add dependency:

         ```xml
         <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-tomcat</artifactId>
         </dependency>
         ```

2. Open msal-web-app/src/main/java/com.microsoft.azure.msalciamwebsample/MsalWebSampleApplication

    - Delete all source code and replace with

    ```Java
        package com.microsoft.azure.msalwebsample;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;
        import org.springframework.boot.builder.SpringApplicationBuilder;
        import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

        @SpringBootApplication
        public class MsalWebSampleApplication extends SpringBootServletInitializer {
         public static void main(String[] args) {
          SpringApplication.run(MsalWebSampleApplication.class, args);
         }

         @Override
         protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
          return builder.sources(MsalWebSampleApplication.class);
         }
        }
    ```

3. Open a command prompt, go to the root folder of the project, and run `mvn package`

- This will generate a `msal-web-app-0.1.0.war` file in your /targets directory.
- Rename this file to `msal4jsample.war`
- Deploy this war file using Tomcat or any other J2EE container solution.
- To deploy on Tomcat container, copy the .war file to the webapp's folder under your Tomcat installation and then start the Tomcat server.
- Repeat these steps for the `msal-web-api` also.

This WAR will automatically be hosted at `http:<yourserverhost>:<yourserverport>/`

Tomcats default port is 8080. This can be changed by
    - Going to tomcat/conf/server.xml
    - Search "Connector Port"
    - Replace "8080" with your desired port number

Example: `http://localhost:8080/msal4jsample`

#### HTTPS on localhost

If you are only testing locally, you may skip this step. If you deploy your app to Azure App Service (for production or for testing), https is handled by Azure and you may skip this step. Note that https is essential for providing critical security and data integrity to your applications, and http should not be used outside of testing scenarios. If you need to configure your application to handle https, complete the instructions in this section.

1. Use the `keytool` utility (included in JRE) if you want to generate self-signed certificate.

    ```Bash
    keytool -genkeypair -alias testCert -keyalg RSA -storetype PKCS12 -keystore keystore.p12 -storepass password
    ```

2. Put the following key-value pairs into your [application.properties](msal-web-sample/src/main/resources/application.properties) file.

    ```ini
    server.ssl.key-store-type=PKCS12
    server.ssl.key-store=classpath:keystore.p12
    server.ssl.key-store-password=password
    server.ssl.key-alias=testCert
    ```

3. Change both occurrences of `8080` to `8443` in the msal-web-sample's [application.properties](msal-web-sample/src/main/resources/application.properties) file.
4. Update your java_webapp Microsoft Entra application registration redirects (e.g., `https://localhost:8443/msal4jsample/secure/aad` and `https://localhost:8443/msal4jsample/graph/me`) on the [Microsoft Entra admin center](https://entra.microsoft.com).

### You're done, run the code

Click on "Login" to start the process of logging in. Once logged in, you'll see the account information for the user that is logged in and a Button "Call OBO API" , which will call the Microsoft Graph API with the OBO token and display the basic information of the signed-in user. You'll then have the option to "Sign out".

## About the Code

There are many key points in this sample to make the On-Behalf-Of-(OBO) flow work properly and in this section we will explain these key points for each project.

### msal-weba-app

1. **AuthPageController** class

    Contains the api to interact with the web app. The `securePage` method handles the authentication part and signs in the user using microsoft authentication.

2. **AuthHelper** class

    Contains helper methods to handle authentication.

    A code snippet showing how to obtain auth result by silent flow.

    ```Java

        private ConfidentialClientApplication createClientApplication() throws MalformedURLException {
            return ConfidentialClientApplication.builder(clientId, ClientCredentialFactory.create(clientSecret))
                                                .authority(authority)
                                                .build();
        }...

          SilentParameters parameters = SilentParameters.builder(
                        Collections.singleton(scope),
                        result.account()).build();

                CompletableFuture<IAuthenticationResult> future = app.acquireTokenSilently(parameters);
                ...

        storeTokenCacheInSession(httpRequest, app.tokenCache().serialize());
        ...
    ```

    Important things to notice:

    - We create a `ConfidentialClientApplication` using **MSAL Build Pattern** passing the `clientId`, `clientSecret` and `authority` in the builder. This `ConfidentialClientApplication` will be responsible of acquiring access tokens later in the code.
    - `ConfidentialClientApplication` also has a token cache, that will cache [access tokens](https://docs.microsoft.com/azure/active-directory/develop/access-tokens) and [refresh tokens](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-auth-code-flow#refresh-the-access-token) for the signed-in user. This is done so that the application can fetch access tokens after they have expired without prompting the user to sign-in again.

3. **AuthFilter** class

    Contains methods for session and state management.

### msal-web-api
1. **ApiController** class

    Uses the [Java Microsoft Graph SDK](https://github.com/microsoftgraph/msgraph-sdk-java) to call the the api(graphMeApi). The `GraphServiceClient` uses the `oboAuthProvider` to acquire the necessary tokens to access the Graph Me endpoint.

2. **AADClaimsVerifier** class
   Validation methods used by Spring are defined here.
```Java
        public void verify(final Map<String, Object> claims) throws InvalidTokenException {
        if (CollectionUtils.isEmpty(claims))
            throw new InvalidTokenException("token must contain claims");
        if (!claims.containsKey("iss"))
            throw new InvalidTokenException("token must contain issuer (iss) claim");
        if (!claims.containsKey("aud"))
            throw new InvalidTokenException("token must contain audience (aud) claim");

        final String jwtIssuer = (String) claims.get(ISS_CLAIM);
        if (!Arrays.stream(acceptedIssuers.toArray()).anyMatch(x -> x.equals(jwtIssuer))) {
            throw new InvalidTokenException("Invalid Issuer (iss) claim: " + jwtIssuer);
        }

        final String jwtAud = (String) claims.get(AUD_CLAIM);
        if (!jwtAud.equals(applicationId) && !jwtAud.equals( V1_AUD_PREFIX + applicationId)) {
            throw new InvalidTokenException("Invalid Audience (aud) claim: " + jwtAud);
        }
       }
```

3. **SecurityResourceServerConfig** class

    Token Validation of the caller happens in this class, where the access token presented by the client app is validated using Spring Security.

    ```Java
            http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/*")
            .access("#oauth2.hasScope('" + accessAsUserScope + "')"); // required scope to access /api URL
    ```

4. **AuthProvider** class

    Contains the methods to exchange the incoming token for an access token for Microsoft Graph.

    A code snippet showing how to obtain obo token

    ```Java
        IAuthenticationResult authResult;
        ConfidentialClientApplication application = null;
        try {
            application = ConfidentialClientApplication
                    .builder(clientId, ClientCredentialFactory.createFromSecret(secret))
                    .authority(authority)
                    .build();

            String cachedTokens = cacheManager.getCache("tokens").get(cacheKey, String.class);
            if (cachedTokens != null) {
                application.tokenCache().deserialize(cachedTokens);
            }

            SilentParameters silentParameters =
                    SilentParameters.builder(Collections.singleton(scope))
                            .build();
            authResult = application.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                OnBehalfOfParameters parameters =
                        OnBehalfOfParameters.builder(Collections.singleton(scope),
                                new UserAssertion(authToken))
                                .build();
                authResult = application.acquireToken(parameters).join();
            } else {
                throw new AuthException(String.format("Error acquiring token from Microsoft Entra ID: %s", ex.getMessage()),
                        ex.getCause());
            }
        }
    ```
    Important things to notice:
    - `application.acquireTokenSilently` is attempted first to try and use the cached tokens. If the silent call fails, the sample falls back to trying to acquire a token via obo. 
    - The **scope** [.default](https://docs.microsoft.com/azure/active-directory/developv2-permissions-and-consent#the-default-scope) is a built-in scope for every application that refers to the static list of permissions configured on the application registration. In our scenario here, it enables the user to grant consent for permissions for both the Web API and the downstream API (Microsoft Graph). For example, the permissions for the Web API and the downstream API (Microsoft Graph) are listed below:
             - Web Api sample (access_as_user)
             - Microsoft Graph (user.read)

    - When you use the `.default` scope, the end user is prompted for a combined set of permissions that include scopes from both the **Web Api** and **Microsoft Graph**.

## Feedback, Community Help and Support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/adal) to get support from the community.
Ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before.
Make sure that your questions or comments are tagged with [`msal4j` `Java`].

If you find a bug in the sample, please raise the issue on [GitHub Issues](https://github.com/Azure-Samples/ms-identity-java-webapp/issues).

To provide a recommendation, visit the following [User Voice page](https://feedback.azure.com/forums/169401-azure-active-directory).

## Contributing

If you'd like to contribute to this sample, see [CONTRIBUTING.MD](https://github.com/Azure-Samples/ms-identity-java-webapp/blob/master/CONTRIBUTING.md).

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information, see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Other samples and documentation

- For more information, see MSAL4J [conceptual documentation](https://github.com/AzureAD/azure-activedirectory-library-for-java/wiki)
- Other samples for Microsoft identity platform are available from [https://aka.ms/aaddevsamplesv2](https://aka.ms/aaddevsamplesv2)
- [Microsoft identity platform and OAuth 2.0 On-Behalf-Of flow](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-on-behalf-of-flow)
- The documentation for Microsoft identity platform is available from [https://aka.ms/aadv2](https://aka.ms/aadv2)
- For more information about web apps scenarios on the Microsoft identity platform see [Scenario: Web app that signs in users](https://docs.microsoft.com/azure/active-directory/develop/scenario-web-app-sign-user-overview) and [Scenario: Web app that calls web APIs](https://docs.microsoft.com/azure/active-directory/develop/scenario-web-app-call-api-overview)
- [Why update to Microsoft identity platform?](https://docs.microsoft.com/azure/active-directory/develop/azure-ad-endpoint-comparison)
- For more information about how OAuth 2.0 protocols work in this scenario and other scenarios, see [Authentication Scenarios for Microsoft Entra ID](http://go.microsoft.com/fwlink/?LinkId=394414).
