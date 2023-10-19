# MSAL Java samples demonstrating how a daemon console application can call Microsoft Graph using its own identity

## About these samples

### Overview

In MSAL client-side applications include desktop and mobile apps, or any other app that runs directly on a user's device rather than on a separate server.

Several samples are available:

1. An application which uses the device code flow to allow users to sign into input-constrained devices such as a smart TV, IoT device, or printer.
    - Source code can be found in the [msal-devicecode](Device-Code-Flow) directory, as well as the [README](Device-Code-Flow/README.md) for configuring and running the sample
1. Use MSAL Java in an application on domain or Azure Active Directory (Azure AD) joined computers to acquire a token using a user's active directory credentials.
    - Source code can be found in the [msal-integrated-windows-auth](Integrated-Windows-Auth-Flow) directory, as well as the [README](Integrated-Windows-Auth-Flow/README.md) for configuring and running the sample
1. An application which signs in users and retrieves tokens by directly handling their password.
    - Source code can be found in the [msal-username-password](Username-Password-Flow) directory, as well as the [README](Username-Password-Flow/README.md) for configuring and running the sample
