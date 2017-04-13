package xyz.gsora.toot;

import MastodonTypes.Status;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by gsora on 4/9/17.
 * <p>
 * Custom adapter, useful for listing some statuses.
 */
public class StatusesListAdapter extends RecyclerView.Adapter<StatusesListAdapter.ViewHolder> {

    private static final String TAG = StatusesListAdapter.class.getSimpleName();

    private ArrayList<Status> statuses;
    private Context parentCtx;
    private static String systemLocale;

    public StatusesListAdapter(Status[] s, Context parentCtx, String locale) {
        statuses = new ArrayList<Status>(Arrays.asList(s));
        systemLocale = locale;
        this.parentCtx = parentCtx;
    }

    public void UpdateStatuses(Status[] s) {
        statuses.addAll(Arrays.asList(s));
        notifyDataSetChanged();
    }

    @Override
    public StatusesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatusesListAdapter.ViewHolder holder, int position) {
        Status s = statuses.get(position);
        Status sb = (Status) s.getReblog();

        if (sb != null) { // this is a boost
            setStatusViewTo(sb.getAccount().getDisplayName(), sb.getContent(), sb.getAccount().getAvatar(), s.getAccount().getDisplayName(), sb.getCreatedAt(), holder);
        } else {
            setStatusViewTo(s.getAccount().getDisplayName(), s.getContent(), s.getAccount().getAvatar(), null, s.getCreatedAt(), holder);
        }

    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    private void setStatusViewTo(String author, String content, String avatar, String booster, String timestamp, StatusesListAdapter.ViewHolder holder) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.statusAuthor.setText(CoolHtml.html(author, Html.FROM_HTML_MODE_COMPACT));
            holder.status.setText(CoolHtml.html(content, Html.FROM_HTML_MODE_COMPACT));
        }

        Picasso.with(parentCtx).load(avatar).into(holder.avatar);

        if (booster != null) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) holder.boostAuthor.getLayoutParams();
            p.setMargins(0, 0, 0, 16);
            holder.boostAuthor.setLayoutParams(p);

            holder.boostAuthor.setVisibility(View.VISIBLE);
            holder.boostAuthor.setText("Boosted by " + booster);
            holder.boostAuthor.setTextSize(12.0f);
        }

        // format the timestamp according to the device's setting
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.forLanguageTag(systemLocale));
        Date d = null;
        try {
            d = fmt.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(parentCtx);
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(parentCtx);

        String finalTimestamp = null;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d);
        cal2.setTime(new Date());
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        if(!sameDay) {
            finalTimestamp = timeFormat.format(d) + " - " + dateFormat.format(d);
        } else {
            finalTimestamp = timeFormat.format(d);
        }

        holder.timestamp.setText(finalTimestamp);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.status_author)
        TextView statusAuthor;
        @BindView(R.id.status_text)
        TextView status;
        @BindView(R.id.avatar)
        CircleImageView avatar;
        @BindView(R.id.boost_author)
        TextView boostAuthor;
        @BindView(R.id.timestamp)
        TextView timestamp;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            status.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /*
    Private methods
     */

}
