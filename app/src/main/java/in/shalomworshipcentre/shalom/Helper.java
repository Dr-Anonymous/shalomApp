package in.shalomworshipcentre.shalom;

import com.parse.ParseConfig;

class Helper {
    private static final long configRefreshInterval = 12 * 60 * 60 * 1000;
    private static long lastFetchedTime;

    // Fetches the config at most once every 12 hours per app runtime
    public static void refreshConfig() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFetchedTime > configRefreshInterval) {
            lastFetchedTime = currentTime;
            ParseConfig.getInBackground();
        }
    }
}