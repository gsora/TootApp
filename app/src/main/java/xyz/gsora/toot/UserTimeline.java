package xyz.gsora.toot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.Locale;

public class UserTimeline extends AppCompatActivity {

    private static final String TAG = UserTimeline.class.getSimpleName();

    private static String systemLocale;

    @BindView(R.id.statuses_list)
    RecyclerView statusList;
    @BindView(R.id.newToot)
    FloatingActionButton newTootFAB;

    MenuItem toot_settings_button;
    StatusesListAdapter adpt;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_timeline);
        ButterKnife.bind(this);

        systemLocale = Locale.getDefault().getLanguage();

        final Mocker m = new Mocker(this);
        llm = new LinearLayoutManager(getApplicationContext());

        /*CoolDivider dividerItemDecoration = new CoolDivider(statusList.getContext(),
                llm.getOrientation(),
                72,
                R.id.avatar);*/

        statusList.setLayoutManager(llm);
        //statusList.addItemDecoration(dividerItemDecoration);
        adpt = new StatusesListAdapter(m.getMockStatuses(), this.getApplicationContext(), systemLocale);
        statusList.setAdapter(adpt);

        // Setup FAB action
        newTootFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserTimeline.this, SendToot.class);
                startActivity(i);
            }
        });
    }

    // add the settings button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu_timeline, menu);
        toot_settings_button = menu.findItem(R.id.toot_settings_button);

        return super.onCreateOptionsMenu(menu);
    }

    public void tappedView(View v) {
        Log.d(TAG, "tapped " + v.toString());
    }
}
