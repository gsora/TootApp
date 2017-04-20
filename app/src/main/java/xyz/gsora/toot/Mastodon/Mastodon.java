package xyz.gsora.toot.Mastodon;

import MastodonTypes.AppCreationResponse;
import MastodonTypes.OAuthResponse;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.gsora.toot.Toot;

/**
 * Created by gsora on 4/20/17.
 * <p>
 * This is the Mastodon API implementation class.
 */
public class Mastodon {

    /**
     * The URI where the Mastodon instance will return OAuth codes.
     */
    public static final String REDIRECT_URI = "https://xyz.gsora.toot/oauth";

    /**
     * Standard scopes.
     */
    public static final String SCOPES = "read write follow";

    private static Mastodon ourInstance = new Mastodon();

    private Mastodon() {
    }

    /**
     * Get an hold of the Mastodon.
     *
     * @return the Mastodon singleton.
     */
    public static Mastodon getInstance() {
        return ourInstance;
    }

    /**
     * Build a Retrofit instance with JSON converter and a RxJava call adapter.
     *
     * @return Retrofit instance
     */
    private Retrofit buildRxRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Toot.getInstanceURL())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * Generate an application name
     *
     * @return string containing the application name
     */
    private String generateApplicationName() {
        return "TootApp-" + Toot.getUsername();
    }

    /**
     * Returns an Observable with the Mastodon API application process response.
     *
     * @return Observable with App creation response.
     */
    public Observable<AppCreationResponse> createMastodonApplication() {
        return buildRxRetrofit().create(API.class).createMastodonApplication(
                generateApplicationName(),
                REDIRECT_URI,
                SCOPES
        );
    }

    /**
     * Returns an Observable that exposes an OAuth token response.
     *
     * @param code authorization code received during the browser authentication section.
     * @return Observable with OAuth token response.
     */
    public Observable<OAuthResponse> requestOAuthTokens(String code) {
        return buildRxRetrofit().create(API.class).requestOAuthTokens(
                Toot.getClientID(),
                Toot.getClientSecret(),
                REDIRECT_URI,
                "authorization_code",
                code
        );
    }
}
