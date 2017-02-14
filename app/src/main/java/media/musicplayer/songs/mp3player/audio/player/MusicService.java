package media.musicplayer.songs.mp3player.audio.player;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.model.CurrentSongDuration;
import media.musicplayer.songs.mp3player.audio.model.SeekBarProgress;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TotalSongDuration;
import media.musicplayer.songs.mp3player.audio.model.TransitInfo;
import media.musicplayer.songs.mp3player.audio.receiver.AlarmReciever;
import media.musicplayer.songs.mp3player.audio.receiver.AudioReceiver;
import media.musicplayer.songs.mp3player.audio.ui.EqualizerActivity;
import media.musicplayer.songs.mp3player.audio.ui.MusicPlayerActivity;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.EqualizerHelper;
import media.musicplayer.songs.mp3player.audio.utils.MediaItem;
import media.musicplayer.songs.mp3player.audio.utils.TimerUtil;
import media.musicplayer.songs.mp3player.audio.utils.UtilFunctions;

/**
 * Created by NewBie on 4/7/16.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    //media player
    public MediaPlayer player;
    //song list
    private ArrayList<Song> songs = new ArrayList<>();
    //current position
    private int songPosn;
    private int repeatType = REPEAT_ALL;
    private static final int REPEAT_ONE = 1;
    private static final int REPEAT_ALL = 2;
    private static final int SUFFER = 3;
    private static final int DONT_REPEAT = 4;
    private Handler progressBarHandler = new Handler();
    SharedPreferences mSharedPreferences;
    //AudioManager.
    private AudioManager mAudioManager;
    public boolean music_isplaying = false;

    private final IBinder musicBind = new MusicBinder();
    EqualizerHelper mEqualizerHelper;
    boolean isOn;

    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    Bitmap mDummyAlbumArt;
    private static boolean currentVersionSupportLockScreenControls = false;

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        cancelAlarm();
        player.stop();
        player.release();
        return false;
    }

    Bitmap icon;

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    @Override
    public void onCreate() {
        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        super.onCreate();
        //initialize position
        songPosn = 0;
        if (icon == null) {
            icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_audiotrack);
        }
        mSharedPreferences = this.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);

    }

    Context mContext;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mContext = getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        initMusicPlayer();
        initAudioFX();
        if (currentVersionSupportLockScreenControls) {
            RegisterRemoteClient();
        }
        return START_STICKY;
    }
//    public boolean isEqualizerEnabled() {
//        return getSharedPreferences().getBoolean("EQUALIZER_ENABLED", true);
//    }

    /**
     * Initializes the equalizer and audio effects for this service session.
     */
    public void initAudioFX() {

        try {
            //Instatiate the equalizer helper object.
            boolean isEnable = mSharedPreferences.getBoolean(EqualizerActivity.EQ_ENABLE, true);
            isOn = isEnable;
            mEqualizerHelper = new EqualizerHelper(mContext, player.getAudioSessionId(),
                    isEnable);

        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
//            mEqualizerHelper.setIsEqualizerSupported(false);
        } catch (Exception e) {
            e.printStackTrace();
            mEqualizerHelper.setIsEqualizerSupported(false);
        }

    }

    public AudioManager getmAudioManager() {
        return mAudioManager;
    }

    public void setmAudioManager(AudioManager mAudioManager) {
        this.mAudioManager = mAudioManager;
    }

    /**
     * Returns the EqualizerHelper instance. This
     * can be used to modify equalizer settings and
     * toggle them on/off.
     */
    public EqualizerHelper getEqualizerHelper() {
        return mEqualizerHelper;
    }

    /**
     * Retrieves the EQ values for mMediaPlayer's current song and
     * applies them to the EQ engine.
     *
     * @param position The id of the mMediaPlayer is current handling.
     */
    private void applyMediaPlayerEQ(int position) {

        if (mEqualizerHelper == null)
            return;

        short fiftyHertzBand = mEqualizerHelper.getEqualizer().getBand(50000);
        short oneThirtyHertzBand = mEqualizerHelper.getEqualizer().getBand(130000);
        short threeTwentyHertzBand = mEqualizerHelper.getEqualizer().getBand(320000);
        short eightHundredHertzBand = mEqualizerHelper.getEqualizer().getBand(800000);
        short twoKilohertzBand = mEqualizerHelper.getEqualizer().getBand(2000000);

        //Get the equalizer/audioFX settings for this specific song.
        int[] eqValues = SQLiteDataController.getInstance(mContext).getEQValueById(position);

        //50Hz Band.
        if (eqValues[0] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) 0);
        } else if (eqValues[0] < 16) {

            if (eqValues[0] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) (-(16 - eqValues[0]) * 100));
            }

        } else if (eqValues[0] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(fiftyHertzBand, (short) ((eqValues[0] - 16) * 100));
        }

        //130Hz Band.
        if (eqValues[1] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) 0);
        } else if (eqValues[1] < 16) {

            if (eqValues[1] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) (-(16 - eqValues[1]) * 100));
            }

        } else if (eqValues[1] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(oneThirtyHertzBand, (short) ((eqValues[1] - 16) * 100));
        }

        //320Hz Band.
        if (eqValues[2] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) 0);
        } else if (eqValues[2] < 16) {

            if (eqValues[2] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) (-(16 - eqValues[2]) * 100));
            }

        } else if (eqValues[2] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(threeTwentyHertzBand, (short) ((eqValues[2] - 16) * 100));
        }

        //800Hz Band.
        if (eqValues[3] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) 0);
        } else if (eqValues[3] < 16) {

            if (eqValues[3] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) (-(16 - eqValues[3]) * 100));
            }

        } else if (eqValues[3] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(eightHundredHertzBand, (short) ((eqValues[3] - 16) * 100));
        }

        //2kHz Band.
        if (eqValues[4] == 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) 0);
        } else if (eqValues[4] < 16) {

            if (eqValues[4] == 0) {
                mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) -1500);
            } else {
                mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) (-(16 - eqValues[4]) * 100));
            }

        } else if (eqValues[4] > 16) {
            mEqualizerHelper.getEqualizer().setBandLevel(twoKilohertzBand, (short) ((eqValues[4] - 16) * 100));
        }


        //Set the audioFX values.
        mEqualizerHelper.getVirtualizer().setStrength((short) eqValues[5]);
        mEqualizerHelper.getBassBoost().setStrength((short) eqValues[6]);


    }

    public MediaPlayer getMediaPlayer() {
        return player;
    }

    public void initMusicPlayer() {
        /*
         * Release the MediaPlayer objects if they are still valid.
		 */
        if (player != null) {
            player.release();
            player = null;
        }
        //set player properties
        player = new MediaPlayer();
        try {
            player.setWakeMode(getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
        } catch (Exception e) {
            player = new MediaPlayer();

        }

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        setSongs(theSongs);
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public int getSongPosn() {
        return songPosn;
    }


    //    public void playSong() {
//        //play a song
//        try {
//
//            player.reset();
//            //get song
//            Song playSong = songs.get(songPosn);
//            //get id
//            player.setDataSource(playSong.getPath());
//            player.prepare();
//            player.start();
//            EventBus.getDefault().post(new SeekBarProgress(0));
//            updateProgressBar();
//
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public void checkListPlayed(Song song) {
        Gson gson = new Gson();
        String listSong = mSharedPreferences.getString(Constants.LAST_PLAYED, "");
        Log.e("listSong ", listSong);


        ArrayList<Song> arrayListSong = new ArrayList<>();
        if (!listSong.equals("")) {
            arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
            }.getType());
        }
        for (Song itemSong : arrayListSong) {
            if (itemSong.getPath().equals(song.getPath()))
                return;
        }
        arrayListSong.add(song);

        gson = new Gson();
        String newList = gson.toJson(arrayListSong);
        Log.e("newList ", newList);

        mSharedPreferences.edit().putString(Constants.LAST_PLAYED, newList).commit();

    }

    public void playSong(int pos) {
        try {
            progressBarHandler.removeCallbacks(mUpdateTimeTask);
            //play a song
            player.reset();
            //get song
            if(getSongs() == null || getSongs().size() ==0 || ((getSongs().size() - 1) < pos)){
                return;
            }
            Song playSong = getSongs().get(pos);
            try {
                checkListPlayed(playSong);
            } catch (Exception e) {
                Log.e("getMessage ", e.getMessage());

            }
            if (currentVersionSupportLockScreenControls) {
                UpdateMetadata(playSong);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            //get id
            player.setDataSource(playSong.getPath());
            player.prepareAsync();
            Intent t = new Intent(Constants.ACTION.UPDATE_SONG_ACTION);
            EventBus.getDefault().post(new TransitInfo(songPosn, getSongs(), true));
            t.putExtra("postion", pos);
            sendBroadcast(t);
            showNotification(songs.get(pos), true);
            //player.start();
        } catch (Exception ex){

        }
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onCompletionPlay();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        updateProgressBar();
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        try {
            if (player != null)
                return player.isPlaying();
        } catch (Exception e) {

        }
        return false;
    }

    public void pausePlayer() {
        if (currentVersionSupportLockScreenControls) {
            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        }
        player.pause();
        showNotification(songs.get(songPosn), false);
        EventBus.getDefault().post(new TransitInfo(songPosn, getSongs(), false));
    }

    public void resumePlayer() {
        try {
            if (currentVersionSupportLockScreenControls) {
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
            }
            player.start();
            showNotification(songs.get(songPosn), true);
            EventBus.getDefault().post(new TransitInfo(songPosn, getSongs(), true));
        } catch (Exception ex) {
        }
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public void playPrev() {

        songPosn--;
        if (songPosn < 0) songPosn = getSongs().size() - 1;
        Log.e("KKKKKKK", "Hien " + songPosn);
        playSong(songPosn);
        EventBus.getDefault().post(new TransitInfo(songPosn, getSongs(), true));
        showNotification(songs.get(songPosn), true);
    }

    boolean isOne = true;

    //skip to next
    public void playNext() {
        songPosn++;
        if (songPosn >= getSongs().size()) songPosn = 0;
        playSong(songPosn);
        EventBus.getDefault().post(new TransitInfo(songPosn, getSongs(), true));
        showNotification(songs.get(songPosn), true);
    }

    // ----------------onSeekBar Change Listener------------------------------//
    //Remove callback when onstart seekbar
    public void onStartTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     */
    public void onStopTrackingTouch(SeekBar seekBar) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = player.getDuration();
        int currentPosition = TimerUtil.progressToTimer(seekBar.getProgress(),
                totalDuration);

        // forward or backward to certain seconds
        player.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        progressBarHandler.postDelayed(mUpdateTimeTask, 100);
    }

    //destroy service
    @Override
    public void onDestroy() {
        super.onDestroy();
        songPosn = -1;
        // Remove progress bar update Hanlder callBacks
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        try {
            getEqualizerHelper().releaseEQObjects();
        } catch (Exception e) {
        }
        cancelAlarm();
        Log.d("Player Service", "Player Service Stopped");
//        if (player != null) {
//            if (player.isPlaying()) {
//                player.stop();
//            }
//            player.release();
//        }

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

    /**
     * On Song Playing completed if repeat is ON play same song again if shuffle
     * is ON play random song
     */
    public void onCompletionPlay() {
        switch (getRepeatType()) {
            case REPEAT_ONE:
                playSong(songPosn);
                break;
            case REPEAT_ALL:
                // no repeat or shuffle ON - play next song
                if (songPosn < (getSongs().size() - 1)) {
                    playSong(songPosn + 1);
                    songPosn = songPosn + 1;
                } else {
                    // play first song
                    playSong(0);
                    songPosn = 0;
                }
                break;
            case SUFFER:
                Random rand = new Random();
                songPosn = rand
                        .nextInt((getSongs().size() - 1) - 0 + 1) + 0;
                playSong(songPosn);
                break;
            case DONT_REPEAT:
                if (songPosn < (getSongs().size() - 1)) {
                    playSong(songPosn + 1);
                    songPosn = songPosn + 1;
                } else {
                    EventBus.getDefault().post(new TransitInfo(songPosn, getSongs(), false));
                    showNotification(songs.get(songPosn), false);
                }
                break;
        }
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = 0;
            try {
                totalDuration = player.getDuration();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            long currentDuration = 0;
            try {
                currentDuration = player.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            // Displaying Total Duration time
//            songTotalDurationLabel.get().setText(
//                    "" + TimerUtil.milliSecondsToTimer(totalDuration));
            EventBus.getDefault().post(new TotalSongDuration("" + TimerUtil.milliSecondsToTimer(totalDuration)));
            // Displaying time completed playing
//            songCurrentDurationLabel.get().setText(
//                    "" + TimerUtil.milliSecondsToTimer(currentDuration));
            EventBus.getDefault().post(new CurrentSongDuration("" + TimerUtil.milliSecondsToTimer(currentDuration)));

            // Updating progress bar
            int progress = (int) (TimerUtil.getProgressPercentage(currentDuration,
                    totalDuration));
            // Log.d("Progress", ""+progress);
            //songProgressBar.get().setProgress(progress);

            EventBus.getDefault().post(new SeekBarProgress(progress));

            // Running this thread after 100 milliseconds
            progressBarHandler.postDelayed(this, 100);
            // Log.d("AndroidBuildingMusicPlayerActivity","Runable  progressbar");
        }
    };

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public void showNotification1(Song song, boolean isPlaying) {
        Intent notificationIntent = new Intent(this, MusicPlayerActivity.class);

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent();
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getBroadcast(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent();
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getBroadcast(this, 1234,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent();
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getBroadcast(this, 0,
                nextIntent, 0);


        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(song.getSongName())
                .setTicker(song.getSongName())
                .setContentText(song.getArtist())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous,
                        "", ppreviousIntent)
                .addAction(((isPlaying == true) ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play), "",
                        pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "",
                        pnextIntent).build();

        // Sets an ID for the notification
        int mNotificationId = Constants.NOTIFICATION_ID.FOREGROUND_SERVICE;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notification);


    }

    private NotificationTarget notificationTarget;


    public void showNotification(Song song, boolean isPlaying) {
        {
            // Using RemoteViews to bind custom layouts into Notification
            RemoteViews remoteViews = new RemoteViews(getPackageName(),
                    R.layout.customnotification);
            Intent notificationIntent = new Intent(this, MusicPlayerActivity.class);

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    // Set Icon
                    .setSmallIcon(R.drawable.ic_launcher)
                            // Set Ticker Message
                    .setTicker(song.getSongName())
                            // Dismiss Notification
                            // Set PendingIntent into Notification
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                            // Set RemoteViews into Notification
                    .setContent(remoteViews);


            Intent previousIntent = new Intent();
            previousIntent.setAction(Constants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getBroadcast(this, 0,
                    previousIntent, 0);

            Intent playIntent = new Intent();
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getBroadcast(this, 1234,
                    playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent();
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getBroadcast(this, 0,
                    nextIntent, 0);

            Intent closeIntent = new Intent();
            closeIntent.setAction(Constants.ACTION.CLOSE_ACTION);
            PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, 0);
//            builder.setDeleteIntent(closePendingIntent);
            builder.setColor(Color.TRANSPARENT);
            final Notification notification = builder.build();

//            notificationTarget = new NotificationTarget(
//                    this,
//                    remoteViews,
//                    R.id.imagenotileft,
//                    notification,
//                    Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);


//             Locate and set the Image into customnotificationtext.xml ImageViews
//            Glide.with(this.getApplicationContext()) // safer!
//                    .load(song.getAlbum_art_uri())
//                    .asBitmap().error(R.drawable.ic_launcher)
//                    .into(notificationTarget);
            if (song.getAlbum_art_uri() == null) {
                remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.ic_launcher);
            } else {
                String pathImage = getPath(this, song.getAlbum_art_uri());
                if (pathImage != null) {
                    remoteViews.setImageViewUri(R.id.imagenotileft, song.getAlbum_art_uri());
                } else {
                    remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.ic_launcher);
                }
            }
            remoteViews.setImageViewResource(R.id.prev, R.drawable.back_prev);
            remoteViews.setImageViewResource(R.id.pause, (isPlaying == false) ? R.drawable.play_btn : R.drawable.pause_btn);
            remoteViews.setImageViewResource(R.id.next, R.drawable.next_btn);

            // Locate and set the Text into customnotificationtext.xml TextViews
            remoteViews.setTextViewText(R.id.title, song.getSongName());
            remoteViews.setTextViewText(R.id.text, song.getArtist());
            remoteViews.setTextViewText(R.id.tv_number, "" + (songPosn + 1) + "/" + getSongs().size());

            remoteViews.setOnClickPendingIntent(R.id.prev, ppreviousIntent);
            remoteViews.setOnClickPendingIntent(R.id.pause, pplayIntent);
            remoteViews.setOnClickPendingIntent(R.id.next, pnextIntent);
            remoteViews.setOnClickPendingIntent(R.id.imv_close, closePendingIntent);

            // Create Notification Manager
            NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Build Notification with Notification Manager
            notificationmanager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        }

    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new AudioReceiver().ComponentName());
        try {
            if (remoteControlClient == null) {
                mAudioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                mAudioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        } catch (Exception ex) {
        }
    }

    @SuppressLint("NewApi")
    private void UpdateMetadata(Song data) {
        if (remoteControlClient == null)
            return;
        RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAlbum());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, data.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getSongName());
        mDummyAlbumArt = UtilFunctions.getAlbumart(getApplicationContext(), data.getAlbumId());
        if (mDummyAlbumArt == null) {
            mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_art);
        }
        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
}
