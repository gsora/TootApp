package xyz.gsora.toot;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.gsora.toot.Mastodon.ToastMaker;

import java.util.ArrayList;
import java.util.Locale;

public class SendToot extends AppCompatActivity {

    public static final String REPLY_ACTION = "xyz.gsora.toot.ReplyToStatus";
    public static final String REPLY_TO = "xyz.gsora.toot.ReplyTo";
    public static final String REPLY_TO_ID = "xyz.gsora.toot.ReplyToId";

    private static final String TAG = SendToot.class.getSimpleName();

    @BindView(R.id.toot_content)
    EditText toot_content;
    @BindView(R.id.characters_remaining)
    TextView characters_remaining;
    private ColorStateList oldColors;
    private MenuItem send_toot_menu;
    private String replyToId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_toot);
        setTitle("Send toot");
        replyToId = null;

        ButterKnife.bind(this);
        SetupCharacterCounter();

        oldColors = characters_remaining.getTextColors();

        Intent reply = getIntent();
        if (reply != null) {
            if (reply.getAction() != null && reply.getAction().equals(REPLY_ACTION)) {
                replyToId = reply.getStringExtra(REPLY_TO_ID);
                StringBuilder handlesString = new StringBuilder();
                ArrayList<String> handles = reply.getStringArrayListExtra(REPLY_TO);
                for (String s :
                        handles) {
                    handlesString.append("@").append(s).append(" ");

                }
                toot_content.append(handlesString.toString());
            }
        }
    }

    /*
    Enable the "send" button
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_toot_menu, menu);
        send_toot_menu = menu.findItem(R.id.send_toot_button);

        send_toot_menu.setOnMenuItemClickListener((MenuItem m) -> {
            Intent sendStatus = new Intent(getApplicationContext(), PostStatus.class);
            sendStatus.putExtra(PostStatus.STATUS, toot_content.getText().toString());
            sendStatus.putExtra(PostStatus.REPLYID, replyToId);
            getApplicationContext().startService(sendStatus);
            finish();
            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }

    private Integer GetTextColor(Context context, int chars) {
        if (chars <= 50) {
            return ContextCompat.getColor(context, R.color.colorPrimary);
        }

        if (chars <= 200) {
            return ContextCompat.getColor(context, R.color.colorAccent);
        }

        return null;
    }

    private void SetupCharacterCounter() {
        TextWatcher characterWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer len = 500 - toot_content.length();
                characters_remaining.setText(String.format(Locale.getDefault(), "%d", len));

                Integer color = GetTextColor(getApplicationContext(), len);

                if (color != null) {
                    characters_remaining.setTextColor(color);
                } else {
                    characters_remaining.setTextColor(oldColors);
                }

                if (send_toot_menu != null) {
                    if (len <= 0) {
                        send_toot_menu.setEnabled(false);
                    } else {
                        send_toot_menu.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        toot_content.addTextChangedListener(characterWatcher);
    }

    public void showVisibilityMenu(View v) {
        final ImageButton rV = (ImageButton) v;
        PopupMenu popup = new PopupMenu(SendToot.this, v);
        popup.getMenuInflater().inflate(R.menu.set_visibility_menu, popup.getMenu());

        popup.setOnMenuItemClickListener((MenuItem item) -> {
                Log.d(TAG, String.valueOf(item.getItemId()));
                rV.setImageDrawable(item.getIcon());
                switch(item.getItemId()) {
                    case R.id.visibility_public:
                        ToastMaker.buildToasty(this, "Your post will appear in public timelines").show();
                        break;
                    case R.id.visibility_unlisted:
                        ToastMaker.buildToasty(this, "Your post will not appear public timelines").show();
                        break;
                    case R.id.visibility_private:
                        ToastMaker.buildToasty(this, "Your post will appear to followers only").show();
                        break;
                    case R.id.visibility_direct:
                        ToastMaker.buildToasty(this, "Your post will appear to mentioned user only").show();
                        break;
                    default:
                        Log.d(TAG, "memes");

                }
                return true;
        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(SendToot.this, (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

}
