package media.musicplayer.songs.mp3player.audio.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import media.musicplayer.songs.mp3player.audio.R;

public class SplashActivity extends AppCompatActivity {
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
     /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        Log.e("VOA","Vao day");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent intent = new Intent(SplashActivity.this, MusicPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
