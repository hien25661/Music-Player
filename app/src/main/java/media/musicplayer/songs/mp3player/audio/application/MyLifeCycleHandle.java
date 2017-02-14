package media.musicplayer.songs.mp3player.audio.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by SF on 28/04/2016.
 */
public class MyLifeCycleHandle implements Application.ActivityLifecycleCallbacks {
    // I use four separate variables here. You can, of course, just use two and
    // increment/decrement them instead of using four and incrementing them all.
    private int resumed;
    private int paused;
    private int started;
    private int stopped;
    private static Context mContext;

    public MyLifeCycleHandle(Context mContext) {
        this.mContext = mContext;
    }

    public MyLifeCycleHandle() {
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;

        Log.e("onActivityResumed: ", "onActivityResumed: ");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        Log.w("test", "application is in foreground: " + (resumed > paused));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        Log.w("test", "application is visible: " + (started > stopped));
        Log.e("onActivityStopped", "onActivityStopped: " + started + "  " + stopped);
        if (started > stopped) {
            isBackground = false;
        } else {
            isBackground = true;
        }

    }

    public static boolean isBackground;
    // If you want a static function you can use to check if your application is
    // foreground/background, you can use the following:
    /*
    // Replace the four variables above with these four
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    // And these two public static functions
    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }
    */
}
