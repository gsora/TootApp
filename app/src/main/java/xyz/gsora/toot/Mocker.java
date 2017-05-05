package xyz.gsora.toot;

import MastodonTypes.Status;
import android.content.Context;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by gsora on 4/9/17.
 * <p>
 * Mocker class, loading a static Mastodon /api/v1/timelines/home JSON representation.
 * <p>
 * This thing should disappear when I'll have a working API interface.
 */
@SuppressWarnings("unused")
class Mocker {

    private String JSONStringRep;
    private Status[] MockStatuses;

    public Mocker(Context ctx) {
        JSONStringRep = LoadJSONFromAssetToString(ctx);
        Gson g = new Gson();
        MockStatuses = g.fromJson(JSONStringRep, Status[].class);
    }

    /*
    Private methods
     */

    private String LoadJSONFromAssetToString(Context ctx) {
        try {
            InputStream is = ctx.getAssets().open("test_timeline.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
    Public methods
     */

    public String getJSONStringRep() {
        return JSONStringRep;
    }

    public void setJSONStringRep(String JSONStringRep) {
        this.JSONStringRep = JSONStringRep;
    }

    public Status[] getMockStatuses() {
        return MockStatuses;
    }

    public void setMockStatuses(Status[] mockStatuses) {
        MockStatuses = mockStatuses;
    }
}
