// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.msalappciamsample;

import java.util.Date;

class StateData {
    private String nonce;
    private Date expirationDate;

    StateData(String nonce, Date expirationDate) {
        this.nonce = nonce;
        this.expirationDate = expirationDate;
    }

    public String getNonce() {
        return nonce;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}