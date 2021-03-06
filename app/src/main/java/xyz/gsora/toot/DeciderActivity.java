package xyz.gsora.toot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DeciderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decider);

        if (Toot.hasLoggedIn()) {
            startActivity(new Intent(this, TimelineFragmentContainer.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
