package media.musicplayer.songs.mp3player.audio.receiver;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import org.greenrobot.eventbus.EventBus;

import media.musicplayer.songs.mp3player.audio.application.MyLifeCycleHandle;
import media.musicplayer.songs.mp3player.audio.model.CloseApp;
import media.musicplayer.songs.mp3player.audio.ui.MusicPlayerActivity;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * Created by hiennguyen on 4/18/16.
 */
public class AudioReceiver extends BroadcastReceiver {
    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (MusicPlayerActivity.musicSrv.isPlaying()) {
                        MusicPlayerActivity.musicSrv.pausePlayer();
                        //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), false);
                    } else {
                        MusicPlayerActivity.musicSrv.resumePlayer();
                        //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()),true);
                    }

                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    if (MusicPlayerActivity.musicSrv != null)
                        MusicPlayerActivity.musicSrv.playNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    if (MusicPlayerActivity.musicSrv != null)
                        MusicPlayerActivity.musicSrv.playPrev();
                    break;
            }
        } else {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            String action = intent.getAction();
            switch (action) {
                case Constants.ACTION.PLAY_ACTION:
                    try {
                        if (MusicPlayerActivity.musicSrv.isPlaying()) {
                            MusicPlayerActivity.musicSrv.pausePlayer();
                            //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), false);
                        } else {
                            MusicPlayerActivity.musicSrv.resumePlayer();
                            //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()),true);
                        }
                    } catch (Exception ex) {
                    }
                    break;
                case Constants.ACTION.PREV_ACTION:
                    try {
                        if (MusicPlayerActivity.musicSrv != null)
                            MusicPlayerActivity.musicSrv.playPrev();
                    } catch (Exception ex) {
                    }
                    //imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), musicSrv.isPlaying());
                    break;
                case Constants.ACTION.NEXT_ACTION:
                    try {
                        if (MusicPlayerActivity.musicSrv != null)
                            MusicPlayerActivity.musicSrv.playNext();
                    } catch (Exception ex) {
                    }
                    //imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), musicSrv.isPlaying());
                    break;
                case Constants.ACTION.UPDATE_SONG_ACTION:
                    //tvTitle.setText(musicSrv.getSongs().get(intent.getIntExtra("postion", 0)).getSongName());
                    //tvSubtitle.setText(musicSrv.getSongs().get(intent.getIntExtra("postion",0)).getAlbum());
                    break;
                case Constants.ACTION.CLOSE_ACTION:
                    try {
                        //context.sendBroadcast(new Intent("finish"));
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
                        EventBus.getDefault().post(new CloseApp(true));
                        //
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                        android.os.Process.sendSignal(pid, android.os.Process.SIGNAL_KILL);
//                if (MyLifeCycleHandle.isBackground) {
//                    Intent i = new Intent(context, MusicPlayerActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    i.putExtra("close_activity", true);
//                    context.startActivity(i);
//                }
                        killThisPackageIfRunning(context, context.getPackageName());
                        System.exit(0);
                    } catch (Exception ex) {
                    }

//                System.exit(1);
//                try {
//                    intent = new Intent(context, MusicPlayerActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra("EXIT", true);
//                    context.startActivity(intent);
//                } catch (Exception e) {
//
//                }
//                killThisPackageIfRunning(context, context.getPackageName());
//                int id = android.os.Process.myPid();
//                android.os.Process.killProcess(id);

                    break;
                default:
                    break;
            }
        }
    }

    public static void killThisPackageIfRunning(final Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(packageName);
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}

