package xyz.gsora.toot;

import MastodonTypes.Status;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainTimeline.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainTimeline#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainTimeline extends Fragment {

    private static final String TAG = UserTimeline.class.getSimpleName();
    private static String systemLocale;
    @BindView(R.id.statuses_list)
    RecyclerView statusList;
    @BindView(R.id.userTimelineRefresh)
    SwipeRefreshLayout refresh;
    private OnFragmentInteractionListener mListener;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int calls = 0;
    private LinearLayoutManager llm;
    private Realm realm;
    private Mastodon m;
    private StatusesListAdapter adapter;
    private String nextPage;
    private Boolean loading = true;

    public MainTimeline() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainTimeline.
     */
    // TODO: Rename and change types and number of parameters
    public static MainTimeline newInstance() {
        MainTimeline fragment = new MainTimeline();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_timeline, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Timeline");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {

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

        // get some data as soon as possible
        pullData(false);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, Toot.debugSettingsStorage());
        }
    }

    private void setUpRecyclerView(String locale) {
        RealmResults<Status> statuses = realm.where(Status.class).findAllSorted("id", Sort.DESCENDING);
        adapter = new StatusesListAdapter(statuses, locale, getActivity());
        llm = new LinearLayoutManager(getActivity());
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
        Toasty.error(getContext(), "Something went wrong :(\n" + error.toString(), Toast.LENGTH_SHORT, true).show();
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

    private void setupRefreshListener() {
        refresh.setOnRefreshListener(() ->
                pullData(false)
        );
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
