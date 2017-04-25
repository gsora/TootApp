package xyz.gsora.toot.Mastodon;

import MastodonTypes.AppCreationResponse;
import MastodonTypes.OAuthResponse;
import MastodonTypes.Status;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.gsora.toot.BuildConfig;
import xyz.gsora.toot.Toot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public OkHttpClient logger() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            return client.addInterceptor(interceptor).build();
        } else {
            return client.build();
        }
    }

    /**
     * Build a Retrofit instance with JSON converter and a RxJava call adapter.
     *
     * @return Retrofit instance
     */
    private Retrofit buildRxRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Toot.getInstanceURL())
                .client(logger())
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
                code,
                SCOPES
        );
    }

    /**
     * Returns the first page of the user's home
     *
     * @return an array of Status containing the user's home first page
     */
    public Observable<Response<Status[]>> getHomeTimeline() {
        return buildRxRetrofit().create(API.class).getHomeTimeline(
                Toot.buildBearer()
        );
    }

    /**
     * Returns the page of the user's home located by the URL
     *
     * @param url page to retrieve
     * @return an array of Status containing the user's home at the given URL
     */
    public Observable<Response<Status[]>> getHomeTimeline(String url) {
        return buildRxRetrofit().create(API.class).getHomeTimeline(
                Toot.buildBearer(),
                url
        );
    }

    public Observable<Response<Status>> postPublicStatus(String statusContent, String inReplyToId, List<String> mediaIds, Boolean sensitive, String spoilerText, StatusVisibility statusVisibility) {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("status", statusContent);

        if (inReplyToId != null) {
            fields.put("in_reply_to_id", inReplyToId);
        }

        if (mediaIds != null) {
            fields.put("media_ids", mediaIds);
        }

        if (sensitive) {
            fields.put("sensitive", sensitive);
        }

        if (spoilerText != null) {
            fields.put("spoiler_text", spoilerText);
        }

        switch (statusVisibility) {
            case PUBLIC:
                fields.put("visibility", "public");
                break;
            case DIRECT:
                fields.put("visibility", "direct");
                break;
            case PRIVATE:
                fields.put("visibility", "private");
                break;
            case UNLISTED:
                fields.put("visibility", "unlisted");
                break;
        }

        return buildRxRetrofit().create(API.class).postStatus(
                Toot.buildBearer(),
                fields
        );
    }

    public enum StatusVisibility {
        PUBLIC,
        DIRECT,
        PRIVATE,
        UNLISTED
    }
}
