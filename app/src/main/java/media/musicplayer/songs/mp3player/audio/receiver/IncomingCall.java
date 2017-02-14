package media.musicplayer.songs.mp3player.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import media.musicplayer.songs.mp3player.audio.ui.MusicPlayerActivity;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * Created by NewBie on 4/26/16.
 */
public class IncomingCall extends BroadcastReceiver {
    Context mContext;
    static MyPhoneStateListener PhoneListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("VOA", "Phone call:");
        mContext = context;
        try {
            // TELEPHONY MANAGER class object to register one listner
            TelephonyManager tmgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if(PhoneListener == null) {
                //Create Listner
                PhoneListener = new MyPhoneStateListener();

                // Register listener for LISTEN_CALL_STATE
                tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }

        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }

    }

    private class MyPhoneStateListener extends PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {

            Log.d("MyPhoneListener", state + "   incoming no:" + incomingNumber);
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music
                try {
                    saveStatusPlayer(mContext,false);
                    if (MusicPlayerActivity.musicSrv.isPlaying()) {
                        MusicPlayerActivity.musicSrv.pausePlayer();
                        saveStatusPlayer(mContext,true);
                    }
                    return;
                } catch (Exception ex) {

                }
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
                try {
                    // Log.e("NOA","status: "+getStatusPlayer(mContext));
                    if (getStatusPlayer(mContext))
                        // if (MusicPlayerActivity.musicSrv.isPlaying())
                        MusicPlayerActivity.musicSrv.resumePlayer();
                } catch (Exception ex) {

                }
                return;
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                try {
                    saveStatusPlayer(mContext,false);
                    if (MusicPlayerActivity.musicSrv.isPlaying()) {
                        MusicPlayerActivity.musicSrv.pausePlayer();
                        saveStatusPlayer(mContext,true);
                    }
                } catch (Exception ex) {

                }
                return;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void saveStatusPlayer(Context context, boolean isPlaying) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("IS_PLAYING", isPlaying).commit();
    }

    private boolean getStatusPlayer(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("IS_PLAYING", false);
    }
}
