package media.musicplayer.songs.mp3player.audio.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class RestartActivity extends AppCompatActivity {

    /**
     * This activity shows nothing; instead, it restarts the android process
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    protected void onResume() {
        super.onResume();
        startActivityForResult(new Intent(this, MusicPlayerActivity.class), 0);
    }

}
