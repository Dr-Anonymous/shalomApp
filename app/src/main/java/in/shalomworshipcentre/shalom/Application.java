package in.shalomworshipcentre.shalom;


import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;

public class Application extends android.app.Application {
    public static final String TODO_GROUP_NAME = "ALL_TODOS";
    boolean notif;

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Todo.class);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Initialize the Parse SDK.
        Parse.initialize(this, "lOjfvl6jcyvHNSFBOHiyID78lJyFYq09SqyBh8CJ", "PHisWmIVwRHFtdX6VmTZOVr4o2V3yWqKWvSowvkn");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        notif = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("notif", true);
        if (notif) {
            ParsePush.subscribeInBackground("shalom");
        } else {
            ParsePush.unsubscribeInBackground("shalom");
        }
    }
}