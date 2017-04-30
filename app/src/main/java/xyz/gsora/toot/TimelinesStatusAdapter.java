package xyz.gsora.toot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by gsora on 4/29/17.
 *
 * Fragment adapter for the various timeline types.
 */
public class TimelinesStatusAdapter extends FragmentStatePagerAdapter {

    TimelinesStatusAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return Timeline.newInstance(Timeline.TimelineContent.TIMELINE_MAIN);
            case 1:
                return Timeline.newInstance(Timeline.TimelineContent.NOTIFICATIONS);
            case 2:
                return Timeline.newInstance(Timeline.TimelineContent.TIMELINE_LOCAL);
            case 3:
                return Timeline.newInstance(Timeline.TimelineContent.TIMELINE_FEDERATED);
            case 4:
                return Timeline.newInstance(Timeline.TimelineContent.FAVORITES);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
