package xyz.gsora.toot.Mastodon;

import MastodonTypes.AppCreationResponse;
import MastodonTypes.OAuthResponse;
import MastodonTypes.Status;
import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.*;

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
            @Field("code") String code
    );

    @GET("api/v1/timelines/home")
    Observable<Response<Status[]>> getHomeTimeline(
            @Header("Authorization") String authBearer
    );

    @GET
    Observable<Response<Status[]>> getHomeTimeline(
            @Header("Authorization") String authBearer,
            @Url String url
    );
}
