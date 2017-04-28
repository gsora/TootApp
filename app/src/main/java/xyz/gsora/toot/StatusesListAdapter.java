package xyz.gsora.toot;

import MastodonTypes.Boost;
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
import java.util.TimeZone;

/**
 * Created by gsora on 4/9/17.
 * <p>
 * Custom adapter, useful for listing some statuses.
 */
@SuppressWarnings("ALL")
public class StatusesListAdapter extends RealmRecyclerViewAdapter<Status, RowViewHolder> {

    private static final String TAG = StatusesListAdapter.class.getSimpleName();
    private final Context parentCtx;

    private final int TOOT = 0;
    private final int TOOT_CW = 1;
    private final int TOOT_BOOST = 2;
    private final int TOOT_BOOST_CW = 3;

    public StatusesListAdapter(RealmResults<Status> data, String locale, Context parentCtx) {
        super(data, true);
        String systemLocale = locale;
        this.parentCtx = parentCtx;
        setHasStableIds(true);
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TOOT:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + TOOT);
                }
                View toot = inflater.inflate(R.layout.status_toot, parent, false);
                viewHolder = new RowViewHolder(toot, TOOT);
                break;
            case TOOT_BOOST:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + TOOT_BOOST);
                }
                View tootBoost = inflater.inflate(R.layout.status_boosted_toot, parent, false);
                viewHolder = new RowViewHolder(tootBoost, TOOT_BOOST);
                break;
            case TOOT_BOOST_CW:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + TOOT_BOOST_CW);
                }
                View tootBoostCw = inflater.inflate(R.layout.status_boosted_toot_cw, parent, false);
                viewHolder = new RowViewHolder(tootBoostCw, TOOT_BOOST_CW);
                break;
            case TOOT_CW:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + TOOT_CW);
                }
                View tootCw = inflater.inflate(R.layout.status_toot_cw, parent, false);
                viewHolder = new RowViewHolder(tootCw, TOOT_CW);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        holder.data = getItem(holder.getAdapterPosition());
        Status s = holder.data;
        Boost sb = s.getReblog();

        if (s.getThisIsABoost()) { // this is a boost
            setStatusViewTo(sb.getAccount().getDisplayName(), sb.getContent(), sb.getAccount().getAvatar(), s.getAccount().getDisplayName(), sb.getCreatedAt(), holder, sb.getSpoilerText());
        } else {
            setStatusViewTo(s.getAccount().getDisplayName(), s.getContent(), s.getAccount().getAvatar(), null, s.getCreatedAt(), holder, s.getSpoilerText());
        }

    }

    @Override
    public int getItemViewType(int position) {
        Status toot = getItem(position);
        // if getThisIsABoost(), cw == null, it's a boost
        if (toot.getThisIsABoost()) {
            if (toot.getSpoilerText().length() > 0) {
                return TOOT_BOOST_CW;
            }

            return TOOT_BOOST;
        }

        if (toot.getSpoilerText().length() > 0) {
            // cw != null, it's a toot+cw
            return TOOT_CW;
        }

        // Otherwise, it's just a toot
        return TOOT;
    }

    private void setStatusViewTo(String author, String content, String avatar, String booster, String timestamp, RowViewHolder holder, String spoilerText) {

        // Standard setup: timestamp and avatar
        Glide
                .with(parentCtx)
                .load(avatar)
                .crossFade()
                .into(holder.avatar);

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
            d = fmt.parse(timestamp);
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

        // status author, and status
        holder.statusAuthor.setText(CoolHtml.html(author));
        holder.status.setText(CoolHtml.html(content));

        // if holder.boostAuthor != null, set it
        if (holder.boostAuthor != null) {
            if (booster != null) {
                if (BuildConfig.DEBUG) {
                    holder.timestamp.setText(holder.timestamp.getText().toString() + " - boosted by " + booster);
                }
                holder.boostAuthor.setText(String.format(parentCtx.getString(R.string.boostedBy), booster));
            }
        }

        // if holder.contentWarningText != null, set it
        if (holder.contentWarningText != null) {
            if (spoilerText.length() > 0) {
                holder.contentWarningText.setText(spoilerText);
            }
        }

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }

}
