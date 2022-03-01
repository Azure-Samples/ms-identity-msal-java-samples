// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.msalobosample;

public class AuthException extends RuntimeException {

    public AuthException(String message, Throwable cause){
        super(message, cause);
    }
}
