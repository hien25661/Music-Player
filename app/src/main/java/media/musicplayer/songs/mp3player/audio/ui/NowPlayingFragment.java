package media.musicplayer.songs.mp3player.audio.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment {
    Context mcontext;
    @Bind(R.id.img_record)
    ImageView img_record;
    @Bind(R.id.tv_songname)
    TextView tv_songname;
    @Bind(R.id.tv_infor)
    TextView tv_infor;
    BroadcastReceiver receiver;
    Bitmap bm;
    boolean isShow = false;

    public NowPlayingFragment() {
    }

    IntentFilter filter = new IntentFilter();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        mcontext = view.getContext();
        Fabric.with(mcontext,new Crashlytics());
        ButterKnife.bind(this, view);
       // Song song = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
        try {
            loadImageAlbum();
        } catch (Exception e) {

        }
        filter.addAction(Constants.ACTION.MAIN_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("VOAAAA","Vao day");
                loadImageAlbum();
            }
        };
        mcontext.registerReceiver(receiver, filter);
        return view;
    }

    public void loadImageAlbum() {
        Song song = MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn());
        Log.e("VOA", "URI " + song.getAlbum_art_uri());
        if (isShow == true) {
            if (song.getAlbum_art_uri() != null) {
                Glide.with(mcontext).load(song.getAlbum_art_uri()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.music_nowplaying).into(img_record);
            } else {
                Glide.with(mcontext).load(R.drawable.music_nowplaying).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(img_record);
            }
            Log.e("VOAAAA","Vao day" + song.getSongName());
            tv_songname.setText(song.getSongName());
            tv_infor.setText(song.getArtist());
            tv_songname.setSelected(true);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isShow = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isShow = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Nowplaying screen");
    }

}
