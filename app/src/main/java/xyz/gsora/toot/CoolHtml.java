package xyz.gsora.toot;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

/**
 * Created by gsora on 4/10/17.
 */
public class CoolHtml {

    public static Spanned html(String s) {
        if(s.length() <= 0) {
            Log.i("CoolHtml", s);
            return new SpannableString("");
        }
        Spanned old;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            old = Html.fromHtml(s, gimmeHtmlModeIfAny());
        } else {
            old = Html.fromHtml(s);
        }

        CharSequence text = old;
        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }

        Spanned sp = new SpannableString(text);
        return sp;
    }

    public static Integer gimmeHtmlModeIfAny() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.FROM_HTML_MODE_COMPACT;
        }

        return 0;
    }
}
