package xyz.gsora.toot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class UserTimeline extends AppCompatActivity {

    private static final String TAG = UserTimeline.class.getSimpleName();

    RecyclerView statusList;
    StatusesListAdapter adpt;
    LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_timeline);
        setTitle("Timeline");

        Mocker m = new Mocker(this);
        statusList = (RecyclerView) findViewById(R.id.statuses_list);
        llm = new LinearLayoutManager(getApplicationContext());

        CoolDivider dividerItemDecoration = new CoolDivider(statusList.getContext(),
                llm.getOrientation(),
                72,
                R.id.avatar);

        statusList.setLayoutManager(llm);
        statusList.addItemDecoration(dividerItemDecoration);
        adpt = new StatusesListAdapter(m.getMockStatuses(), this.getApplicationContext());
        statusList.setAdapter(adpt);

    }
}
