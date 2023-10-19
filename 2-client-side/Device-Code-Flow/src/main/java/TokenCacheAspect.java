// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class TokenCacheAspect implements ITokenCacheAccessAspect {

    private String data;

    public TokenCacheAspect(String fileName) {
        this.data = readDataFromFile(fileName);
    }

    @Override
    public void beforeCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
        iTokenCacheAccessContext.tokenCache().deserialize(data);
    }

    @Override
    public void afterCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
        data = iTokenCacheAccessContext.tokenCache().serialize();
        // you could implement logic here to write changes to file
    }

    private static String readDataFromFile(String resource) {
        try {
            //Determine if sample running from IDE (resource URI starts with 'file') or from a .jar (resource URI starts with 'jar'),
            //  so that sample_cache.json is read properly
            if (TokenCacheAspect.class.getResource("TokenCacheAspect.class").toString().startsWith("file")) {
                URL path = TokenCacheAspect.class.getResource(resource);
                return new String(
                        Files.readAllBytes(
                                Paths.get(path.toURI())));
            }
            else {
                URI uri = TokenCacheAspect.class.getResource(resource).toURI();
                Map<String, String> env = new HashMap<>();
                env.put("create", "true");
                FileSystem fs = FileSystems.newFileSystem(uri, env);
                Path myFolderPath = Paths.get(uri);

                return new String(Files.readAllBytes(myFolderPath));
            }
        } catch (Exception ex){
            System.out.println("Error reading data from file: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}