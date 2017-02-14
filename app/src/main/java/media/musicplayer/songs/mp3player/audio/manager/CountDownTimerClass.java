package media.musicplayer.songs.mp3player.audio.manager;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.utils.Constants;


/**
 * This class handles the count down timer
 */
public class CountDownTimerClass extends CountDownTimer {

    private static final String TAG = CountDownTimerClass.class.getSimpleName();

    public static final String COUNTDOWN_BROADCAST_RECEIVER = Constants.INTENTFILTER_TIMER;

    private final Intent countDownTimer = new Intent(Constants.INTENTFILTER_TIMER);

    public static boolean isRunning = false;

    public static long focusHourMilli = 0;

    public CountDownTimerClass(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public boolean isCanceled = false;
    @Override
    public void onTick(long millisUntilFinished) {
        isRunning = true;
        this.focusHourMilli = millisUntilFinished;

//        long millis = millisUntilFinished;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

        sendTimerBroadcastTimer(hms, millisUntilFinished, false);


        Log.e("TIMER","*****running timer  " + hms + " Milli Second " + millisUntilFinished);
     //   AppLog.showLog(TAG, "*****running timer  " + hms + " Milli Second " + millisUntilFinished);


    }

    @Override
    public void onFinish() {
        /**
         *The CountDownTimer is not precise, it will return as close to 1 second (in this case)
         * as it can but often that won't be enough to give you what you want so using this way to set "00:00:00"
         * in {@link com.leapfrog.hokusfokus.fragment.ActiveTaskTimer} after countdown finishes
         *
         * http://stackoverflow.com/questions/6810416/android-countdowntimer-shows-1-for-two-seconds
         *
         * http://stackoverflow.com/questions/4824068/how-come-millisuntilfinished-cannot-detect-exact-countdowntimer-intervals
         */
        sendTimerBroadcastTimer("00:00:00", 0, true);

        isRunning = false;

        stopTimer();

     //   AppLog.showLog(TAG, "countdown finish");
    }

    //    Stop timer service as soon as the Focus hour ends
    private void stopTimer() {
        this.cancel();
        Intent startTimer = new Intent(MainApplication.getInstance().getBaseContext(), TimerService.class);
        MainApplication.getInstance().getBaseContext().stopService(startTimer);
    }


    private void sendTimerBroadcastTimer(String hms, long millisUntilFinished, boolean isTimeFinished){
        countDownTimer.putExtra(Constants.HOCUS_FOCUS_TIMER, hms);
        countDownTimer.putExtra(Constants.HOCUS_FOCUS_TIMER_MILLI_SECOND, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
        MainApplication.getInstance().getBaseContext().sendBroadcast(countDownTimer);
    }
}