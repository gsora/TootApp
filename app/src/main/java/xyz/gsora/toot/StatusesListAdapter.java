package xyz.gsora.toot;

import MastodonTypes.Status;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by gsora on 4/9/17.
 * <p>
 * Custom adapter, useful for listing some statuses.
 */
public class StatusesListAdapter extends RecyclerView.Adapter<StatusesListAdapter.ViewHolder> {

    private Status[] statuses;
    private Context parentCtx;

    public StatusesListAdapter(Status[] s, Context parentCtx) {
        statuses = s;
        this.parentCtx = parentCtx;
    }

    @Override
    public StatusesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatusesListAdapter.ViewHolder holder, int position) {
        Status s = statuses[position];
        Status sb = (Status) s.getReblog();

        if (sb != null) { // this is a boost
            setStatusViewTo(sb.getAccount().getDisplayName(), sb.getContent(), sb.getAccount().getAvatar(), s.getAccount().getDisplayName(), holder);
        } else {
            setStatusViewTo(s.getAccount().getDisplayName(), s.getContent(), s.getAccount().getAvatar(), null, holder);
        }

    }

    @Override
    public int getItemCount() {
        return statuses.length;
    }

    private void setStatusViewTo(String author, String content, String avatar, String booster, StatusesListAdapter.ViewHolder holder) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.statusAuthor.setText(Html.fromHtml(author, Html.FROM_HTML_MODE_COMPACT));
            holder.status.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.statusAuthor.setText(Html.fromHtml(author));
            holder.status.setText(Html.fromHtml(content));
        }

        Picasso.with(parentCtx).load(avatar).into(holder.avatar);

        if (booster != null) {
            holder.boostAuthor.setVisibility(View.VISIBLE);
            holder.boostAuthor.setText("Boosted by " + booster);
        }
    }

    /*
    Private methods
     */

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView statusAuthor;
        public TextView status;
        public ImageView avatar;
        public TextView boostAuthor;

        public ViewHolder(View v) {
            super(v);
            statusAuthor = (TextView) v.findViewById(R.id.status_author);
            status = (TextView) v.findViewById(R.id.status_text);
            status.setMovementMethod(LinkMovementMethod.getInstance());
            avatar = (ImageView) v.findViewById(R.id.avatar);
            boostAuthor = (TextView) v.findViewById(R.id.boost_author);
        }
    }
}
