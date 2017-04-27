package xyz.gsora.toot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
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

        // start the main timeline fragment
        startFragment(Timeline.TimelineContent.TIMELINE_MAIN);
    }

    private void startFragment(Timeline.TimelineContent timelineContent) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, Timeline.newInstance(timelineContent));
        ft.commitAllowingStateLoss();
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
            switch (tabId) {
                case R.id.timeline:
                    Log.d(TAG, "setupBottomBar: pressed timeline");
                    startFragment(Timeline.TimelineContent.TIMELINE_MAIN);
                    break;
                case R.id.notifications:
                    Log.d(TAG, "setupBottomBar: pressed notifications");
                    startFragment(Timeline.TimelineContent.NOTIFICATIONS);
                    break;
                case R.id.local:
                    Log.d(TAG, "setupBottomBar: pressed local");
                    startFragment(Timeline.TimelineContent.TIMELINE_LOCAL);
                    break;
                case R.id.federated:
                    Log.d(TAG, "setupBottomBar: pressed federated");
                    startFragment(Timeline.TimelineContent.TIMELINE_FEDERATED);
                    break;
                case R.id.favorites:
                    Log.d(TAG, "setupBottomBar: pressed favorites");
                    startFragment(Timeline.TimelineContent.FAVORITES);
                    break;
            }
        });
    }

}
