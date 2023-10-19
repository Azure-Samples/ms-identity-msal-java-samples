import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Bridges the MSAL's token caching API with a more standard Read(key) / Write(key, payload) API.
 */
public abstract class MsalAbstractTokenCacheProvider implements ITokenCacheAccessAspect  {

    private final String cacheKey;

    /**
     * Constructor.
     * @param cacheKey - currently the app dev needs to configure the cache key. In the future, MSAL will suggest this.
     */
    MsalAbstractTokenCacheProvider(String cacheKey) {
        this.cacheKey = cacheKey;
    }


    public void beforeCacheAccess(ITokenCacheAccessContext context) {
        try {
            String content = ReadFromCache(cacheKey).get();

            context.tokenCache().deserialize(content);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void afterCacheAccess(ITokenCacheAccessContext context) {
        if (context.hasCacheChanged())
        {
            String cacheContent = context.tokenCache().serialize();
            WriteToCache(cacheKey, cacheContent);
        }
    }

    protected abstract CompletableFuture<Void> WriteToCache(String cacheKey, String payload);

    protected abstract CompletableFuture<String> ReadFromCache(String cacheKey);
}

