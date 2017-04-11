package xyz.gsora.toot;

import MastodonTypes.Status;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by gsora on 4/9/17.
 * <p>
 * Custom adapter, useful for listing some statuses.
 */
public class StatusesListAdapter extends RecyclerView.Adapter<StatusesListAdapter.ViewHolder> {

    private ArrayList<Status> statuses;
    private Context parentCtx;

    public StatusesListAdapter(Status[] s, Context parentCtx) {
        statuses = new ArrayList<Status>(Arrays.asList(s));
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
            setStatusViewTo(sb.getAccount().getDisplayName(), sb.getContent(), sb.getAccount().getAvatar(), s.getAccount().getDisplayName(), holder);
        } else {
            setStatusViewTo(s.getAccount().getDisplayName(), s.getContent(), s.getAccount().getAvatar(), null, holder);
        }

    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    private void setStatusViewTo(String author, String content, String avatar, String booster, StatusesListAdapter.ViewHolder holder) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.statusAuthor.setText(CoolHtml.html(author, Html.FROM_HTML_MODE_COMPACT));
            holder.status.setText(CoolHtml.html(content, Html.FROM_HTML_MODE_COMPACT));
        }

        Picasso.with(parentCtx).load(avatar).into(holder.avatar);

        if (booster != null) {
            //RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) holder.boostAuthor.getLayoutParams();
            //p.setMargins(0, 16, 0, 0);
            //holder.boostAuthor.setLayoutParams(p);

            holder.boostAuthor.setVisibility(View.VISIBLE);
            holder.boostAuthor.setText("Boosted by " + booster);
            holder.boostAuthor.setTextSize(12.0f);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public @BindView(R.id.status_author)
        TextView statusAuthor;
        public @BindView(R.id.status_text)
        TextView status;
        public @BindView(R.id.avatar)
        CircleImageView avatar;
        public @BindView(R.id.boost_author)
        TextView boostAuthor;

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
