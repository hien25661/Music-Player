package media.musicplayer.songs.mp3player.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import media.musicplayer.songs.mp3player.audio.ui.MusicPlayerActivity;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * Created by Hien on 6/18/2016.
 */
public class HeadphonePlug extends BroadcastReceiver {
    private boolean headsetConnected = false;
    SharedPreferences sharedPreferences;

    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        boolean isPlayPlugin = sharedPreferences.getBoolean(Constants.ENABLE_PLUGIN, true);
        try {
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    if (isPlayPlugin) {
                        if (MusicPlayerActivity.musicSrv.isPlaying()) {
                            MusicPlayerActivity.musicSrv.pausePlayer();
                        }
                    }
                } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    if (isPlayPlugin) {
                        if (MusicPlayerActivity.musicSrv.isPlaying()) {
                        } else {
                            MusicPlayerActivity.musicSrv.resumePlayer();
                        }
                    }
                }
            }
        }catch (Exception ex){

        }
    }
}
