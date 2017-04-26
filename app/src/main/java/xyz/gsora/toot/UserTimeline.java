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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.roughike.bottombar.BottomBar;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Response;
import xyz.gsora.toot.Mastodon.Link;
import xyz.gsora.toot.Mastodon.LinkParser;
import xyz.gsora.toot.Mastodon.Mastodon;

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
    @BindView(R.id.BottomNavigation)
    BottomBar bottomBar;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int calls = 0;
    private LinearLayoutManager llm;
    private MenuItem toot_settings_button;
    private Realm realm;
    private Mastodon m;
    private StatusesListAdapter adapter;
    private String nextPage;
    private Boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_timeline);
        ButterKnife.bind(this);
        realm = Toot.getRealm();
        m = Mastodon.getInstance();
        nextPage = null;
        systemLocale = Locale.getDefault().getLanguage();
        setUpRecyclerView(systemLocale);

        if (BuildConfig.DEBUG) {
            emptyRealm();
        }

        // setup the refresh listener
        setupRefreshListener();

        // setup the bottom navigation bar
        setupBottomBar();

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

    // add the settings button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu_timeline, menu);
        toot_settings_button = menu.findItem(R.id.toot_settings_button);

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpRecyclerView(String locale) {
        RealmResults<Status> statuses = realm.where(Status.class).findAllSorted("id", Sort.DESCENDING);
        adapter = new StatusesListAdapter(statuses, locale, this);
        llm = new LinearLayoutManager(this);
        statusList.setLayoutManager(llm);
        statusList.setAdapter(adapter);
        statusList.setHasFixedSize(false);

        statusList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            // TODO: replace me
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            pullData(true);
                        }
                    }
                }
            }
        });
    }

    private void setupRefreshListener() {
        refresh.setOnRefreshListener(() ->
                pullData(false)
        );
    }

    private void pullData(Boolean page) {
        refresh.setRefreshing(true);
        Observable<Response<Status[]>> home;
        if (nextPage == null || !page) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "no previous page, loading the first one");
            }
            home = m.getHomeTimeline();
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "got previous page, loading it!");
            }
            home = m.getHomeTimeline(nextPage);
        }
        home
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::updateData,
                        this::updateDataError
                );
    }

    private void updateDataError(Throwable error) {
        Log.d(TAG, error.toString());
        refresh.setRefreshing(false);
        Toasty.error(getApplicationContext(), "Something went wrong :(\n" + error.toString(), Toast.LENGTH_SHORT, true).show();
        loading = true;
    }

    private void updateData(Response<Status[]> statuses) {
        debugCallNums();
        realm.executeTransaction((Realm r) -> {
            for (Status s : statuses.body()) {
                if (s.getReblog() == null) {
                    s.setThisIsABoost(false);
                } else {
                    s.setThisIsABoost(true);
                }
                if (realm.where(Status.class).equalTo("id", s.getId()).count() <= 0) {
                    r.insertOrUpdate(s);
                }
            }
        });

        refresh.setRefreshing(false);

        String links = statuses.headers().get("Link");
        Link next = LinkParser.parseNext(links);
        nextPage = next.getURL();
        loading = true;
    }

    private void emptyRealm() {
        realm.executeTransaction((Realm r) -> r.deleteAll());
    }

    private void debugCallNums() {
        Log.d(TAG, "number of calls: " + ++calls);
    }

    private void setupBottomBar() {
        bottomBar.setOnTabSelectListener((int tabId) -> {
            switch (tabId) {
                case R.id.timeline:
                    Log.d(TAG, "setupBottomBar: pressed timeline");
                    break;
                case R.id.notifications:
                    Log.d(TAG, "setupBottomBar: pressed notifications");
                    break;
                case R.id.local:
                    Log.d(TAG, "setupBottomBar: pressed local");
                    break;
                case R.id.federated:
                    Log.d(TAG, "setupBottomBar: pressed federated");
                    break;
                case R.id.favorites:
                    Log.d(TAG, "setupBottomBar: pressed favorites");
                    break;
            }
        });
    }

}
