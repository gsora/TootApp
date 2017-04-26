package xyz.gsora.toot.Mastodon;

import MastodonTypes.AppCreationResponse;
import MastodonTypes.OAuthResponse;
import MastodonTypes.Status;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.Map;

/**
 * Created by gsora on 4/20/17.
 * <p>
 * Mastodon API interface.
 */
public interface API {

    @FormUrlEncoded
    @POST("api/v1/apps")
    Observable<AppCreationResponse> createMastodonApplication(
            @Field("client_name") String clientName,
            @Field("redirect_uris") String redirectUri,
            @Field("scopes") String scopes
    );

    @FormUrlEncoded
    @POST("oauth/token")
    Observable<OAuthResponse> requestOAuthTokens(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("scope") String scope
    );

    @GET("api/v1/timelines/home")
    Observable<Response<Status[]>> getHomeTimeline(
            @Header("Authorization") String authBearer
    );

    @GET
        // to use with Links forward page
    Observable<Response<Status[]>> getHomeTimeline(
            @Header("Authorization") String authBearer,
            @Url String url
    );

    @GET("/api/v1/timelines/public")
    Observable<Response<Status[]>> getPublicTimeline(
            @Header("Authorization") String authBearer,
            @Query("local") String local
    );

    @GET("/api/v1/timelines/public")
    Observable<Response<Status[]>> getPublicTimeline(
            @Header("Authorization") String authBearer,
            @Query("local") String local,
            @Url String url
    );

    @FormUrlEncoded
    @POST("api/v1/statuses")
    Observable<Response<Status>> postStatus(
            @Header("Authorization") String authBearer,
            @FieldMap(encoded = false) Map<String, Object> fields
    );
}
