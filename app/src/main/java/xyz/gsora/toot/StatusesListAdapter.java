package xyz.gsora.toot;

import MastodonTypes.Boost;
import MastodonTypes.MediaAttachment;
import MastodonTypes.Status;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import io.realm.RealmList;
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

    private Timeline.TimelineContent timelineContent;

    public StatusesListAdapter(RealmResults<Status> data, String locale, Context parentCtx, Timeline.TimelineContent timelineContent
    ) {
        super(data, true);
        String systemLocale = locale;
        this.parentCtx = parentCtx;
        setHasStableIds(true);
        this.timelineContent = timelineContent;
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
                viewHolder = new RowViewHolder(toot, TOOT, parentCtx, timelineContent);
                break;
            case TOOT_BOOST:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + TOOT_BOOST);
                }
                View tootBoost = inflater.inflate(R.layout.status_boosted_toot, parent, false);
                viewHolder = new RowViewHolder(tootBoost, TOOT_BOOST, parentCtx, timelineContent);
                break;
            case TOOT_BOOST_CW:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + TOOT_BOOST_CW);
                }
                View tootBoostCw = inflater.inflate(R.layout.status_boosted_toot_cw, parent, false);
                viewHolder = new RowViewHolder(tootBoostCw, TOOT_BOOST_CW, parentCtx, timelineContent);
                break;
            case TOOT_CW:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onCreateViewHolder: type found: " + TOOT_CW);
                }
                View tootCw = inflater.inflate(R.layout.status_toot_cw, parent, false);
                viewHolder = new RowViewHolder(tootCw, TOOT_CW, parentCtx, timelineContent);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        holder.bindData(getItem(holder.getAdapterPosition()));
        Status s = holder.data;
        Boost sb = s.getReblog();

        if (BuildConfig.DEBUG) {
            if (s.getMediaAttachments().size() > 0) {
                Log.d(TAG, "onBindViewHolder: found status with media attachments!");
                s.getMediaAttachments().forEach(
                        mediaAttachment -> Log.d(TAG, "media: \t" + mediaAttachment.getPreviewUrl())
                );
            }

            if (sb != null && sb.getMediaAttachments().size() > 0) {
                Log.d(TAG, "onBindViewHolder: found status with boost, and media attachments!");
                s.getMediaAttachments().forEach(
                        mediaAttachment -> Log.d(TAG, "media: \t" + mediaAttachment.getPreviewUrl())
                );
            }
        }

        if (s.getThisIsABoost()) { // this is a boost
            setStatusViewTo(sb.getAccount().getDisplayName(), sb.getContent(), sb.getAccount().getAvatar(), s.getAccount().getDisplayName(), sb.getCreatedAt(), holder, sb.getSpoilerText(), sb.getMediaAttachments(), sb.getSensitive());
        } else {
            setStatusViewTo(s.getAccount().getDisplayName(), s.getContent(), s.getAccount().getAvatar(), null, s.getCreatedAt(), holder, s.getSpoilerText(), s.getMediaAttachments(), s.getSensitive());
        }

    }

    @Override
    public int getItemViewType(int position) {
        Status toot = getItem(position);
        // if getThisIsABoost(), cw == null, it's a boost
        if (toot.getReblog() != null) {
            Log.d(TAG, "getItemViewType: got boost, spoilerText len:" + toot.getSpoilerText().length());
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

    private void setStatusViewTo(String author, String content, String avatar, String booster, String timestamp, RowViewHolder holder, String spoilerText, RealmList<MediaAttachment> mediaAttachment, boolean sensitiveContent) {

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

        // if there are boost and star button, use the correct available data
        if (holder.star != null && holder.boost != null) {
            boolean reblogged = false;
            boolean faved = false;

            if (holder.data.getReblog() != null) {
                reblogged = holder.data.getReblog().getReblogged();
            } else {
                reblogged = holder.data.getReblogged();
            }

            if (holder.data.getReblog() != null) {
                faved = holder.data.getReblog().getFavourited();
            } else {
                faved = holder.data.getFavourited();
            }

            if (reblogged) {
                holder.boost.setImageDrawable(ContextCompat.getDrawable(parentCtx, R.drawable.ic_autorenew_blue_500_24dp));
            } else {
                holder.boost.setImageDrawable(ContextCompat.getDrawable(parentCtx, R.drawable.ic_autorenew_black_24dp));
            }

            if (faved) {
                holder.star.setImageDrawable(ContextCompat.getDrawable(parentCtx, R.drawable.ic_stars_yellow_600_24dp));
            } else {
                holder.star.setImageDrawable(ContextCompat.getDrawable(parentCtx, R.drawable.ic_stars_black_24dp));
            }
        }


        // set media if any
        if (mediaAttachment != null && mediaAttachment.size() > 0) {
            holder.masterImageContainer.setVisibility(View.VISIBLE);

            holder.imageContainerFirst.setVisibility(View.GONE);
            holder.imageContainerSecond.setVisibility(View.GONE);

            holder.firstImage.setVisibility(View.GONE);
            holder.secondImage.setVisibility(View.GONE);
            holder.thirdImage.setVisibility(View.GONE);
            holder.fourthImage.setVisibility(View.GONE);

            for (int i = 0; i < mediaAttachment.size(); i++) {
                putImageInContainer(mediaAttachment.get(i).getPreviewUrl(), i, holder, sensitiveContent);
            }
            switch (mediaAttachment.size() - 1) {
                case 0:
                case 1:
                    holder.imageContainerFirst.setVisibility(View.VISIBLE);
                    break;
                case 2:
                case 3:
                    holder.imageContainerFirst.setVisibility(View.VISIBLE);
                    holder.imageContainerSecond.setVisibility(View.VISIBLE);
                    break;
            }
        } else {
            holder.masterImageContainer.setVisibility(View.GONE);
        }

        holder.status.setText(CoolHtml.html(content));


    }

    private void hideFirstMediaContainer(RowViewHolder holder) {
        LinearLayout.LayoutParams l = (LinearLayout.LayoutParams) holder.imageContainerFirst.getLayoutParams();
        l.height = 0;
        holder.imageContainerFirst.setLayoutParams(l);
    }

    private void hideSecondMediaContainer(RowViewHolder holder) {
        LinearLayout.LayoutParams l = (LinearLayout.LayoutParams) holder.imageContainerSecond.getLayoutParams();
        l.height = 0;
        holder.imageContainerSecond.setLayoutParams(l);
    }

    private void setImageOrSensitive(String url, ImageView imageView, boolean sensitiveContent) {
        if (!sensitiveContent) {
            Glide
                    .with(parentCtx)
                    .load(url)
                    .crossFade()
                    .into(imageView);
        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.GRAY));
        }
        imageView.setVisibility(View.VISIBLE);
    }

    private void putImageInContainer(String url, int index, RowViewHolder holder, boolean sensitiveContent) {
        switch (index) {
            case 0:
                setImageOrSensitive(url, holder.firstImage, sensitiveContent);
                break;
            case 1:
                setImageOrSensitive(url, holder.secondImage, sensitiveContent);
                break;
            case 2:
                setImageOrSensitive(url, holder.thirdImage, sensitiveContent);
                break;
            case 3:
                setImageOrSensitive(url, holder.fourthImage, sensitiveContent);
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public long getItemId(int index) {
        return index;
    }

}
