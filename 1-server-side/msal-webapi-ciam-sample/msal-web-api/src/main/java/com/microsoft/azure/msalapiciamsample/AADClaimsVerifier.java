package com.microsoft.azure.msalapiciamsample;

import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class AADClaimsVerifier implements JwtClaimsSetVerifier {
    private static final String ISS_CLAIM = "iss";
    private static final String AUD_CLAIM = "aud";
    private static final String V2_ISSUER_FORMAT = "https://%s.ciamlogin.com/%s/v2.0";


    private String acceptedIssuer = "";
    private final String applicationId;

    public AADClaimsVerifier(final String tenant, final String applicationId) {


        Assert.notNull(tenant, "acceptedTenant value cannot be null");
        Assert.notNull(applicationId, "applicationId (for audience claim) cannot be null");

        this.applicationId = applicationId;

        generateAcceptedIssuers(tenant);
    }

    private void generateAcceptedIssuers(final String acceptedTenant) {
        acceptedIssuer = String.format(V2_ISSUER_FORMAT, acceptedTenant, acceptedTenant);
    }

    /**
     * This method is used by Spring to verify certain parts of the access token.
     * <p>
     * Currently, it ensures that the issue and audience exist, that the issuer matches the accepted issues defined
     * in the security.oauth2.aad.aliases field of application.properites, and that the audience matches the API set up
     * in Azure
     */

    public void verify(final Map<String, Object> claims) throws InvalidTokenException {
        if (CollectionUtils.isEmpty(claims))
            throw new InvalidTokenException("token must contain claims");
        if (!claims.containsKey("iss"))
            throw new InvalidTokenException("token must contain issuer (iss) claim");
        if (!claims.containsKey("aud"))
            throw new InvalidTokenException("token must contain audience (aud) claim");

        final String jwtIssuer = (String) claims.get(ISS_CLAIM);
        if (!acceptedIssuer.equals(jwtIssuer)) {
            throw new InvalidTokenException("Invalid Issuer (iss) claim: " + jwtIssuer);
        }

        final String jwtAud = (String) claims.get(AUD_CLAIM);
        if (!jwtAud.equals(applicationId)) {
            throw new InvalidTokenException("Invalid Audience (aud) claim: " + jwtAud);
        }

    }
}
