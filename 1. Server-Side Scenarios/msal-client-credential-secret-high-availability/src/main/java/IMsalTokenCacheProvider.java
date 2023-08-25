//import com.microsoft.aad.msal4j.ITokenCache;
//import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
//
//import java.util.concurrent.CompletableFuture;
//
///***
// * Token cache provider interface
// */
//public interface IMsalTokenCacheProvider {
//
//    void Initialize(ITokenCacheAccessAspect tokenCache);
//
//    /// <summary>
//    /// Clear the user token cache.
//    /// </summary>
//    /// <param name="homeAccountId">HomeAccountId for a user account in the cache.</param>
//    /// <returns>A <see cref="Task"/> that represents a completed clear operation.</returns>
//
//    /**
//     * Clears the user token cache
//     * @param homeAccountId - for the user account in the cache
//     * @return a compleatable future
//     */
//    CompletableFuture<Void> Clear(String homeAccountId);
//}
//
