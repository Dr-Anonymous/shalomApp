package in.shalomworshipcentre.shalom;


import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class Application extends android.app.Application {
    public static final String TODO_GROUP_NAME = "ALL_TODOS";

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Todo.class);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        // Initialize the Parse SDK.
        Parse.initialize(this, "lOjfvl6jcyvHNSFBOHiyID78lJyFYq09SqyBh8CJ", "PHisWmIVwRHFtdX6VmTZOVr4o2V3yWqKWvSowvkn");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("shalom", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }

}