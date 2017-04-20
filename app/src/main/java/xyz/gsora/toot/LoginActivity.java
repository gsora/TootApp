package xyz.gsora.toot;

import MastodonTypes.AppCreationResponse;
import MastodonTypes.OAuthResponse;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import xyz.gsora.toot.Mastodon.Mastodon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.userAtInstance)
    TextView userAtInstance;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.progress)
    ProgressBar progress;

    private UserTuple ut;
    private Mastodon m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        ut = new UserTuple();
        m = Mastodon.getInstance();
        setupUserStringTest();

        // I truly don't understand why I need to set these here, while they're already been set via XML.
        progress.setVisibility(View.INVISIBLE);
        login.setEnabled(false);

        // parse intent data if any (aka if the browser redirected here)
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri == null || !uri.toString().startsWith(Mastodon.REDIRECT_URI) || Toot.hasLoggedIn()) {
            // TODO: actually handle the case where no data comes from OAuth
            handleApplicationCreationError(new Exception("!!! ERROR !!! --> OAuth code data was null!"));
        } else {
            userAtInstance.setEnabled(false);
            login.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
            handleOAuthReply(uri);
        }

    }

    /**
     * Setup the user/instance URI validator.
     */
    private void setupUserStringTest() {
        userAtInstance.addTextChangedListener(new TextWatcher() {
            private final Pattern USER_INSTANCE_PATTERN = Pattern.compile("(\\w+)@(\\w+\\.\\w+)");

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher p = USER_INSTANCE_PATTERN.matcher(s);

                if (p.find()) {
                    // 1 = username
                    // 2 = instanceURI
                    ut.setUser(p.group(1));
                    ut.setInstanceURI(p.group(2));
                } else {
                    ut.setUser("");
                    ut.setInstanceURI("");
                }

                login.setEnabled(p.matches());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Listener method for clicks on the "login" button.
     *
     * @param view the button who fired the event.
     */
    public void onLoginClick(View view) {
        // start some animations
        progress.setVisibility(View.VISIBLE);

        // disable all the UI components
        userAtInstance.setEnabled(false);
        login.setEnabled(false);

        // save user and instance uri since we're about to log in
        Toot.saveUsernameInstanceTuple(ut);

        Observable<AppCreationResponse> createApp = m.createMastodonApplication();
        createApp
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::handleApplicationCreationRequest,
                        this::handleApplicationCreationError
                );
    }

    /**
     * Handle a succesful application creation request.
     *
     * @param acr a valid AppCreationResponse instance
     */
    private void handleApplicationCreationRequest(AppCreationResponse acr) {
        // since we successfully created an application, we can save its data and the UserTuple
        Toot.saveUsernameInstanceTuple(ut);
        Toot.saveClientID(acr.getClientId());
        Toot.saveClientSecret(acr.getClientSecret());

        // build an URI and throw it into the browser
        Uri destination = Uri.parse("https://" + Toot.getInstanceURI() + "/oauth/authorize").buildUpon()
                .appendQueryParameter("client_id", acr.getClientId())
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", Mastodon.REDIRECT_URI)
                .build();

        Intent browser = new Intent(Intent.ACTION_VIEW, destination);
        startActivity(browser);
    }

    /**
     * Handle an unsuccessful application creation request.
     *
     * @param error the request error
     */
    private void handleApplicationCreationError(Throwable error) {
        // TODO: fix my dumbness
        Log.d(TAG, "application creation error: " + error.toString());

        userAtInstance.setEnabled(true);
        login.setEnabled(true);
    }


    /**
     * Handle a successful OAuth code request.
     *
     * @param uri object containing the URL-encoded code.
     */
    private void handleOAuthReply(Uri uri) {
        String code = null;
        try {
            code = uri.getQueryParameter("code");
        } catch (Exception e) {
            handleApplicationCreationError(e);
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "oauth reply code: " + code);
        }

        // requesting OAuth tokens
        Observable<OAuthResponse> oauth = m.requestOAuthTokens(code);
        oauth
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::handleSuccessfulOAuthReply,
                        this::handleUnsuccessfulOAuthReply
                );
    }

    /**
     * Handle a successful OAuth response.
     *
     * @param oAuthResponse the successful OAuth response.
     */
    private void handleSuccessfulOAuthReply(OAuthResponse oAuthResponse) {
        Toot.markLoggedIn();
        Toot.saveOAuthAccessToken(oAuthResponse.getAccessToken());
        Toot.saveOAuthRefreshToken(oAuthResponse.getRefreshToken());

        // send everyone to the main view!
        Intent timeline = new Intent(this, UserTimeline.class);
        startActivity(timeline);
        finish();
    }

    /**
     * Handle an unsuccessful OAuth response
     *
     * @param error the error fired from the network stack.
     */
    private void handleUnsuccessfulOAuthReply(Throwable error) {
        // TODO: fix my dumbness
        Log.d(TAG, "OAuth error: " + error.toString());

        userAtInstance.setEnabled(true);
        login.setEnabled(true);
        progress.setVisibility(View.INVISIBLE);
    }
}
