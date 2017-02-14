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
//https://github.com/protocol10/ASplayer/tree/master/src/com/akshay/protocol10/asplayer/receiver
//http://stackoverflow.com/questions/32561523/android-service-with-mediaplayer-gets-recreated-or-destroyed

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.PagerAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.interfaces.AccelerometerListener;
import media.musicplayer.songs.mp3player.audio.interfaces.GetSongFinish;
import media.musicplayer.songs.mp3player.audio.interfaces.TypeSearch;
import media.musicplayer.songs.mp3player.audio.manager.AccelerometerManager;
import media.musicplayer.songs.mp3player.audio.manager.SongsManager;
import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.Artist;
import media.musicplayer.songs.mp3player.audio.model.CloseApp;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.ItemSearch;
import media.musicplayer.songs.mp3player.audio.model.Playlist;
import media.musicplayer.songs.mp3player.audio.model.SearchModel;
import media.musicplayer.songs.mp3player.audio.model.SingleBackground;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.SortPlain;
import media.musicplayer.songs.mp3player.audio.model.TransitAlbumSongListEvent;
import media.musicplayer.songs.mp3player.audio.model.TransitGenresEvent;
import media.musicplayer.songs.mp3player.audio.model.TransitInfo;
import media.musicplayer.songs.mp3player.audio.model.TransitNextSong;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaySong;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaylistSongListEvent;
import media.musicplayer.songs.mp3player.audio.model.TransitSkinEvent;
import media.musicplayer.songs.mp3player.audio.model.TransitSongListEvent;
import media.musicplayer.songs.mp3player.audio.player.MusicService;
import media.musicplayer.songs.mp3player.audio.receiver.AudioReceiver;
import media.musicplayer.songs.mp3player.audio.receiver.HeadphonePlug;
import media.musicplayer.songs.mp3player.audio.task.AsynTaskGetSongLocal;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.LogHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Main activity for the music player.
 * This class hold the MediaBrowser and the MediaController instances. It will create a MediaBrowser
 * when it is created and connect/disconnect on start/stop. Thus, a MediaBrowser will be always
 * connected while this activity is running.
 */
public class MusicPlayerActivity extends AppCompatActivity
        implements GetSongFinish, AccelerometerListener {

    private static final String TAG = LogHelper.makeLogTag(MusicPlayerActivity.class);
    private static final String SAVED_MEDIA_ID = "com.example.android.ugamp.MEDIA_ID";
    private static final String FRAGMENT_TAG = "uamp_list_container";
    private static final String FRAGMENT_SEARCH_TAG = "fragment_search";
    private static final String FRAGMENT_PLAYLISTSONG_TAG = "fragment_play_list_song";
    private static final String FRAGMENT_LISTSONG_TAG = "fragment_list_song";
    private static final String FRAGMENT_GENRES_TAG = "fragment_genres";
    private static final String FRAGMENT_LISTSONG_ALBUM_TAG = "fragment_list_album_song";


    public static final String EXTRA_START_FULLSCREEN =
            "com.example.android.uamp.EXTRA_START_FULLSCREEN";

    /**
     * Optionally used with {@link #EXTRA_START_FULLSCREEN} to carry a MediaDescription to
     * the {@link FullScreenPlayerActivity}, speeding up the screen rendering
     * while the {@link android.support.v4.media.session.MediaControllerCompat} is connecting.
     */
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.example.android.uamp.CURRENT_MEDIA_DESCRIPTION";

    private Bundle mVoiceSearchParams;
    private ViewPager viewPager;
    PagerSlidingTabStrip tabLayout;
    int currentTab;
    SongsManager mSongmanager;
    SearchView searchView;
    public static ArrayList<Song> mlist_play = new ArrayList<>();
    BroadcastReceiver receiver;
    AudioReceiver audioReceiver = new AudioReceiver();
    SharedPreferences sharedPreferences;
    HeadphonePlug headphonePlugReceicer = new HeadphonePlug();

    @Bind(R.id.ll_viewmini)
    LinearLayout ll_viewmini;
    @Bind(R.id.dragView)
    LinearLayout ll_dragview;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidePanel;
    @Bind(R.id.listsong)
    ImageView img_listSong;

    @Bind(R.id.prev)
    ImageView img_mini_prev;
    @Bind(R.id.next)
    ImageView img_mini_next;

    private void initDatabase(Context context) {
        // TODO Auto-generated method stub

        try {
            SQLiteDataController.getInstance(context).isCreatedDatabase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // FolderPlaylistItem folder = new FolderPlaylistItem("MyPlaylist");
        // data.insertFolder(folder);
    }

    MediaController mediaController;
    @Bind(R.id.title)
    TextView tvTitle;
    @Bind(R.id.subtitle)
    TextView tvSubtitle;
    @Bind(R.id.tv_minisong)
    TextView tvMiniSong;
    @Bind(R.id.toolbar)
    Toolbar toolBar;
    @Bind(R.id.img_thumbnail)
    ImageView img_thumbnail;
    @Bind(R.id.imvRoot)
    ImageView imvRoot;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void settingBackground() {
        sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE);
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(this, imvRoot, type);
    }

    ActionBar actionBar;
    ImageView refresh_imv;
    TextView title;
    ViewPlayer topPlayer;

    //    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if(intent.getBooleanExtra("close_activity",false)){
//            this.finish();
//        }
//    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        try {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                try {
                    stopService(playIntent);
                } catch (Exception ex) {

                }
                finish();
            } else {
                LogHelper.d(TAG, "Activity onCreate");
                setContentView(R.layout.activity_player);
                ButterKnife.bind(this);
                topPlayer = new ViewPlayer(MusicPlayerActivity.this);
                ll_viewmini.setVisibility(View.GONE);
                mSlidePanel.setEnabled(false);
//        initializeToolbar();
//        TypefaceHelper.typeface(this);
                settingBackground();
                setSupportActionBar(toolBar);
                actionBar = getSupportActionBar();
                getSupportActionBar().setTitle(R.string.app_name);

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(R.layout.custom_actionbar_equalizer);
                refresh_imv = (ImageView) findViewById(R.id.onoff_imv);
                refresh_imv.setImageResource(R.drawable.refresh);
                refresh_imv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reFreshSong();
                    }
                });
                title = (TextView) findViewById(R.id.title);
                title.setText(R.string.app_name);
                try {
                    if (playIntent == null) {
                        playIntent = new Intent(this, MusicService.class);
                        bindService(playIntent, musicConnection, BIND_AUTO_CREATE);
                        startService(playIntent);
                    }
                } catch (Exception e) {

                }
                AdView mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                initDatabase(this);
                //initializeFromParams(savedInstanceState, getIntent());
                mSongmanager = new SongsManager();
                viewPager = (ViewPager) findViewById(R.id.viewpager);
                //setupViewPager(viewPager);
                tabLayout = (PagerSlidingTabStrip) findViewById(R.id.tab_layout);
                Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/OregonBold.ttf");
                tabLayout.setTypeface(typeFace, 1);
                DisplayMetrics dm = getResources().getDisplayMetrics();

                tabLayout.setTextSize((int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dm)));
//            tabLayout.addTab(tabLayout.newTab().setText(R.string.playlist));
//            tabLayout.addTab(tabLayout.newTab().setText(R.string.Songs));
//            tabLayout.addTab(tabLayout.newTab().setText(R.string.Album));
//            tabLayout.addTab(tabLayout.newTab().setText(R.string.Artists));
//            tabLayout.addTab(tabLayout.newTab().setText(R.string.Genre));
//            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

//        final PagerAdapter adapter = new PagerAdapter
//                (getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(adapter);
//            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//                @Override
//                public void onTabSelected(TabLayout.Tab tab) {
//                    int position = tab.getPosition();
//                    Log.e("onTabSelected", "onTabSelected  " + position);
//
//                    viewPager.setCurrentItem(position);
//                    if (position == 0) {
////                    ln_control.setVisibility(View.INVISIBLE);
//
//
//                    } else {
//                        if (musicSrv != null && musicSrv.isPlaying())
//                            ln_control.setVisibility(View.VISIBLE);
//                    }
//                }
//
//                @Override
//                public void onTabUnselected(TabLayout.Tab tab) {
//
//                }
//
//                @Override
//                public void onTabReselected(TabLayout.Tab tab) {
//
//                }
//            });
                mSlidePanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        topPlayer.hidePopupVolume();
                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                        if (mSlidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            topPlayer.hidePopupVolume();
                            img_listSong.setVisibility(View.GONE);
                            img_mini_next.setVisibility(View.GONE);
                            img_mini_prev.setVisibility(View.GONE);
                            imvPlaypause.setVisibility(View.VISIBLE);
                            SongsListFragment myFragment1 = (SongsListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LISTSONG_TAG);
                            if (myFragment1 != null) {
                                if (sharedPreferences.getInt("currentTag", 0) == FragmentTag.FAVORITE.ordinal()) {
                                    Gson gson = new Gson();
                                    String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
                                    Log.e("listSong ", listSong);

                                    ArrayList<Song> arrayListSong = new ArrayList<>();
                                    if (!listSong.equals("")) {
                                        arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
                                        }.getType());
                                    }
                                    myFragment1.updateList(arrayListSong);
                                    ((PlaylistFragment) (adapter.getCurrentFragment())).mListSong = arrayListSong;
                                    ((PlaylistFragment) (adapter.getCurrentFragment())).handle();

                                }
                            }
                        } else {
                            if (mSlidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                                img_listSong.setVisibility(View.VISIBLE);
                                img_mini_next.setVisibility(View.GONE);
                                img_mini_prev.setVisibility(View.GONE);
                                imvPlaypause.setVisibility(View.GONE);
                                topPlayer.checkFavoriteSong();
                            }
                        }
                    }
                });
                reFreshSong();
            }
        } catch (Exception ex) {
            reFreshSong();
        }
    }

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 11;

    @Override
    protected void onResume() {
        super.onResume();

        try {
            settingBackground();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(headphonePlugReceicer, filter);
            SongsListFragment myFragment1 = (SongsListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LISTSONG_TAG);
            if (myFragment1 != null) {
                if (sharedPreferences.getInt("currentTag", 0) == FragmentTag.FAVORITE.ordinal()) {
                    Gson gson = new Gson();
                    String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
                    Log.e("listSong ", listSong);

                    ArrayList<Song> arrayListSong = new ArrayList<>();
                    if (!listSong.equals("")) {
                        arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
                        }.getType());
                    }
                    myFragment1.updateList(arrayListSong);
                    ((PlaylistFragment) (adapter.getCurrentFragment())).mListSong = arrayListSong;
                    ((PlaylistFragment) (adapter.getCurrentFragment())).handle();

                }
            }
        } catch (Exception e) {

        }
        try {
            //Check device supported Accelerometer senssor or not
            if (AccelerometerManager.isSupported(this)) {

                //Start Accelerometer Listening
                AccelerometerManager.startListening(this);
            }
        } catch (Exception e) {

        }
        MainApplication.getInstance().trackScreenView("Home Screen");
        try {
            if (musicSrv != null) {
                tvTitle.setText(musicSrv.getSongs().get(musicSrv.getSongPosn()).getSongName());
                tvSubtitle.setText(musicSrv.getSongs().get(musicSrv.getSongPosn()).getAlbum());
                imvPlaypause.setImageResource(musicSrv.isPlaying() ? R.drawable.uamp_ic_pause_white_48dp : R.drawable.uamp_ic_play_arrow_white_48dp);
                tvMiniSong.setText("" + (musicSrv.getSongPosn() + 1) + "/" + musicSrv.getSongs().size());
                if (musicSrv.getSongs().get(musicSrv.getSongPosn()).getAlbum_art_uri() != null) {
                    Glide.with(this).load(musicSrv.getSongs().get(musicSrv.getSongPosn()).getAlbum_art_uri()).error(R.drawable.listening).crossFade().into(img_thumbnail);
                } else {
                    Glide.with(this).load(R.drawable.listening).crossFade().into(img_thumbnail);
                }
            }
        } catch (Exception e) {

        }

    }

    @Subscribe
    public void sortSong(SortPlain event) {
        Log.e("sortSong", "sortSong");
        SongsFragment songsFragment = (SongsFragment) adapter.getCurrentFragment();
        songsFragment.sortByTitle(event.typeSort, event.checkSort);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void SearchAction(SearchModel event) {
        String s = event.text;
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH_TAG);
        if (myFragment != null && myFragment.isVisible())

        {
            ArrayList<ItemSearch> mlistSearch = new ArrayList<ItemSearch>();
            if (!s.equals("")) {
                ItemSearch itemSong = new ItemSearch();
                itemSong.setTYPE(TypeSearch.SECTIONSONG);
                mlistSearch.add(itemSong);
                int numbSong = 0, numbArtist = 0, numbAlbum = 0;
                for (Song song : mlistSong) {
                    if (song.getSongName().toString().toLowerCase().contains(s.toLowerCase())) {
                        ItemSearch item = new ItemSearch();
                        item.setTYPE(TypeSearch.SONG);
                        item.setSong(song);
                        numbSong++;
                        mlistSearch.add(item);
                    }

                }
                if (numbSong == 0) {
                    mlistSearch.remove(itemSong);
                }
                ItemSearch itemArtist = new ItemSearch();
                itemArtist.setTYPE(TypeSearch.SECTIONARTIST);
                mlistSearch.add(itemArtist);
                for (Artist artist : mSongmanager.getArtistSong(mlistSong)) {
                    if (artist.getName_artist().toLowerCase().contains(s.toLowerCase())) {
                        ItemSearch item = new ItemSearch();
                        item.setTYPE(TypeSearch.ARTIST);
                        item.setArtist(artist);
                        numbArtist++;
                        mlistSearch.add(item);
                    }
                }
                if (numbArtist == 0) {
                    mlistSearch.remove(itemArtist);
                }
                ItemSearch itemAlbum = new ItemSearch();
                itemAlbum.setTYPE(TypeSearch.SECTIONALBUM);
                mlistSearch.add(itemAlbum);
                for (Album album : mSongmanager.getAlbumSong(mlistSong)) {
                    if (album.getAlbum_name().toLowerCase().contains(s.toLowerCase())) {
                        ItemSearch item = new ItemSearch();
                        item.setTYPE(TypeSearch.ALBUM);
                        item.setAlbum(album);
                        numbAlbum++;
                        mlistSearch.add(item);
                    }
                }
                if (numbAlbum == 0) {
                    mlistSearch.remove(itemAlbum);
                }
            } else {
                ItemSearch itemSong = new ItemSearch();
                itemSong.setTYPE(TypeSearch.SECTIONSONG);
                mlistSearch.add(itemSong);
                for (Song song : mlistSong) {
                    ItemSearch item = new ItemSearch();
                    item.setTYPE(TypeSearch.SONG);
                    item.setSong(song);
                    mlistSearch.add(item);
                }
                ItemSearch itemArtist = new ItemSearch();
                itemArtist.setTYPE(TypeSearch.SECTIONARTIST);
                mlistSearch.add(itemArtist);
                for (Artist artist : mSongmanager.getArtistSong(mlistSong)) {
                    ItemSearch item = new ItemSearch();
                    item.setTYPE(TypeSearch.ARTIST);
                    item.setArtist(artist);
                    mlistSearch.add(item);
                }
                ItemSearch itemAlbum = new ItemSearch();
                itemAlbum.setTYPE(TypeSearch.SECTIONALBUM);
                mlistSearch.add(itemAlbum);
                for (Album album : mSongmanager.getAlbumSong(mlistSong)) {
                    ItemSearch item = new ItemSearch();
                    item.setTYPE(TypeSearch.ALBUM);
                    item.setAlbum(album);
                    mlistSearch.add(item);
                }
            }

            myFragment.setSearchAdapter(mlistSearch);
        }
    }


    PagerAdapter adapter;

    ArrayList<Song> mlistSong = new ArrayList<>();

    public ArrayList<Song> getMlistSong() {
        return mlistSong;
    }

    public void setMlistSong(ArrayList<Song> mlistSong) {
        this.mlistSong = mlistSong;
    }

    @Override
    public void Finish(ArrayList<Song> mlist) {
        if (mlist != null && mlist.size() > 0) {
            mlistSong.clear();
            mlistSong.addAll(mlist);

            adapter = new PagerAdapter
                    (this, getSupportFragmentManager(), 6, mlistSong);
            adapter.getStringTitleSong(mlistSong.size());
            viewPager.setAdapter(adapter);
            tabLayout.setViewPager(viewPager);
            viewPager.setCurrentItem(1);
        }
//          musicSrv.setList(mlistSong);
//          musicBound = true;
//        musicSrv.setListSong(4);
//        musicSrv.playSong();

    }

    @Subscribe
    public void transitToSkin(TransitSkinEvent event) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SkinFragment bdf = new SkinFragment();
        ft.replace(R.id.container, bdf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Subscribe
    public void transitToGenres(TransitGenresEvent event) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        GenreFragment bdf = new GenreFragment(this, mlistSong);
        ft.replace(R.id.container, bdf, FRAGMENT_GENRES_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(FRAGMENT_GENRES_TAG);
        ft.commit();
    }

    @Subscribe
    public void transitToListSong(TransitSongListEvent event) {
//        searchView.onActionViewCollapsed();
//        menuItem.collapseActionView();
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH_TAG);
        if (myFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
            getSupportFragmentManager().popBackStack();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("TAG", event.fragmentTag);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SongsListFragment bdf = new SongsListFragment(event.getMlist());
        bdf.setTitle(event.getTitle());
        bdf.setArguments(bundle);
        ft.replace(R.id.container, bdf, FRAGMENT_LISTSONG_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(FRAGMENT_LISTSONG_TAG);
        ft.commit();
    }

    @Subscribe
    public void transitToAlbumListSong(TransitAlbumSongListEvent event) {
//        searchView.onActionViewCollapsed();
//        menuItem.collapseActionView();
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH_TAG);
        if (myFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
            getSupportFragmentManager().popBackStack();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("TAG", event.fragmentTag);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AlbumListSongFragment bdf = new AlbumListSongFragment(event.getMlist(), event.getAlbum());
        bdf.setTitle(event.getTitle());
        bdf.setArguments(bundle);
        ft.replace(R.id.container, bdf, FRAGMENT_LISTSONG_ALBUM_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(FRAGMENT_LISTSONG_ALBUM_TAG);
        ft.commit();
    }

    @Subscribe
    public void transitToPlayListSong(TransitPlaylistSongListEvent event) {
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH_TAG);
        if (myFragment != null && myFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PlaylistSongsListFragment bdf = new PlaylistSongsListFragment(event.getMlist());
        bdf.setTitle(event.getTitle());
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isplaylist",true);
//        bdf.setArguments(bundle);
        ft.replace(R.id.container, bdf, FRAGMENT_PLAYLISTSONG_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(FRAGMENT_PLAYLISTSONG_TAG);
        ft.commit();
    }

    @Subscribe
    public void playSongItem(TransitPlaySong event) {
        if (!event.isAddQueue) {
            mlist_play.clear();
        }
        ll_viewmini.setVisibility(View.VISIBLE);
        ln_control.setVisibility(View.VISIBLE);
        topPlayer.setVisibility(View.VISIBLE);
        mSlidePanel.setEnabled(true);
        mlist_play.addAll(event.getListSong());
        Log.e("mlist_play", "" + mlist_play.size());

        musicSrv.setList(mlist_play);
        musicSrv.setSong(event.posClick);

        tvTitle.setText(event.getListSong().get(event.posClick).getSongName());
        tvSubtitle.setText(event.getListSong().get(event.posClick).getAlbum());
        imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        tvMiniSong.setText("" + (musicSrv.getSongPosn() + 1) + "/" + musicSrv.getSongs().size());
        if (event.getListSong().get(event.posClick).getAlbum_art_uri() != null) {
            Glide.with(this).load(event.getListSong().get(event.posClick).getAlbum_art_uri()).error(R.drawable.listening).crossFade().into(img_thumbnail);
        } else {
            Glide.with(this).load(R.drawable.listening).crossFade().into(img_thumbnail);
        }
        musicSrv.playSong(event.posClick);
        //showNotification(mlist_play.get(event.posClick),true);
    }

    @Subscribe
    public void playNextSongItem(TransitNextSong event) {
//        if (!event.isAddQueue) {
//            mlist_play.clear();
//        }

        ln_control.setVisibility(View.VISIBLE);
//        mlist_play.addAll(event.getListSong());
        Song songNext = event.getListSong().get(event.getPosClick());

        ArrayList<Song> mNowPlayingList = new ArrayList<>();
        mNowPlayingList = musicSrv.getSongs();

        int posPlaying = 0;
        if (mNowPlayingList.size() == 0) {
            mNowPlayingList.add(songNext);
            //   Log.e("VOAAAA", "HELLO " + mNowPlayingList.size());
            musicSrv.setList(mNowPlayingList);
            musicSrv.setSong(0);
            musicSrv.playSong(0);
        } else {
            posPlaying = musicSrv.getSongPosn();
            mNowPlayingList.add((posPlaying + 1), songNext);
            musicSrv.setList(mNowPlayingList);
            musicSrv.setSong(posPlaying);
        }

        tvTitle.setText(mNowPlayingList.get(posPlaying).getSongName());
        tvSubtitle.setText(mNowPlayingList.get(posPlaying).getAlbum());
        imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        tvMiniSong.setText("" + (musicSrv.getSongPosn() + 1) + "/" + musicSrv.getSongs().size());
        if (event.getListSong().get(musicSrv.getSongPosn()).getAlbum_art_uri() != null) {
            Glide.with(this).load(event.getListSong().get(event.posClick).getAlbum_art_uri()).error(R.drawable.listening).crossFade().into(img_thumbnail);
        } else {
            Glide.with(this).load(R.drawable.listening).crossFade().into(img_thumbnail);
        }
    }

    @Subscribe
    public void setInfor(TransitInfo event) {
        tvTitle.setText(event.songArrayList.get(event.pos).getSongName());
        tvSubtitle.setText(event.songArrayList.get(event.pos).getAlbum());
        imvPlaypause.setImageResource((event.isPlaying) ? R.drawable.uamp_ic_pause_white_48dp : R.drawable.uamp_ic_play_arrow_white_48dp);
        tvMiniSong.setText("" + (musicSrv.getSongPosn() + 1) + "/" + musicSrv.getSongs().size());
        if (event.songArrayList.get(event.pos).getAlbum_art_uri() != null) {
            Glide.with(this).load(event.songArrayList.get(event.pos).getAlbum_art_uri()).error(R.drawable.listening).crossFade().into(img_thumbnail);
        } else {
            Glide.with(this).load(R.drawable.listening).crossFade().into(img_thumbnail);
        }
    }

    public void transitToSearch() {
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH_TAG);
        if (myFragment != null) {

            getSupportFragmentManager().beginTransaction().remove(myFragment).commit();
            getSupportFragmentManager().popBackStack();
        }
        ArrayList<ItemSearch> mlistSearch = new ArrayList<>();
        ItemSearch itemSong = new ItemSearch();
        itemSong.setTYPE(TypeSearch.SECTIONSONG);
        mlistSearch.add(itemSong);
        int numbSong = 0, numbArtist = 0, numbAlbum = 0;
        for (Song song : mlistSong) {
            ItemSearch item = new ItemSearch();
            item.setTYPE(TypeSearch.SONG);
            item.setSong(song);
            numbSong++;
            mlistSearch.add(item);
        }
        if (numbSong == 0) {
            mlistSearch.remove(itemSong);
        }
        ItemSearch itemArtist = new ItemSearch();
        itemArtist.setTYPE(TypeSearch.SECTIONARTIST);
        mlistSearch.add(itemArtist);
        for (Artist artist : mSongmanager.getArtistSong(mlistSong)) {
            ItemSearch item = new ItemSearch();
            item.setTYPE(TypeSearch.ARTIST);
            item.setArtist(artist);
            numbArtist++;
            mlistSearch.add(item);
        }
        if (numbArtist == 0) {
            mlistSearch.remove(itemArtist);
        }
        ItemSearch itemAlbum = new ItemSearch();
        itemAlbum.setTYPE(TypeSearch.SECTIONALBUM);
        mlistSearch.add(itemAlbum);
        for (Album album : mSongmanager.getAlbumSong(mlistSong)) {
            ItemSearch item = new ItemSearch();
            item.setTYPE(TypeSearch.ALBUM);
            item.setAlbum(album);
            numbAlbum++;
            mlistSearch.add(item);
        }
        if (numbAlbum == 0) {
            mlistSearch.remove(itemAlbum);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SearchFragment bdf = new SearchFragment(mlistSearch);
        ft.replace(R.id.container, bdf, FRAGMENT_SEARCH_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(FRAGMENT_SEARCH_TAG);
        ft.commit();
        //ln_control.setVisibility(View.GONE);

    }

    @Override
    public void onStart() {
        try {
            super.onStart();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.ACTION.PREV_ACTION);
//        intentFilter.addAction(Constants.ACTION.PLAY_ACTION);
//        intentFilter.addAction(Constants.ACTION.NEXT_ACTION);
//        intentFilter.addAction(Constants.ACTION.UPDATE_SONG_ACTION);
//        registerReceiver(audioReceiver, null);
            EventBus.getDefault().register(this);
        } catch (Exception ex) {
        }
    }

    @Bind(R.id.play_pause)
    public ImageView imvPlaypause;

    @OnClick(R.id.prev)
    public void onPrevSong() {
        if (musicSrv != null) musicSrv.playPrev();
        imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), musicSrv.isPlaying());

    }

    @OnClick(R.id.next)
    public void onNextSong() {
        if (musicSrv != null) musicSrv.playNext();
        imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), musicSrv.isPlaying());
    }

    @OnClick(R.id.play_pause)
    public void onPlayPause(ImageView imgView) {
        if (musicSrv.isPlaying()) {
            musicSrv.pausePlayer();
            imgView.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
        } else {
            musicSrv.resumePlayer();
            imgView.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        }
        //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), musicSrv.isPlaying());
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
//        if (musicBound) {
//            unbindService(musicConnection);
//            musicBound = false;
//        }
        //       unregisterReceiver(audioReceiver);
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
        try {
            if (musicSrv != null && !musicSrv.isPlaying()) {
                if (playIntent != null)
                    stopService(playIntent);
            }
            unregisterReceiver(headphonePlugReceicer);
            cancelNotification();
            //Check device supported Accelerometer senssor or not
            if (AccelerometerManager.isListening()) {

                //Start Accelerometer Listening
                AccelerometerManager.stopListening();
            }
        } catch (Exception ex) {

        }
    }


    @Override
    public void onBackPressed() {
        setTitle(getString(R.string.app_name));
        SearchFragment myFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH_TAG);
        SongsListFragment myFragment1 = (SongsListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LISTSONG_TAG);
        PlaylistSongsListFragment myFragment2 = (PlaylistSongsListFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_PLAYLISTSONG_TAG);
        GenreFragment myFragment3 = (GenreFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_GENRES_TAG);
        AlbumListSongFragment myFragment4 = (AlbumListSongFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_LISTSONG_ALBUM_TAG);
        if (myFragment != null) {
            ln_control.setVisibility(View.VISIBLE);
        }
        if (myFragment != null || myFragment1 != null || myFragment2 != null || myFragment3 != null || myFragment4 != null) {
            super.onBackPressed();
        } else {
            moveTaskToBack(true);
        }


    }

    @Bind(R.id.ln_control)
    LinearLayout ln_control;

    @OnClick(R.id.ln_control)
    public void transitToFullScreen() {
//        Intent mIntent = new Intent(this, FullScreenPlayerActivity.class);
//        mIntent.putExtra("currentpos", musicSrv.getSongPosn());
//        startActivity(mIntent);
        if (mSlidePanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        } else {
            mSlidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    public static MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            musicBound = true;
            topPlayer.setVisibility(View.INVISIBLE);
            ll_dragview.addView(topPlayer);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void cancelNotification() {
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                stopService(playIntent);
            } catch (Exception ex) {

            }
            finish();
        }
    }

    @Subscribe
    public void closeApp(CloseApp event) {
        if (event.isClose == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                try {
                    stopService(playIntent);
                } catch (Exception ex) {

                }
                finishAffinity();
            } else {
                finish();
            }

        }
    }

    public void reFreshSong() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED || hasPhonePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
       /* int hasPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (hasPhonePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }*/

        new AsynTaskGetSongLocal(this, this, false).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    new AsynTaskGetSongLocal(this, this, false).execute();
                } else {
                    // Permission Denied
                    Toast.makeText(MusicPlayerActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake(float force, boolean isNext) {
        if (sharedPreferences.getBoolean(Constants.ENABLE_SHAKE, false) == true) {
            if (musicSrv != null) {
                if (musicSrv.getSongs().size() > 0) {
                    if (isNext) {
                        musicSrv.playNext();
                        imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    } else {
                        musicSrv.playPrev();
                        imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    }
                }
            }
        }
    }

    @OnClick(R.id.listsong)
    public void showListSong() {
        topPlayer.showListSong();
    }


    /*
    * Create view bottom player
    * User can slide up from bottom to show fullscreen player
    * */


}
