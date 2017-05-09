package xyz.gsora.toot;

import MastodonTypes.MediaAttachment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import io.realm.RealmList;

/**
 * Created by Gianguido Sorà on 09/05/2017.
 * <p>
 * GridView adapter to show medias from statuses.
 */
public class MediaAttachmentsAdapter extends RecyclerView.Adapter<MediaAttachmentsAdapter.ViewHolder> {

    private RealmList<MediaAttachment> attachments;
    private Context parentCtx;

    public MediaAttachmentsAdapter(Context context, RealmList<MediaAttachment> attachments) {
        parentCtx = context;
        this.attachments = attachments;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parentCtx).inflate(R.layout.media_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaAttachment m = attachments.get(position);
        Glide
                .with(parentCtx)
                .load(m.getPreviewUrl())
                .placeholder(R.mipmap.missing_avatar)
                .crossFade()
                .into(holder.mediaPreview);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mediaPreview)
        ImageView mediaPreview;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
