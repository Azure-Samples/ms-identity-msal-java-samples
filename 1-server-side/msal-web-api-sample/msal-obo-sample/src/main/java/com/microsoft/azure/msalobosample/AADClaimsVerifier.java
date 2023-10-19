package com.microsoft.azure.msalobosample;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class Microsoft Entra IDClaimsVerifier implements JwtClaimsSetVerifier {
    private static final String ISS_CLAIM = "iss";
    private static final String AUD_CLAIM = "aud";
    private static final String V1_AUD_PREFIX = "api://";
    private static final String V1_ISSUER_FORMAT = "https://%s/%s/";
    private static final String V2_ISSUER_FORMAT= "https://%s/%s/v2.0";


    private HashSet<String> acceptedIssuers = new HashSet<String>();
    private final String applicationId;

    public Microsoft Entra IDClaimsVerifier(final String[] Microsoft Entra IDAliases, final String[] acceptedTenants, final String applicationId) {
        // In production, you'd want to get a valid list of issuers from:
        // https://login.microsoftonline.com/common/discovery/instance?authorization_endpoint=https://login.microsoftonline.com/common/oauth2/v2.0/authorize&api-version=1.1
        // You must get all the values under the metadata[].aliases[] properties.

        Assert.notEmpty(Microsoft Entra IDAliases, "Microsoft Entra IDAliases cannot be empty");
        for (final String issuer : Microsoft Entra IDAliases) {
            Assert.notNull(issuer, "Microsoft Entra IDAlias value cannot be null");
        }

        Assert.notEmpty(acceptedTenants, "acceptedTenants cannot be empty");
        for (final String tenant : acceptedTenants) {
            Assert.notNull(tenant, "acceptedTenant value cannot be null");
        }

        Assert.notNull(applicationId, "applicationId (for audience claim) cannot be null");

        this.applicationId = applicationId;

        generateAcceptedIssuers(Microsoft Entra IDAliases, acceptedTenants);
    }

    private void generateAcceptedIssuers(final String[] Microsoft Entra IDAliases, final String[] acceptedTenants) {
        for (int i = 0; i < Microsoft Entra IDAliases.length; i++) {
            for (int j = 0; j < acceptedTenants.length; j++){
                acceptedIssuers.add(String.format(V1_ISSUER_FORMAT, Microsoft Entra IDAliases[i], acceptedTenants[j]));
                acceptedIssuers.add(String.format(V2_ISSUER_FORMAT, Microsoft Entra IDAliases[i], acceptedTenants[j]));
            }
        }
    }

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
}
