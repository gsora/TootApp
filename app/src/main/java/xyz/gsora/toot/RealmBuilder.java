package xyz.gsora.toot;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static xyz.gsora.toot.Timeline.*;

/**
 * Created by gsora on 5/5/17.
 * <p>
 * Build a Realm configuration based on the destination timeline content
 */
public class RealmBuilder {

    public static Realm getRealmForTimelineContent(Timeline.TimelineContent timelineContent) {
        RealmConfiguration config = null;

        switch (timelineContent) {
            case TIMELINE_MAIN:
                config = new RealmConfiguration.Builder().name(TIMELINE_MAIN).build();
                break;
            case TIMELINE_LOCAL:
                config = new RealmConfiguration.Builder().name(TIMELINE_LOCAL).build();
                break;
            case TIMELINE_FEDERATED:
                config = new RealmConfiguration.Builder().name(TIMELINE_FEDERATED).build();
                break;
            case FAVORITES:
                config = new RealmConfiguration.Builder().name(FAVORITES).build();
                break;
            case NOTIFICATIONS:
                config = new RealmConfiguration.Builder().name(NOTIFICATIONS).build();
                break;
        }

        return Realm.getInstance(config);
    }
}
