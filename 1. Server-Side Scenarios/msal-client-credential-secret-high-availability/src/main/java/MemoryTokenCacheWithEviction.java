import com.google.common.cache.*;

import java.util.concurrent.CompletableFuture;

public class MemoryTokenCacheWithEviction extends MsalAbstractTokenCacheProvider {

    private static Cache<String, String> cache = null;
    private long maxSize = 0;

    /**
     * Creates an in-memory token cache based on https://github.com/google/guava/, which limits the maximum size to 100k token entries.
     * For client_credentials (app to app tokens), there is 1 entry for each tenant and each resource, around 2000 characters
     * For user tokens, there is 1 entry per account, tenant and resource, around 10000 characters.
     *
     * The memory consumption depends on the VM and cannot be inferred, but a rough approximation is 2-3 bytes per character. 100k entries should
     * limit the memory consumption to 500 MB.
     *
     *
     * @param cacheKey
     */
    MemoryTokenCacheWithEviction(String cacheKey) {

        this(cacheKey, 100 * 1000 );

    }

    /**
     * Creates an in-memory token cache based on https://github.com/google/guava/, which limits the maximum size to a certain number of characters.
     *
     * @param cacheKey - for app tokens, this should include tenant_id; for user tokens it should include account id
     * @param maxSize - max number of string characters the token cache can hold. An app token is approx 2000 characters and a user token entry is around 10000 characters.
     */
    MemoryTokenCacheWithEviction(String cacheKey, long maxSize) {

        super(cacheKey);

        if (cache==null) {
            this.maxSize = maxSize;
            cache = CacheBuilder.newBuilder()
                    .maximumSize(maxSize)
                    .build();
        }
    }

    @Override
    protected CompletableFuture<Void> WriteToCache(String cacheKey, String payload) {
        cache.put(cacheKey, payload);

        return CompletableFuture.completedFuture(null);

    }

    @Override
    protected CompletableFuture<String> ReadFromCache(String cacheKey) {

        String result = cache.getIfPresent(cacheKey);
        return CompletableFuture.completedFuture(result);
    }


}
