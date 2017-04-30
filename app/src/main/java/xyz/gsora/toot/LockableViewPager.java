package xyz.gsora.toot;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gsora on 4/30/17.
 * <p>
 * A ViewPager that doesn't scroll.
 * Credits: https://gist.github.com/nesquena/898db22a38747bd9bc19#file-lockableviewpager-java
 */
public class LockableViewPager extends ViewPager {

    private boolean swipeable;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.swipeable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.swipeable && super.onTouchEvent(event);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.swipeable && super.onInterceptTouchEvent(event);

    }

    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }
}