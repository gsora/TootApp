package xyz.gsora.toot.Mastodon;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
import es.dmoral.toasty.Toasty;

/**
 * Created by gsora on 5/5/17.
 * <p>
 * Make tasty toasts!
 */
public class ToastMaker {
    public static Toast buildToasty(Context ctx, String s) {
        Toast t = Toasty.info(ctx, s, Toast.LENGTH_SHORT, true);
        t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
        return t;
    }
}
