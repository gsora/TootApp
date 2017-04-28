package xyz.gsora.toot;

import MastodonTypes.Boost;
import MastodonTypes.Status;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    public StatusesListAdapter(RealmResults<Status> data, String locale, Context parentCtx) {
        super(data, true);
        String systemLocale = locale;
        this.parentCtx = parentCtx;
        setHasStableIds(true);
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_row_layout, parent, false);
        return new RowViewHolder(v);
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

    private void setStatusViewTo(String author, String content, String avatar, String booster, String timestamp, RowViewHolder holder, String spoilerText) {

        holder.statusAuthor.setText(CoolHtml.html(author));
        holder.status.setText(CoolHtml.html(content));
        if (spoilerText.length() > 0) {
            holder.status.setTextSize(0.0f);
            holder.showContentWarning.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) holder.showContentWarning.getLayoutParams();
            p.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            p.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            // ugly hack
            p.bottomMargin = -16;
            holder.showContentWarning.setLayoutParams(p);
            holder.contentWarningText.setTextSize(16.0f);
            holder.contentWarningText.setVisibility(View.VISIBLE);
            holder.contentWarningText.setText(spoilerText);
        } else {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) holder.showContentWarning.getLayoutParams();
            p.height = 0;
            p.width = 0;
            p.bottomMargin = 0;
            holder.showContentWarning.setLayoutParams(p);
            holder.status.setTextSize(16.0f);
            holder.contentWarningText.setTextSize(0.0f);
            holder.contentWarningText.setText("");
        }

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

        if (booster != null) {
            if (BuildConfig.DEBUG) {
                holder.timestamp.setText(holder.timestamp.getText().toString() + " - boosted by " + booster);
            }
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) holder.boostAuthor.getLayoutParams();
            p.setMargins(0, 0, 0, 16);
            holder.boostAuthor.setLayoutParams(p);
            holder.boostAuthor.setVisibility(View.VISIBLE);
            holder.boostAuthor.setText(String.format(parentCtx.getString(R.string.boostedBy), booster));
            holder.boostAuthor.setTextSize(12.0f);
        } else {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) holder.boostAuthor.getLayoutParams();
            p.setMargins(0, 0, 0, 0);
            holder.boostAuthor.setLayoutParams(p);
            holder.boostAuthor.setVisibility(View.INVISIBLE);
            holder.boostAuthor.setText("");
            holder.boostAuthor.setTextSize(0);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }

}
