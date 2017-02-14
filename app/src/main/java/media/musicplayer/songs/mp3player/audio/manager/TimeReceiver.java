package media.musicplayer.songs.mp3player.audio.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import org.greenrobot.eventbus.EventBus;

import media.musicplayer.songs.mp3player.audio.model.UpdateGuiEvent;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * Created by Hien on 7/9/2016.
 */
public class TimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Constants.INTENTFILTER_TIMER)){
            String timer = intent.getExtras().getString(Constants.HOCUS_FOCUS_TIMER);
            EventBus.getDefault().post(new UpdateGuiEvent(timer));
        }
    }
}
