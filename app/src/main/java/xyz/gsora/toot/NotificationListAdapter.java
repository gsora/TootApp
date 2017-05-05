package xyz.gsora.toot;

import MastodonTypes.Notification;
import MastodonTypes.Status;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Created by gsora on 5/1/17.
 * <p>
 * RecyclerView adapter for Notifications.
 */
public class NotificationListAdapter extends RealmRecyclerViewAdapter<Notification, RowViewHolder> {

    private static final String TAG = StatusesListAdapter.class.getSimpleName();
    private final Context parentCtx;

    private final int FAV = 0;
    private final int MENTION = 1;
    private final int BOOST = 2;
    private final int FOLLOW = 3;

    NotificationListAdapter(RealmResults<Notification> data, String locale, Context parentCtx) {
        super(data, true);
        this.parentCtx = parentCtx;
        setHasStableIds(true);
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case FAV:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + FAV);
                }
                View toot = inflater.inflate(R.layout.notification_star, parent, false);
                viewHolder = new RowViewHolder(toot, FAV, parentCtx, Timeline.TimelineContent.NOTIFICATIONS);
                break;
            case MENTION:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + MENTION);
                }
                View tootBoost = inflater.inflate(R.layout.status_toot, parent, false);
                viewHolder = new RowViewHolder(tootBoost, MENTION, parentCtx, Timeline.TimelineContent.NOTIFICATIONS);
                break;
            case BOOST:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + BOOST);
                }
                View tootBoostCw = inflater.inflate(R.layout.notification_boost, parent, false);
                viewHolder = new RowViewHolder(tootBoostCw, BOOST, parentCtx, Timeline.TimelineContent.NOTIFICATIONS);
                break;
            case FOLLOW:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + FOLLOW);
                }
                View tootCw = inflater.inflate(R.layout.notification_follow, parent, false);
                viewHolder = new RowViewHolder(tootCw, FOLLOW, parentCtx, Timeline.TimelineContent.NOTIFICATIONS);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        holder.dataNotification = getItem(holder.getAdapterPosition());
        Status s = holder.dataNotification.getStatus();

        setStatusViewTo(holder.dataNotification, holder);
    }

    @Override
    public int getItemViewType(int position) {
        Notification toot = getItem(position);

        switch (toot.getType()) {
            case "mention":
                return MENTION;
            case "reblog":
                return BOOST;
            case "favourite":
                return FAV;
            case "follow":
                return FOLLOW;
            default:
                return MENTION;
        }
    }

    private void setStatusViewTo(Notification n, RowViewHolder holder) {

        String whoSendsTheNotification = null;

        if (n.getAccount().getDisplayName().length() > 0) {
            whoSendsTheNotification = n.getAccount().getDisplayName();
        } else {
            whoSendsTheNotification = n.getAccount().getUsername();
        }

        // Standard setup: timestamp and avatar
        if (n.getStatus() == null) {
            Glide
                    .with(parentCtx)
                    .load(n.getAccount().getAvatar())
                    .crossFade()
                    .into(holder.avatar);
        } else {
            Glide
                    .with(parentCtx)
                    .load(n.getStatus().getAccount().getAvatar())
                    .crossFade()
                    .into(holder.avatar);
        }

        // format the timestamp according to the device's setting
        SimpleDateFormat fmt;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Toot.getAppContext().getResources().getConfiguration().getLocales().get(0));
        } else {
            fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Toot.getAppContext().getResources().getConfiguration().locale);
        }

        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date d = null;
        try {
            d = fmt.parse(n.getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(parentCtx);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(parentCtx);

        String finalTimestamp;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d);
        cal2.setTime(new Date());
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        if (!sameDay) {
            finalTimestamp = timeFormat.format(d) + " - " + dateFormat.format(d);
        } else {
            finalTimestamp = timeFormat.format(d);
        }

        holder.timestamp.setText(finalTimestamp);

        if (Objects.equals(n.getType(), "favourite") || Objects.equals(n.getType(), "reblog")) {
            Status s = n.getStatus();
            if (s.getAccount().getDisplayName().length() > 0) {
                holder.statusAuthor.setText(CoolHtml.html(s.getAccount().getDisplayName()));
            } else {
                holder.statusAuthor.setText(CoolHtml.html(s.getAccount().getUsername()));
            }
        } else {
            holder.statusAuthor.setText(whoSendsTheNotification);
        }

        if (Objects.equals(n.getType(), "favourite") || Objects.equals(n.getType(), "reblog") || Objects.equals(n.getType(), "mention")) {
            holder.status.setText(CoolHtml.html(n.getStatus().getContent()));
        }

        switch (n.getType()) {
            case "reblog":
                holder.boostedByNotification.setText(String.format(parentCtx.getString(R.string.boostedByNotification), whoSendsTheNotification));
                break;
            case "favourite":
                holder.favouritedBy.setText(String.format(parentCtx.getString(R.string.favouritedBy), whoSendsTheNotification));
                break;
            case "follow":
                holder.followedBy.setText(String.format(parentCtx.getString(R.string.followedBy), n.getAccount().getDisplayName()));
                break;
        }


    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }
}
