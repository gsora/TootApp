package xyz.gsora.toot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.roughike.bottombar.BottomBar;

public class TimelineFragmentContainer extends AppCompatActivity {

    private static final String TAG = TimelineFragmentContainer.class.getSimpleName();

    @BindView(R.id.newToot)
    FloatingActionButton newTootFAB;
    @BindView(R.id.BottomNavigation)
    BottomBar bottomBar;
    @BindView(R.id.viewPager)
    LockableViewPager viewPager;

    private TimelinesStatusAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_fragment_container);
        ButterKnife.bind(this);

        // setup the bottom navigation bar
        setupBottomBar();

        // Setup FAB action
        newTootFAB.setOnClickListener((View v) -> {
            Intent i = new Intent(TimelineFragmentContainer.this, SendToot.class);
            startActivity(i);
        });

        if (BuildConfig.DEBUG) {
            Log.d(TAG, Toot.debugSettingsStorage());
        }

        // setup the viewpager
        viewPagerAdapter = new TimelinesStatusAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setSwipeable(false);

        // set the base name
        setTitle(Timeline.TIMELINE_MAIN);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setTitle(Timeline.TIMELINE_MAIN);
                        break;
                    case 1:
                        setTitle(Timeline.NOTIFICATIONS);
                        break;
                    case 2:
                        setTitle(Timeline.TIMELINE_LOCAL);
                        break;
                    case 3:
                        setTitle(Timeline.TIMELINE_FEDERATED);
                        break;
                    case 4:
                        setTitle(Timeline.FAVORITES);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // add the settings button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu_timeline, menu);
        @SuppressWarnings("unused") MenuItem toot_settings_button = menu.findItem(R.id.toot_settings_button);

        return super.onCreateOptionsMenu(menu);
    }

    private void setupBottomBar() {
        bottomBar.setOnTabSelectListener((int tabId) -> {
            viewPager.setCurrentItem(bottomBar.getCurrentTabPosition());
            switch (tabId) {
                case R.id.timeline:
                    viewPager.setCurrentItem(0, false);
                    break;
                case R.id.notifications:
                    viewPager.setCurrentItem(1, false);
                    break;
                case R.id.local:
                    viewPager.setCurrentItem(2, false);
                    break;
                case R.id.federated:
                    viewPager.setCurrentItem(3, false);
                    break;
                case R.id.favorites:
                    viewPager.setCurrentItem(4, false);
                    break;
            }
        });
    }

}
