package xyz.gsora.toot;

import MastodonTypes.Status;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Response;
import xyz.gsora.toot.Mastodon.Mastodon;

import java.util.ArrayList;
import java.util.Random;

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
    private NotificationManager nM;

    private int notificationId;

    public PostStatus() {
        super("PostStatus");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Mastodon m = Mastodon.getInstance();
        realm = Realm.getInstance(new RealmConfiguration.Builder().name(TIMELINE_MAIN).build());

        nM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random r = new Random();
        notificationId = r.nextInt();

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

            // create a new "toot sending" notification
            NotificationCompat.Builder mBuilder = buildNotification(
                    "Sending toot",
                    null,
                    true
            );
            nM.notify(notificationId, mBuilder.build());

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
        Log.d(TAG, "postSuccessful: post ok!");

        // toot sent, remove notification
        nM.cancel(notificationId);
    }

    private void postError(Throwable error) {
        Log.d(TAG, "postError: post error! --> " + error.toString());

        // cancel "sending" notification id
        nM.cancel(notificationId);

        // create a new "error" informative notification
        NotificationCompat.Builder mBuilder = buildNotification(
                "Failed to send toot",
                "We had some problems sending your toot, check your internet connection, or maybe the Mastodon instance you're using could be down.",
                false
        );

        nM.notify(notificationId + 1, mBuilder.build());
    }

    /**
     * Builds a {@link NotificationCompat.Builder} with some predefined properties
     *
     * @param title                notification title
     * @param text                 notification body, can be null
     * @param hasUndefinedProgress declare if the notification have to contain an undefined progressbar
     * @return a {@link NotificationCompat.Builder} with the properties passed as parameter.
     */
    private NotificationCompat.Builder buildNotification(String title, String text, Boolean hasUndefinedProgress) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        if (text != null) {
            style.bigText(text);
        }
        mBuilder.setStyle(style);
        mBuilder.setSmallIcon(R.drawable.ic_reply_white_24dp);
        if (hasUndefinedProgress) {
            mBuilder.setProgress(0, 0, true);
        }
        return mBuilder;
    }
}
