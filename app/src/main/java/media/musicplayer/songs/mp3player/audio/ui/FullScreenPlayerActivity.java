/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package media.musicplayer.songs.mp3player.audio.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import jp.wasabeef.glide.transformations.BlurTransformation;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.interfaces.AccelerometerListener;
import media.musicplayer.songs.mp3player.audio.manager.AccelerometerManager;
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
import media.musicplayer.songs.mp3player.audio.player.MusicService;
import media.musicplayer.songs.mp3player.audio.receiver.AlarmReciever;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.LocaleHelper;
import media.musicplayer.songs.mp3player.audio.utils.LogHelper;
import media.musicplayer.songs.mp3player.audio.widgets.TimerDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * A full screen player that shows the current playing music with a background image
 * depicting the album art. The activity also has controls to seek/pause/play the audio.
 */
public class FullScreenPlayerActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener, View.OnTouchListener, AccelerometerListener {
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
    @Bind(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.spn_sleeptimer)
    Spinner spn_sleeptimer;
    @Bind(R.id.imv_timer)
    ImageView imv_timer;

    @Bind(R.id.imv_favorite)
    ImageView imv_favorite;

    @Bind(R.id.shareface)
    ImageView imv_share;

    @Bind(R.id.imv_list)
    ImageView imv_list;

    @Bind(R.id.imv_volume)
    ImageView imv_volume;

    NowPlayingFragment nowPlayingFragment;
    RotateImageFragment rotateImageFragment;
    String[] arrayTimer;
    SharedPreferences sharedPreferences;
    SeekBar seekBarVolume;

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
        }
    }

    Intent t, t2;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    TextView title;
    ImageView imv_equalizer;
    ImageView imv_setting;

    boolean isSetAlarm = true;
    boolean isStartNewActivity = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ActionBar actionBar;
    boolean isPausePlay = false;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void setDataSpinner(String[] arr) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, arr); //selected item will look like a spinner set from XML
        spn_sleeptimer.setAdapter(spinnerArrayAdapter);
        if (sharedPreferences.getInt(TIMER_SET, 0) != 0) {
            isSetAlarm = false;
        }

        spn_sleeptimer.setSelection(sharedPreferences.getInt(TIMER_SET, 0));
    }

    TimerDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this,new Crashlytics());
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        setContentView(R.layout.activity_full_player);
        sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);

        ButterKnife.bind(this);
        arrayTimer = getResources().getStringArray(R.array.arr_timerset);
        setDataSpinner(arrayTimer);
        spn_sleeptimer.setOnItemSelectedListener(this);
        spn_sleeptimer.setOnTouchListener(this);
        t = new Intent(Constants.ACTION.MAIN_ACTION);
        t2 = new Intent(Constants.ACTION.UPDATE_SONG_ACTION);
        //initializeToolbar();
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.now_playing);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_actionbar);
        title = (TextView) findViewById(R.id.title);
        imv_equalizer = (ImageView) findViewById(R.id.imv_equalizer);
        imv_equalizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEqualizer();
            }
        });

        imv_setting = (ImageView) findViewById(R.id.imv_setting);
        imv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartNewActivity = true;
                Intent mIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(mIntent);
            }
        });
        title.setText(R.string.now_playing);
        nowPlayingList = MusicPlayerActivity.mlist_play;
        currentPlay = getIntent().getIntExtra("currentpos", 0);
        //setting ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mBackgroundImage = (ImageView) findViewById(R.id.background_image);
        // settingBackground();
        mPauseDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_pause_white_48dp);
        mPlayDrawable = ContextCompat.getDrawable(this, R.drawable.uamp_ic_play_arrow_white_48dp);
        mPlayPause = (ImageView) findViewById(R.id.play_pause);
        mSkipNext = (ImageView) findViewById(R.id.next);
        mSkipPrev = (ImageView) findViewById(R.id.prev);
        mRepeat = (ImageView) findViewById(R.id.repeat);
        mShuffe = (ImageView) findViewById(R.id.shuffle);

        mStart = (TextView) findViewById(R.id.startText);
        mEnd = (TextView) findViewById(R.id.endText);
        mSeekbar = (SeekBar) findViewById(R.id.seekBar1);

//        mLoading = (ProgressBar) findViewById(R.id.progressBar1);
        mControllers = findViewById(R.id.controllers);
        nowPlayingFragment = new NowPlayingFragment();
        rotateImageFragment = new RotateImageFragment();
        //add tab
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("NowPlayingFragment", NowPlayingFragment.class)
                .add("RotateImageFragment", RotateImageFragment.class)
                .create());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);


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
        mSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayerActivity.musicSrv.playNext();
                t.putExtra("MESSAGE", "NEXT");
                sendBroadcast(t);
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
                sendBroadcast(t);
                if (checkExistInFavorite(MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                    imv_favorite.setImageResource(R.drawable.likein);
                } else {
                    imv_favorite.setImageResource(R.drawable.like);
                }
            }
        });
        if (MusicPlayerActivity.musicSrv == null) {
            finish();
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
                isPausePlay = true;
                if (MusicPlayerActivity.musicSrv.isPlaying()) {
                    MusicPlayerActivity.musicSrv.pausePlayer();
                    mPlayPause.setImageResource(R.drawable.play_btn);
                } else {

                    MusicPlayerActivity.musicSrv.resumePlayer();
                    mPlayPause.setImageResource(R.drawable.pause_btn);
                }
            }
        });

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
        try {
            initPopupVolume();
            if (checkExistInFavorite(MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                imv_favorite.setImageResource(R.drawable.likein);
            } else {
                imv_favorite.setImageResource(R.drawable.like);
            }
        } catch (Exception ex) {

        }

    }

    public void onClickEqualizer() {
        Intent mIntent = new Intent(this, EqualizerActivity.class);
        startActivity(mIntent);
    }

    public Activity getActivity() {
        return this;
    }

    public void settingBackground() {
        sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE);
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(this, mBackgroundImage, type);
    }

    public void updateLanguage() {
        int lang = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE).getInt(MoreFragment.LANG, 0);

        switch (lang) {
            case 0:
                LocaleHelper.setLocale(getActivity(), "en");
                break;
            case 1:
                LocaleHelper.setLocale(getActivity(), "fr");
                break;
            case 2:
                LocaleHelper.setLocale(getActivity(), "de");
                break;
            case 3:
                LocaleHelper.setLocale(getActivity(), "it");
                break;
            case 4:
                LocaleHelper.setLocale(getActivity(), "es");
                break;
            case 5:
                LocaleHelper.setLocale(getActivity(), "pt", "PT");

                break;
            case 6:
                LocaleHelper.setLocale(getActivity(), "pt", "BR");

                break;
            case 7:
                LocaleHelper.setLocale(getActivity(), "ru");
                break;
            case 8:
                LocaleHelper.setLocale(getActivity(), "ja");
                break;
            case 9:
                LocaleHelper.setLocale(getActivity(), "tr");
                break;
        }
        updateView();
    }

    public void updateView() {
        if (actionBar != null)
            actionBar.setTitle(R.string.now_playing);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public void playSong(TransitPlaySong event) {
        ArrayList<Song> mlist_play = new ArrayList<>();
        mlist_play = MusicPlayerActivity.musicSrv.getSongs();
        if (!event.isAddQueue) {
            mlist_play.clear();
        }
        t.putExtra("MESSAGE", "");
        sendBroadcast(t);
        //mlist_play.addAll(event.getListSong());
        Log.e("mlist_play", "" + mlist_play.size());

        MusicPlayerActivity.musicSrv.setList(mlist_play);
        MusicPlayerActivity.musicSrv.setSong(event.posClick);

        MusicPlayerActivity.musicSrv.playSong(event.posClick);
        if (checkExistInFavorite(mlist_play.get(event.posClick))) {
            imv_favorite.setImageResource(R.drawable.likein);
        } else {
            imv_favorite.setImageResource(R.drawable.like);
        }
    }

    Song songPlaying = null;

    @Subscribe
    public void setInfor(TransitInfo event) {
        mPlayPause.setImageResource((event.isPlaying) ? R.drawable.pause_btn : R.drawable.play_btn);
        t2.putExtra("MESSAGE", "" + event.isPlaying);
        sendBroadcast(t2);
        sendBroadcast(t);
        if(isPausePlay == false) {
            setBackGround();
        }
        isPausePlay = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            Song song = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
            sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE);
            int type = sharedPreferences.getInt(Constants.ID_BG, 0);
            int drawableBg = SingleBackground.getInstance().getCurrentBackground(type);
            if (song.getAlbum_art_uri() != null) {
//                Glide.with(this).load(song.getAlbum_art_uri()).error(drawableBg).diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true).signature(new StringSignature(""+System.currentTimeMillis())).bitmapTransform(new BlurTransformation(this, 10)).into(mBackgroundImage);
                Glide.with(this).load(song.getAlbum_art_uri()).override(360,640).error(R.drawable.b1).signature(new StringSignature("" + System.currentTimeMillis())).bitmapTransform(new BlurTransformation(this, 10)).into(mBackgroundImage);

                //Check device supported Accelerometer senssor or not
                if (AccelerometerManager.isSupported(this)) {

                    //Start Accelerometer Listening
                    AccelerometerManager.startListening(this);
                }
            }

        } catch (Exception e) {

        }
        MainApplication.getInstance().trackScreenView("Full Screen Player Screen");
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

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarm = new Intent(this,
                AlarmReciever.class);
        PendingIntent morningIntent = PendingIntent.getBroadcast(this, 9999,
                intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putInt(TIMER_SET, 0).commit();
            sharedPreferences.edit().putInt("timer_action", 0).commit();
        }
        alarmManager.cancel(morningIntent);
        morningIntent.cancel();
    }

    public final static String TIMER_SET = "timerset";

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("onTouch: ", " " + onTouch);
        if (!onTouch) return;
        try {
            cancelAlarm();
        } catch (Exception ex) {
            Log.e("Error", "Cancel Alarm");
        }
        switch (position) {
            case 0://Not Set
                sharedPreferences.edit().putInt(TIMER_SET, 0).commit();
                toastMess(-1);
                break;
            case 1://30 mins
                scheduleAlarm(30 * 60 * 1000, true);
                sharedPreferences.edit().putInt(TIMER_SET, 1).commit();
                toastMess(30);
                break;
            case 2://60 mins
                sharedPreferences.edit().putInt(TIMER_SET, 2).commit();
//                if (isSetAlarm == true)
                scheduleAlarm(60 * 60 * 1000, true);
                toastMess(60);

                break;
            case 3://90 mins
                sharedPreferences.edit().putInt(TIMER_SET, 3).commit();
//                if (isSetAlarm == true)
                scheduleAlarm(90 * 60 * 1000, true);
                toastMess(90);

                break;
            case 4://120 mins
                sharedPreferences.edit().putInt(TIMER_SET, 4).commit();
//                if (isSetAlarm == true)
                scheduleAlarm(120 * 60 * 1000, true);
                toastMess(120);

                break;
            case 5://150 mins
                sharedPreferences.edit().putInt(TIMER_SET, 5).commit();
//                if (isSetAlarm == true)
                scheduleAlarm(150 * 60 * 1000, true);
                toastMess(150);

                break;
            case 6://180 mins
                sharedPreferences.edit().putInt(TIMER_SET, 6).commit();
//                if (isSetAlarm == true)
                scheduleAlarm(180 * 60 * 1000, true);
                toastMess(180);
                break;
            case 7://Custom
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.custom_minute);
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                alert.setView(input);
                alert.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for OK button here
                        int minutes = Integer.parseInt(input.getText().toString());
                        sharedPreferences.edit().putInt(TIMER_SET, 7).commit();
                        if (minutes < 0) {
                            toastMess(-1);
                        } else {
                            scheduleAlarm(minutes * 60 * 1000, true);
                            toastMess(minutes);
                        }
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                        dialog.dismiss();
                        spn_sleeptimer.setSelection(0);
                    }
                });
                alert.show();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Subscribe
    public void onStopAlarm(FinishAlarm event) {
        if (event.isPlaying == false) {
            setDataSpinner(arrayTimer);
        }
    }

    boolean onTouch;

    public void toastMess(int minute) {
        if (minute == -1) {
            Toast.makeText(FullScreenPlayerActivity.this, getResources().getString(R.string.sleep_mess_cancel), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FullScreenPlayerActivity.this, getResources().getString(R.string.sleep_mess_1) + " " + minute + " " + getResources().getString(R.string.sleep_mess_2), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        onTouch = true;
        return false;
    }

    @Subscribe
    public void closeApp(CloseApp event) {
        try {
            stopService(new Intent(this, MusicService.class));
        } catch (Exception ex) {

        }
        Intent mIntent = new Intent(this, MusicPlayerActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.putExtra("EXIT", true);
        startActivity(mIntent);
        finish();
    }

    @OnClick(R.id.imv_timer)
    public void settingTimer() {
        dialog = new TimerDialog(this);
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
                    stopService(new Intent(getBaseContext(), TimerService.class));
                } catch (Exception ex) {
                    Log.i("TimerService", "Error");
                }
                Intent timeService1 = new Intent(getBaseContext(), TimerService.class);
                timeService1.putExtra("timer", timerSet);
                timeService1.putExtra("action", action);
                startService(timeService1);
                sharedPreferences.edit().putInt(TIMER_SET, 1).commit();
                sharedPreferences.edit().putInt("timer_action", action).commit();
                // dialog.dismiss();
            }

            @Override
            public void clickOnStop() {
                cancelAlarm();
                stopService(new Intent(getBaseContext(), TimerService.class));
                dialog.updateGUITimer(false, "0");
                //  dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Subscribe
    public void setCountDown(UpdateGuiEvent event) {
        Log.e("BOAA", "" + event.timer);
        try {
            if (event.timer.equals("00:00:00")) {
                stopService(new Intent(getBaseContext(), TimerService.class));
                try {
                    int action = sharedPreferences.getInt("timer_action", 0);
                    boolean isStop = (action == 0) ? true : false;
                    if (isStop) {
                        MusicPlayerActivity.musicSrv.pausePlayer();
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotifyMgr.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
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
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
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

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake(float force, boolean isNext) {
        if (sharedPreferences.getBoolean(Constants.ENABLE_SHAKE, false) == true) {
            if (isNext) {
                MusicPlayerActivity.musicSrv.playNext();
                t.putExtra("MESSAGE", "NEXT");
                sendBroadcast(t);
            } else {
                MusicPlayerActivity.musicSrv.playPrev();
                t.putExtra("MESSAGE", "PREV");
                sendBroadcast(t);
            }
        }
    }

    @OnClick(R.id.imv_favorite)
    public void addToFavorite() {
        songPlaying = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
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

    @OnClick(R.id.shareface)
    public void shareListening() {
        songPlaying = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = "Listening the song : " + songPlaying.getSongName() + "\nvia " + "http://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    @OnClick(R.id.imv_list)
    public void showList() {
        viewPager.setCurrentItem(1, true);
        viewPagerTab.setViewPager(viewPager);
    }

    PopupWindow popupWindow;

    @OnClick(R.id.imv_volume)
    public void showPopup() {

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(imv_volume, 0, 0);
        }

    }

    private void setBackGround() {
        try {
            Song song = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
            int type = sharedPreferences.getInt(Constants.ID_BG, 0);
            int drawableBg = SingleBackground.getInstance().getCurrentBackground(type);
            if (song.getAlbum_art_uri() != null) {
                Glide.with(this).load(song.getAlbum_art_uri()).override(360,640).error(R.drawable.b1).signature(new StringSignature("" + System.currentTimeMillis())).bitmapTransform(new BlurTransformation(this, 10)).into(mBackgroundImage);
            } else {

            }
        } catch (Exception e) {
        }
    }

    private void initPopupVolume() {
        LayoutInflater layoutInflater
                = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
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
    }

    @OnClick(R.id.imv_ringstone)
    public void startRingdroidEditor() {
        songPlaying = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
        String filename = songPlaying.getPath();
        try {
            MusicPlayerActivity.musicSrv.pausePlayer();
            Intent intent = new Intent(Intent.ACTION_EDIT,
                    Uri.parse(filename));
            intent.setClassName(
                    "media.musicplayer.songs.mp3player.audio",
                    "com.ringdroid.RingdroidEditActivity");
            startActivity(intent);
        } catch (Exception e) {
            Log.e("Ringdroid", "Couldn't start editor");
        }
    }

    @Subscribe
    public void stopTimer(FinishTimer event) {
        if (event.isFinish == true) {
            try {
                stopService(new Intent(getBaseContext(), TimerService.class));
                dialog.updateGUITimer(false, "0");
                sharedPreferences.edit().putInt("timer_action", 0).commit();
                dialog.dismiss();
            } catch (Exception ex) {

            }
        }
    }
}
