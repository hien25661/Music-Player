package media.musicplayer.songs.mp3player.audio.ui;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.SongAdapter_FullScreen;
import media.musicplayer.songs.mp3player.audio.manager.TimerService;
import media.musicplayer.songs.mp3player.audio.model.CloseApp;
import media.musicplayer.songs.mp3player.audio.model.CurrentSongDuration;
import media.musicplayer.songs.mp3player.audio.model.FinishAlarm;
import media.musicplayer.songs.mp3player.audio.model.FinishTimer;
import media.musicplayer.songs.mp3player.audio.model.SeekBarProgress;
import media.musicplayer.songs.mp3player.audio.model.SingleBackground;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TotalSongDuration;
import media.musicplayer.songs.mp3player.audio.model.TransitInfo;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaySong;
import media.musicplayer.songs.mp3player.audio.model.UpdateGuiEvent;
import media.musicplayer.songs.mp3player.audio.receiver.AlarmReciever;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.LogHelper;
import media.musicplayer.songs.mp3player.audio.widgets.TimerDialog;

/**
 * Created by Hien on 7/16/2016.
 */
public class ViewPlayer extends RelativeLayout {
    Context context;
    AppCompatActivity activity;
    private static final String TAG = LogHelper.makeLogTag(FullScreenPlayerActivity.class);
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private ImageView mRepeat;
    private ImageView mShuffe;
    private ImageView mSkipPrev;
    private ImageView mSkipNext;
    private ImageView mPlayPause;
    private TextView mStart;
    private TextView mEnd;
    private SeekBar mSeekbar;

    //    private ProgressBar mLoading;
    private View mControllers;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private ImageView mBackgroundImage;
    public ArrayList<Song> nowPlayingList = new ArrayList<>();
    public int currentPlay;

    NowPlayingFragment nowPlayingFragment;
    RotateImageFragment rotateImageFragment;
    String[] arrayTimer;
    SharedPreferences sharedPreferences;
    SeekBar seekBarVolume;
    Intent t, t2;
    TextView title;
    ImageView imv_equalizer;
    ImageView imv_setting, imv_timer;
    boolean isSetAlarm = true;
    boolean isStartNewActivity = false;
    SmartTabLayout viewPagerTab;
    ViewPager viewPager;
    PopupWindow popupWindow;
    ImageView imv_volume;
    TimerDialog dialog;
    public final static String TIMER_SET = "timerset";
    ImageView imv_share, imv_ringstone, imv_favorite;
    TextView tv_songname;
    TextView tv_infor;
    ImageView img_record;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ArrayList<Song> mlistSong = new ArrayList<>();
    public ViewPlayer(Context context) {
        super(context);
        this.context = context;
        activity = (AppCompatActivity) (context);
        init();
    }

    public ViewPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        activity = (AppCompatActivity) (context);
        init();
    }

    public ViewPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        activity = (AppCompatActivity) (context);
        init();
    }

    private void init() {
        EventBus.getDefault().register(this);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(R.layout.activity_full_player_top, this, true);
        imv_share = (ImageView) v.findViewById(R.id.shareface);
        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.pageView);
        mDrawerList = (ListView) v.findViewById(R.id.left_drawer);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        t = new Intent(Constants.ACTION.MAIN_ACTION);
        t2 = new Intent(Constants.ACTION.UPDATE_SONG_ACTION);

        imv_volume = (ImageView) v.findViewById(R.id.imv_volume);
        imv_setting = (ImageView) v.findViewById(R.id.imv_setting);
        imv_ringstone = (ImageView) v.findViewById(R.id.imv_ringstone);
        imv_favorite = (ImageView) v.findViewById(R.id.imv_favorite);
        img_record = (ImageView) v.findViewById(R.id.img_record);

        tv_songname = (TextView) v.findViewById(R.id.tv_songname);
        tv_infor = (TextView) v.findViewById(R.id.tv_infor);
//        viewPager = (ViewPager)v.findViewById(R.id.viewpager);
//        viewPagerTab = (SmartTabLayout)v.findViewById(R.id.viewpagertab);

        mStart = (TextView) v.findViewById(R.id.startText);
        mEnd = (TextView) v.findViewById(R.id.endText);
        mSeekbar = (SeekBar) v.findViewById(R.id.seekBar1);
        imv_favorite = (ImageView) v.findViewById(R.id.imv_favorite);

        mBackgroundImage = (ImageView) findViewById(R.id.background_image);
        // settingBackground();
        mPauseDrawable = ContextCompat.getDrawable(context, R.drawable.uamp_ic_pause_white_48dp);
        mPlayDrawable = ContextCompat.getDrawable(context, R.drawable.uamp_ic_play_arrow_white_48dp);
        mPlayPause = (ImageView) findViewById(R.id.play_pause);
        mSkipNext = (ImageView) findViewById(R.id.next);
        mSkipPrev = (ImageView) findViewById(R.id.prev);
        mRepeat = (ImageView) findViewById(R.id.repeat);
        mShuffe = (ImageView) findViewById(R.id.shuffle);

//        nowPlayingFragment = new NowPlayingFragment();
//        rotateImageFragment = new RotateImageFragment();
//        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
//                activity.getSupportFragmentManager(), FragmentPagerItems.with(context)
//                .add("NowPlayingFragment", NowPlayingFragment.class)
//                .add("RotateImageFragment", RotateImageFragment.class)
//                .create());
//        viewPager.setAdapter(adapter);
//        viewPagerTab.setViewPager(viewPager);


        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicPlayerActivity.musicSrv.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayerActivity.musicSrv.onStopTrackingTouch(seekBar);
            }
        });
        if (MusicPlayerActivity.musicSrv == null) {
        } else {
            if (MusicPlayerActivity.musicSrv.isPlaying()) {
                mPlayPause.setImageResource(R.drawable.pause_btn);
            } else {
                mPlayPause.setImageResource(R.drawable.play_btn);
            }
        }
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayerActivity.musicSrv.isPlaying()) {
                    MusicPlayerActivity.musicSrv.pausePlayer();
                    mPlayPause.setImageResource(R.drawable.play_btn);
                } else {

                    MusicPlayerActivity.musicSrv.resumePlayer();
                    mPlayPause.setImageResource(R.drawable.pause_btn);
                }
            }
        });
        mSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerActivity.musicSrv.playNext();
                t.putExtra("MESSAGE", "NEXT");
                context.sendBroadcast(t);
                if (checkExistInFavorite(MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                    imv_favorite.setImageResource(R.drawable.likein);
                } else {
                    imv_favorite.setImageResource(R.drawable.like);
                }
            }
        });
        mSkipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerActivity.musicSrv.playPrev();
                t.putExtra("MESSAGE", "PREV");
                context.sendBroadcast(t);
                if (checkExistInFavorite(MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                    imv_favorite.setImageResource(R.drawable.likein);
                } else {
                    imv_favorite.setImageResource(R.drawable.like);
                }
            }
        });
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (MusicPlayerActivity.musicSrv.getRepeatType()) {
                    case 1://Repeat One
                        MusicPlayerActivity.musicSrv.setRepeatType(2);
                        mRepeat.setImageResource(R.drawable.ic_repeat_white_36dp);
                        mShuffe.setImageResource(R.drawable.ic_shuffle_grey600_36dp);
                        break;
                    case 2://Repeat All
                        MusicPlayerActivity.musicSrv.setRepeatType(4);
                        mRepeat.setImageResource(R.drawable.ic_repeat_grey600_36dp);
                        mShuffe.setImageResource(R.drawable.ic_shuffle_grey600_36dp);
                        break;
                    case 4://Dont repeat
                        MusicPlayerActivity.musicSrv.setRepeatType(1);
                        mRepeat.setImageResource(R.drawable.ic_repeat_one_white_36dp);
                        mShuffe.setImageResource(R.drawable.ic_shuffle_grey600_36dp);
                        break;

                }
            }
        });
        mShuffe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicPlayerActivity.musicSrv.getRepeatType() == 3) {
                    MusicPlayerActivity.musicSrv.setRepeatType(4);
                    mShuffe.setImageResource(R.drawable.ic_shuffle_grey600_36dp);
                    mRepeat.setImageResource(R.drawable.ic_repeat_grey600_36dp);
                } else {
                    MusicPlayerActivity.musicSrv.setRepeatType(3);
                    mShuffe.setImageResource(R.drawable.ic_shuffle_white_36dp);
                    mRepeat.setImageResource(R.drawable.ic_repeat_grey600_36dp);
                }
            }
        });
        imv_equalizer = (ImageView) findViewById(R.id.imv_equalizer);
        imv_equalizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEqualizer();
            }
        });
        try {
            if (checkExistInFavorite(MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                imv_favorite.setImageResource(R.drawable.likein);
            } else {
                imv_favorite.setImageResource(R.drawable.like);
            }
        } catch (Exception ex) {

        }

        imv_volume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(imv_volume, 0, 0);
                }
            }
        });
        imv_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imv_setting = (ImageView) v.findViewById(R.id.imv_setting);
        imv_timer = (ImageView) v.findViewById(R.id.imv_timer);

        imv_timer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                settingTimer();
            }
        });
        imv_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(context, SettingActivity.class);
                context.startActivity(mIntent);
            }
        });
        imv_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareListening();
            }
        });
        imv_ringstone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRingdroidEditor();
            }
        });
        imv_favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite();
            }
        });
        setBackGround();
    }
    public void checkFavoriteSong(){
        try {
            if (checkExistInFavorite(MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                imv_favorite.setImageResource(R.drawable.likein);
            } else {
                imv_favorite.setImageResource(R.drawable.like);
            }
        } catch (Exception ex) {

        }
    }
    private void initPopupVolume() {
        try {
            LayoutInflater layoutInflater
                    = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.popup, null);
            seekBarVolume = (SeekBar) popupView.findViewById(R.id.seekbar_volume);
            seekBarVolume.setBackgroundColor(Color.argb(88, 0, 0, 0));
            popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            final int maxVolume = MusicPlayerActivity.musicSrv.getmAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            final int currentVolume = MusicPlayerActivity.musicSrv.getmAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
            seekBarVolume.setMax(maxVolume);
            seekBarVolume.setProgress(currentVolume);
            seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    MusicPlayerActivity.musicSrv.getmAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } catch (Exception ex) {
            Log.e("VVVVVV", "Loi volume");
        }
    }

    private void setBackGround() {
        try {
            Song song = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
            int type = sharedPreferences.getInt(Constants.ID_BG, 0);
            int drawableBg = SingleBackground.getInstance().getCurrentBackground(type);
            if (song.getAlbum_art_uri() != null) {
                Glide.with(context).load(song.getAlbum_art_uri()).override(360, 640).error(drawableBg).signature(new StringSignature("" + System.currentTimeMillis())).bitmapTransform(new BlurTransformation(context, 10)).into(mBackgroundImage);
            } else {

            }
        } catch (Exception e) {
        }
    }

    @Subscribe
    public void showTotalDuration(TotalSongDuration event) {
        mEnd.setText(event.total);
    }

    @Subscribe
    public void showCurrentDuration(CurrentSongDuration event) {
        mStart.setText(event.current);
    }

    @Subscribe
    public void showSeekbarProgress(SeekBarProgress event) {
        mSeekbar.setProgress(event.progress);
    }

    @Subscribe
    public void setInfor(TransitInfo event) {
        Song song = event.songArrayList.get(event.pos);
        mPlayPause.setImageResource((event.isPlaying) ? R.drawable.pause_btn : R.drawable.play_btn);
        t2.putExtra("MESSAGE", "" + event.isPlaying);
        context.sendBroadcast(t2);
        context.sendBroadcast(t);
        setBackGround();
        if (popupWindow == null) {
            initPopupVolume();
        }
        if (song.getAlbum_art_uri() != null) {
            Glide.with(context).load(song.getAlbum_art_uri()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.music_nowplaying).into(img_record);
        } else {
            Glide.with(context).load(R.drawable.music_nowplaying).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(img_record);
        }
        Log.e("VOAAAA", "Vao day" + song.getSongName());
        tv_songname.setText(song.getSongName());
        tv_infor.setText(song.getArtist());
        tv_songname.setSelected(true);
        updateListSong();

    }

    public void hidePopupVolume() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }
    }


    public void onClickEqualizer() {
        Intent mIntent = new Intent(context, EqualizerActivity.class);
        context.startActivity(mIntent);
    }

    public void settingBackground() {
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(context, mBackgroundImage, type);
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(context,
                AlarmReciever.class);
        PendingIntent morningIntent = PendingIntent.getBroadcast(context, 9999,
                intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(TIMER_SET, 0).commit();
            sharedPreferences.edit().putInt("timer_action", 0).commit();
        }
        alarmManager.cancel(morningIntent);
        morningIntent.cancel();
    }

    public void settingTimer() {
        dialog = new TimerDialog(context);
        dialog.setClickListener(new TimerDialog.clickListener() {
            @Override
            public void clickOnSet(int hour, int minute, int action) {
                /*Calendar calendar = Calendar.getInstance();
                long currentTime = calendar.getTimeInMillis();
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);

                if (calendar.getTimeInMillis() > currentTime) {
                    long timer = calendar.getTimeInMillis() - currentTime;
                    int minutes = (int) ((timer / (1000 * 60)) % 60);
                    scheduleAlarm(timer, (action == 0) ? true : false);
                    toastMess(minutes);
                    dialog.dismiss();
                } else {
                    Toast.makeText(FullScreenPlayerActivity.this, "Time must greater than current time", Toast.LENGTH_SHORT).show();
                }*/
                long timerSet = hour * 60 * 60 * 1000 + minute * 60 * 1000;
                // scheduleAlarm(timerSet, (action == 0) ? true : false);
                try {
                    context.stopService(new Intent(context, TimerService.class));
                } catch (Exception ex) {
                    Log.i("TimerService", "Error");
                }
                Intent timeService1 = new Intent(context, TimerService.class);
                timeService1.putExtra("timer", timerSet);
                timeService1.putExtra("action", action);
                context.startService(timeService1);
                sharedPreferences.edit().putInt(TIMER_SET, 1).commit();
                sharedPreferences.edit().putInt("timer_action", action).commit();
                // dialog.dismiss();
            }

            @Override
            public void clickOnStop() {
                cancelAlarm();
                context.stopService(new Intent(context, TimerService.class));
                dialog.updateGUITimer(false, "0");
                //  dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Subscribe
    public void stopTimer(FinishTimer event) {
        if (event.isFinish == true) {
            try {
                context.stopService(new Intent(context, TimerService.class));
                dialog.updateGUITimer(false, "0");
                sharedPreferences.edit().putInt("timer_action", 0).commit();
                dialog.dismiss();
            } catch (Exception ex) {

            }
        }
    }

    @Subscribe
    public void setCountDown(UpdateGuiEvent event) {
        Log.e("BOAA", "" + event.timer);
        try {
            if (event.timer.equals("00:00:00")) {
                context.stopService(new Intent(context, TimerService.class));
                try {
                    int action = sharedPreferences.getInt("timer_action", 0);
                    boolean isStop = (action == 0) ? true : false;
                    if (isStop) {
                        MusicPlayerActivity.musicSrv.pausePlayer();
                        NotificationManager mNotifyMgr =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
                } catch (Exception ex) {

                }
                if (dialog != null) {
                    dialog.updateGUITimer(true, event.timer);
                }
            } else {
                if (dialog != null) {
                    dialog.updateGUITimer(true, event.timer);
                }
            }
        } catch (Exception ex) {

        }
    }

    public void shareListening() {
        Log.e("AAAAA", "Vao day");
        Song songPlaying = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = "Listening the song : " + songPlaying.getSongName() + "\nvia " + "http://play.google.com/store/apps/details?id=" + context.getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        context.startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    public void startRingdroidEditor() {
        Song songPlaying = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
        String filename = songPlaying.getPath();
        try {
            MusicPlayerActivity.musicSrv.pausePlayer();
            Intent intent = new Intent(Intent.ACTION_EDIT,
                    Uri.parse(filename));
            intent.setClassName(
                    "media.musicplayer.songs.mp3player.audio",
                    "com.ringdroid.RingdroidEditActivity");
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't start editor");
        }
    }

    public void addToFavorite() {
        Song songPlaying = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
        if (checkExistInFavorite(songPlaying)) {
            Log.e("VOA", "Remove");
            removeFavorite(songPlaying);
            imv_favorite.setImageResource(R.drawable.like);
        } else {
            Log.e("VOA", "ADD");
            addFavorite(songPlaying);
            imv_favorite.setImageResource(R.drawable.likein);
        }
    }

    private void addFavorite(Song song) {
        Gson gson = new Gson();
        String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
        Log.e("listSong ", listSong);


        ArrayList<Song> arrayListSong = new ArrayList<>();
        if (!listSong.equals("")) {
            arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
            }.getType());
        }
        arrayListSong.add(song);
        gson = new Gson();
        String newList = gson.toJson(arrayListSong);
        Log.e("newList ", newList);

        sharedPreferences.edit().putString(Constants.FAVORITE, newList).commit();
        imv_favorite.setImageResource(R.drawable.likein);

    }

    private void removeFavorite(Song song) {
        Gson gson = new Gson();
        String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
        Log.e("listSong ", listSong);


        ArrayList<Song> arrayListSong = new ArrayList<>();
        if (!listSong.equals("")) {
            arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
            }.getType());
        }
        int index = -1;
        for (int i = 0; i < arrayListSong.size(); i++) {
            if (song.getPath().equals(arrayListSong.get(i).getPath())) {
                index = i;
                Log.e("VOA", "" + index);
                break;
            }
        }
        if (index > -1) {
            arrayListSong.remove(index);
        }
        gson = new Gson();
        String newList = gson.toJson(arrayListSong);
        Log.e("newList ", newList);

        sharedPreferences.edit().putString(Constants.FAVORITE, newList).commit();
        imv_favorite.setImageResource(R.drawable.like);
    }

    private boolean checkExistInFavorite(Song song) {
        Gson gson = new Gson();
        String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
        Log.e("listSong ", listSong);


        ArrayList<Song> arrayListSong = new ArrayList<>();
        if (!listSong.equals("")) {
            arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
            }.getType());
        }
        for (Song itemSong : arrayListSong) {
            if (itemSong.getPath().equals(song.getPath())) {
                return true;
            }
        }
        return false;
    }
    SongAdapter_FullScreen adapter;
    public void showListSong() {
        mlistSong = MusicPlayerActivity.musicSrv.getSongs();
        for (Song s : mlistSong) {
            if (s.equals(mlistSong.get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                s.setSelect(true);
            } else s.setSelect(false);
        }
        adapter = new SongAdapter_FullScreen(context, R.layout.item_song_playingtop, mlistSong);
        mDrawerList.setAdapter(adapter);
        if(mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
        }else {
            mDrawerLayout.openDrawer(mDrawerList);
        }
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new TransitPlaySong(mlistSong, position, true));
                for (Song s : mlistSong){
                    if(s.equals(mlistSong.get(position))){
                        s.setSelect(true);
                    }
                    else s.setSelect(false);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    public void updateListSong() {
        mlistSong = MusicPlayerActivity.musicSrv.getSongs();
        for (Song s : mlistSong) {
            if (s.equals(mlistSong.get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                s.setSelect(true);
            } else s.setSelect(false);
        }
        adapter = new SongAdapter_FullScreen(context, R.layout.item_song_playingtop, mlistSong);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new TransitPlaySong(mlistSong, position, true));
                for (Song s : mlistSong){
                    if(s.equals(mlistSong.get(position))){
                        s.setSelect(true);
                    }
                    else s.setSelect(false);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
