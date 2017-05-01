package xyz.gsora.toot;

import MastodonTypes.Mention;
import MastodonTypes.Notification;
import MastodonTypes.Status;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

/**
 * Created by Gianguido Sorà on 28/04/2017.
 * <p>
 * Status row view holder implementation
 */
@SuppressWarnings("unused")
public class RowViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = RowViewHolder.class.getSimpleName();

    public Status data;
    public int type;
    public @Nullable
    @BindView(R.id.status_text)
    TextView status;
    public @BindView(R.id.avatar)
    CircleImageView avatar;
    public @BindView(R.id.timestamp)
    TextView timestamp;
    Notification dataNotification;
    // each data item is just a string in this case
    @BindView(R.id.status_author)
    TextView statusAuthor;
    @Nullable
    @BindView(R.id.boost_author)
    TextView boostAuthor;
    @Nullable
    @BindView(R.id.contentWarningText)
    TextView contentWarningText;
    @Nullable
    @BindView(R.id.showContentWarning)
    Button showContentWarning;
    @Nullable
    @BindView(R.id.reply)
    ImageButton replyButton;
    @Nullable
    @BindView(R.id.boostedByNotification)
    TextView boostedByNotification;
    @Nullable
    @BindView(R.id.favouritedBy)
    TextView favouritedBy;
    @Nullable
    @BindView(R.id.followedBy)
    TextView followedBy;


    @SuppressWarnings("unused")
    private Integer bottomStatus;
    @SuppressWarnings("unused")
    private Integer topStatus;
    @SuppressWarnings("unused")
    private Integer leftStatus;
    @SuppressWarnings("unused")
    private Integer rightStatus;

    RowViewHolder(View v, int type) {
        super(v);
        data = null;
        dataNotification = null;
        this.type = type;
        ButterKnife.bind(this, v);
        if (status != null) {
            status.setMovementMethod(LinkMovementMethod.getInstance());
        }

        // if showContentWarning has been bind
        if (showContentWarning != null) {
            showContentWarning.setOnClickListener((View button) -> {
                if (status.getTextSize() <= 0.0f) {
                    status.setTextSize(16.0f);
                } else {
                    status.setTextSize(0.0f);
                }
            });
        }

        if (replyButton != null) {
            replyButton.setOnClickListener((View button) -> {
                Intent reply = new Intent(Toot.getAppContext(), SendToot.class);
                ArrayList<String> handles = new ArrayList<>();

                if (dataNotification == null) {
                    handles.add(data.getAccount().getAcct());
                    if (!(data.getMentions().size() <= 0)) {
                        for (Mention mention : data.getMentions()) {
                            String acct = mention.getAcct();
                            if (!acct.contains(Toot.getUsername())) {
                                handles.add(mention.getAcct());
                            }
                        }
                    }
                } else {
                    handles.add(dataNotification.getStatus().getAccount().getAcct());
                    if (!(dataNotification.getStatus().getMentions().size() <= 0)) {
                        for (Mention mention : dataNotification.getStatus().getMentions()) {
                            String acct = mention.getAcct();
                            if (!acct.contains(Toot.getUsername())) {
                                handles.add(mention.getAcct());
                            }
                        }
                    }
                }

                reply.setAction(SendToot.REPLY_ACTION);
                reply.putStringArrayListExtra(SendToot.REPLY_TO, handles);
                if (dataNotification == null) {
                    reply.putExtra(SendToot.REPLY_TO_ID, Long.toString(data.getId()));
                } else {
                    reply.putExtra(SendToot.REPLY_TO_ID, dataNotification.getStatus().getId());
                }

                reply.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(reply);

            });
        }
    }
}