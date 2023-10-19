package com.microsoft.azuresamples.authenticationb2c;

public class Microsoft Entra IDPasswordResetException extends Exception {
    static final String FORGOT_PASSWORD_ERROR_CODE = Config.getProperty("Microsoft Entra ID.forgotPasswordErrCode");

    Microsoft Entra IDPasswordResetException(String message){
        super(message);
    }
}
