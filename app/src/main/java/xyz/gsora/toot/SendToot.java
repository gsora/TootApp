package xyz.gsora.toot;

import android.content.Context;
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

public class SendToot extends AppCompatActivity {

    private static final String TAG = SendToot.class.getSimpleName();

    @BindView(R.id.toot_content)
    EditText toot_content;
    @BindView(R.id.characters_remaining)
    TextView characters_remaining;
    ColorStateList oldColors;
    private MenuItem send_toot_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_toot);
        setTitle("Send toot");

        ButterKnife.bind(this);
        SetupCharacterCounter();

        oldColors = characters_remaining.getTextColors();
    }

    /*
    Enable the "send" button
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_toot_menu, menu);
        send_toot_menu = menu.findItem(R.id.send_toot_button);

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
                characters_remaining.setText(len.toString());

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

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, String.valueOf(item.getItemId()));
                rV.setImageDrawable(item.getIcon());
                return true;
            }
        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(SendToot.this, (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }
}
