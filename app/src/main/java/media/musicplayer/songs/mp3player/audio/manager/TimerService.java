package media.musicplayer.songs.mp3player.audio.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import media.musicplayer.songs.mp3player.audio.receiver.AlarmReciever;
import media.musicplayer.songs.mp3player.audio.utils.Constants;


public class TimerService extends Service {
    private final IBinder musicBind = new MusicBinder();
    private static final String TAG = TimerService.class.getSimpleName();
    private long timeDifference;
    public CountDownTimerClass countDown;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e(TAG, "Timer Started");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        try {
            cancelAlarm();
            Log.e(TAG, "Timer Stop");
            countDown.cancel();
            super.onDestroy();
            Log.e(TAG, "Timer Stopped");
            stopSelf();
        } catch (Exception ex) {

        }
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this,
                AlarmReciever.class);
        PendingIntent morningIntent = PendingIntent.getBroadcast(this, 9999,
                intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("timerset", 0).commit();
        sharedPreferences.edit().putInt("timer_action", 0).commit();
        alarmManager.cancel(morningIntent);
        morningIntent.cancel();
    }
    public void scheduleAlarm(long milisecond, boolean isStopPlayback) {
        Long time = System.currentTimeMillis() + milisecond;

        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        intentAlarm.putExtra("stop_playback", isStopPlayback);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(this, 9999, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Initialize and start the counter
        if (intent != null) {
            timeDifference = intent.getExtras().getLong("timer", 0);
            int action = intent.getExtras().getInt("action",0);
            Log.e("TIMER", "" + timeDifference);
            if (timeDifference != 0) {
                countDown = new CountDownTimerClass(timeDifference + 1000, 1000);
                countDown.start();
                scheduleAlarm(timeDifference, (action == 0) ? true : false);
            }
        }
        return START_STICKY;
    }


    //get difference between start and end time of Hokus focus time period
    private void getHokusFocusDifference() {
        timeDifference = 10000000;
    }

    public class MusicBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

    }
}
