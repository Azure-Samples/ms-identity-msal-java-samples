package com.microsoft.azuresamples.authenticationb2c;

public class AADPasswordResetException extends Exception {
    static final String FORGOT_PASSWORD_ERROR_CODE = Config.getProperty("aad.forgotPasswordErrCode");

    AADPasswordResetException(String message){
        super(message);
    }
}