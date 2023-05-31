package mdp20126376.mdpcw01.musicplayer20126376.model;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * The activities manager
 */
public class ActivityCollector {
    private static final List<AppCompatActivity> activities = new ArrayList<>();

    public static void addActivity(AppCompatActivity activity) {
        activities.add(activity);
    }

    public static void removeActivity(AppCompatActivity activity) {
        activities.remove(activity);
    }

    public static void finishAllActivities() {
        for (AppCompatActivity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}

