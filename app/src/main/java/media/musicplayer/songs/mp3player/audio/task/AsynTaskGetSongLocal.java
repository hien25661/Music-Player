package media.musicplayer.songs.mp3player.audio.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.interfaces.GetSongFinish;
import media.musicplayer.songs.mp3player.audio.manager.SongsManager;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.widgets.SplashDialog;

/**
 * Created by hiennguyen on 4/2/16.
 */
public class AsynTaskGetSongLocal extends AsyncTask<Void, Void, Void> {
    GetSongFinish getSongFinish;
    Context mcontext;
    ArrayList<Song> result_list = new ArrayList<>();
    SongsManager manager = new SongsManager();
    SplashDialog progressDialog;
    private final int interval = 3000; // 3 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            try {
                if (progressDialog != null)
                    progressDialog.dismiss();
            } catch (Exception e) {

            }
        }
    };
    AlertDialog progressAndroidDialog;


    boolean isSplash;

    public AsynTaskGetSongLocal(Context context, GetSongFinish listener, boolean isSplash) {
        this.mcontext = context;
        this.getSongFinish = listener;
        this.isSplash = isSplash;
        progressAndroidDialog = new SpotsDialog(mcontext, R.style.Custom);
        progressDialog = new SplashDialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!isSplash) {
            progressAndroidDialog.show();
        } else {
            progressDialog.show();
            handler.postDelayed(runnable, interval);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        result_list = manager.ListAllSongs(mcontext);
        SQLiteDataController.getInstance(mcontext).saveEQPresets();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressAndroidDialog.dismiss();
        getSongFinish.Finish(result_list);
    }
}
