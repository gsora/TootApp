package xyz.gsora.toot;

import MastodonTypes.Status;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Response;
import xyz.gsora.toot.Mastodon.Mastodon;

import java.util.Arrays;
import java.util.Locale;

public class UserTimeline extends AppCompatActivity {

    private static final String TAG = UserTimeline.class.getSimpleName();
    private static String systemLocale;
    @BindView(R.id.statuses_list)
    RecyclerView statusList;
    @BindView(R.id.newToot)
    FloatingActionButton newTootFAB;
    @BindView(R.id.userTimelineRefresh)
    SwipeRefreshLayout refresh;
    MenuItem toot_settings_button;
    Realm realm;
    Mastodon m;
    StatusesListAdapter adapter;
    private String nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_timeline);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        m = Mastodon.getInstance();
        nextPage = null;
        systemLocale = Locale.getDefault().getLanguage();
        setUpRecyclerView(systemLocale);

        // setup the refresh listener
        setupRefreshListener();

        // get some data as soon as possible
        pullData(false);


        // Setup FAB action
        newTootFAB.setOnClickListener((View v) -> {
                Intent i = new Intent(UserTimeline.this, SendToot.class);
                startActivity(i);
        });

        if (BuildConfig.DEBUG) {
            Log.d(TAG, Toot.debugSettingsStorage());
        }
    }

    public void updateData(Response<Status[]> statuses) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(Arrays.asList(statuses.body()));
        realm.commitTransaction();
        refresh.setRefreshing(false);

        String links = statuses.headers().get("Link");
        Log.d(TAG, links);
    }

    // add the settings button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu_timeline, menu);
        toot_settings_button = menu.findItem(R.id.toot_settings_button);

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpRecyclerView(String locale) {
        RealmResults<Status> statuses = realm.where(Status.class).findAllSorted("createdAt", Sort.DESCENDING);
        adapter = new StatusesListAdapter(statuses, locale, getApplicationContext());
        statusList.setLayoutManager(new LinearLayoutManager(this));
        statusList.setAdapter(adapter);
        statusList.setHasFixedSize(true);
    }

    private void setupRefreshListener() {
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            pullData(false);
        });
    }

    private void pullData(Boolean isPage) {
        Observable<Response<Status[]>> home;
        if (!isPage) {
            home = m.getHomeTimeline();
        } else {
            home = m.getHomeTimeline(nextPage);
        }
        home
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::updateData
                );
    }

}
