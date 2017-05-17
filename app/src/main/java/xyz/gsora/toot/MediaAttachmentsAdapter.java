package xyz.gsora.toot;

import MastodonTypes.MediaAttachment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import io.realm.RealmList;

/**
 * Created by Gianguido Sorà on 09/05/2017.
 * <p>
 * GridView adapter to show medias from statuses.
 */
public class MediaAttachmentsAdapter extends BaseAdapter {

    private RealmList<MediaAttachment> attachments;
    private Context parentCtx;

    public MediaAttachmentsAdapter(Context context, RealmList<MediaAttachment> attachments) {
        parentCtx = context;
        this.attachments = attachments;
        if (BuildConfig.DEBUG) {
            Log.d(this.getClass().getSimpleName(), "MediaAttachmentsAdapter: elements to display -> " + attachments.size());
        }
    }

    @Override
    public int getCount() {
        return attachments.size();
    }

    @Override
    public MediaAttachment getItem(int position) {
        return attachments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        final MediaAttachment m = attachments.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(parentCtx);
            convertView = layoutInflater.inflate(R.layout.media_layout, null);
        }

        // 3
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.mediaPreviewImage);

        // 4
        Glide
                .with(parentCtx)
                .load(m.getPreviewUrl())
                .placeholder(R.mipmap.missing_avatar)
                .crossFade()
                .into(imageView);

        return convertView;
    }

}
