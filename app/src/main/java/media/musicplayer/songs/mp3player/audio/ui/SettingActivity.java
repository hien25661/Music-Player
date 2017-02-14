package media.musicplayer.songs.mp3player.audio.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.CloseApp;
import media.musicplayer.songs.mp3player.audio.model.SingleBackground;
import media.musicplayer.songs.mp3player.audio.player.MusicService;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Bind(R.id.imvRoot)
    ImageView imvRoot;
    SharedPreferences sharedPreferences;
    public final static String MORE_TAG = "MORE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        settingBackground();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.Setting);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MoreFragment(), MORE_TAG)
                .commit();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void settingBackground() {
        sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE);
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(this, imvRoot, type);

    }

    public void setTitleString() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.Setting);
    }

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
//
//    @Subscribe
//    public void restartApplication(RestartEvent event) {
//        Log.e("restartApplication","restartApplication");
//        Intent i = new Intent(this, RestartActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
//    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            settingBackground();
        } catch (Exception e) {

        }
    }

    @Subscribe
    public void closeApp(CloseApp event) {
        try {
            stopService(new Intent(this, MusicService.class));
        } catch (Exception ex) {

        }
        Intent mIntent = new Intent(this, MusicPlayerActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.putExtra("EXIT", true);
        startActivity(mIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //finish();
    }



}
