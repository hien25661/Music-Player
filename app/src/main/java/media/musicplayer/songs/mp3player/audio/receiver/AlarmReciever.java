package media.musicplayer.songs.mp3player.audio.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.greenrobot.eventbus.EventBus;

import media.musicplayer.songs.mp3player.audio.model.CloseApp;
import media.musicplayer.songs.mp3player.audio.model.FinishAlarm;
import media.musicplayer.songs.mp3player.audio.model.FinishTimer;
import media.musicplayer.songs.mp3player.audio.ui.MusicPlayerActivity;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * Created by NewBie on 4/25/16.
 */
public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            boolean isStop = intent.getBooleanExtra("stop_playback", false);
            if (isStop) {
                MusicPlayerActivity.musicSrv.pausePlayer();
                NotificationManager mNotifyMgr =
                        (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("timerset", 0).commit();
                sharedPreferences.edit().putInt("timer_action", 0).commit();
                EventBus.getDefault().post(new FinishAlarm(false));
                EventBus.getDefault().post(new CloseApp(false));
                int id = android.os.Process.myPid();
                android.os.Process.killProcess(id);
                System.exit(0);
            } else {
                if(MusicPlayerActivity.musicSrv!=null){
                    if(!MusicPlayerActivity.musicSrv.isPlaying()){
                        MusicPlayerActivity.musicSrv.resumePlayer();
                    }
                }
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("timerset", 0).commit();
                sharedPreferences.edit().putInt("timer_action", 0).commit();
                EventBus.getDefault().post(new FinishTimer(true));
                //MusicPlayerActivity.musicSrv.playNext();
            }
        }catch (Exception ex){

        }
    }
}
