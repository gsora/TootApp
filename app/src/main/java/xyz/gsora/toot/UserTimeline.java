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
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int calls = 0;
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
        realm = Realm.getDefaultInstance();
        m = Mastodon.getInstance();
        nextPage = null;
        systemLocale = Locale.getDefault().getLanguage();
        setUpRecyclerView(systemLocale);

        emptyRealm();

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

    // add the settings button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu_timeline, menu);
        toot_settings_button = menu.findItem(R.id.toot_settings_button);

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpRecyclerView(String locale) {
        RealmResults<Status> statuses = realm.where(Status.class).findAllSortedAsync("id", Sort.DESCENDING);
        adapter = new StatusesListAdapter(statuses, locale, getApplicationContext());
        llm = new LinearLayoutManager(this);
        statusList.setLayoutManager(llm);
        statusList.setAdapter(adapter);
        statusList.setHasFixedSize(true);

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
                            Log.d(TAG, "end of the list!");
                            pullData(true);
                        }
                    }
                }
            }
        });
    }

    private void setupRefreshListener() {
        refresh.setOnRefreshListener(() -> {
            pullData(false);
        });
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

    public void updateDataError(Throwable error) {
        Log.d(TAG, error.toString());
        refresh.setRefreshing(false);
        Toasty.error(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_SHORT, true).show();

    }

    public void updateData(Response<Status[]> statuses) {
        debugCallNums();
        realm.executeTransaction((Realm r) -> {
            for (Status s : statuses.body()) {
                r.insertOrUpdate(s);
            }
        });
        refresh.setRefreshing(false);

        String links = statuses.headers().get("Link");
        Link next = LinkParser.parseNext(links);
        nextPage = next.getURL();
        loading = true;
    }

    private void emptyRealm() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    private void debugCallNums() {
        Log.d(TAG, "number of calls: " + ++calls);
    }
}
