// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.helpers;

/**
 * Implement this so that AuthHelper can be customized to your needs!
 * This Sample project implements this in IdentityContextAdapterServlet.java
 * MUST BE INSTANTIATED ONCE PER REQUEST IN WEB APPS / WEB APIs before passing to AuthHelper
 */
public interface IdentityContextAdapter {
    public void setContext(IdentityContextData context);
    public IdentityContextData getContext();
    public void redirectUser(String location);
    public String getParameter(String parameterName);
}
