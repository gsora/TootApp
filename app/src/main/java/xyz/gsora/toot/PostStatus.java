package xyz.gsora.toot;

import MastodonTypes.Status;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Response;
import xyz.gsora.toot.Mastodon.Mastodon;

import java.util.ArrayList;

import static xyz.gsora.toot.Timeline.TIMELINE_MAIN;

/**
 * An {@link IntentService} subclass for handling asynchronous toot sending.
 * <p>
 */
@SuppressWarnings("WeakerAccess")
public class PostStatus extends IntentService {

    public static final String STATUS = "xyz.gsora.toot.extra.status";
    public static final String REPLYID = "xyz.gsora.toot.extra.replyid";
    public static final String MEDIAIDS = "xyz.gsora.toot.extra.mediaids";
    public static final String SENSITIVE = "xyz.gsora.toot.extra.sensitive";
    public static final String SPOILERTEXT = "xyz.gsora.toot.extra.spoilertext";
    public static final String VISIBILITY = "xyz.gsora.toot.extra.visibility";
    private static final String TAG = PostStatus.class.getSimpleName();
    private Realm realm;

    public PostStatus() {
        super("PostStatus");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Mastodon m = Mastodon.getInstance();
        realm = Realm.getInstance(new RealmConfiguration.Builder().name(TIMELINE_MAIN).build());

        if (intent != null) {
            final String status = intent.getStringExtra(STATUS);
            final String replyid = intent.getStringExtra(REPLYID);
            final ArrayList<String> mediaids = intent.getStringArrayListExtra(MEDIAIDS);
            final Boolean sensitive = intent.getBooleanExtra(SENSITIVE, false);
            final String spoilertext = intent.getStringExtra(SPOILERTEXT);
            final Integer visibility = intent.getIntExtra(VISIBILITY, 0);

            Mastodon.StatusVisibility trueVisibility = Mastodon.StatusVisibility.PUBLIC;

            switch (visibility) {
                case 0:
                    trueVisibility = Mastodon.StatusVisibility.PUBLIC;
                    break;
                case 1:
                    trueVisibility = Mastodon.StatusVisibility.DIRECT;
                    break;
                case 2:
                    trueVisibility = Mastodon.StatusVisibility.UNLISTED;
                    break;
                case 3:
                    trueVisibility = Mastodon.StatusVisibility.PRIVATE;
                    break;
            }


            Observable<Response<Status>> post = m.postPublicStatus(status, replyid, mediaids, sensitive, spoilertext, trueVisibility);
            post
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            this::postSuccessful,
                            this::postError
                    );

        }
    }

    private void postSuccessful(Response<Status> response) {
        realm.executeTransaction((Realm r) -> r.insertOrUpdate(response.body()));
        Log.d(TAG, "postSuccessful: post ok!");
    }

    private void postError(Throwable error) {
        Log.d(TAG, "postSuccessful: post error! --> " + error.toString());

    }
}
