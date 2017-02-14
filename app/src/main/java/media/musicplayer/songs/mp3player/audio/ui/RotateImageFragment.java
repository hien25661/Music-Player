package media.musicplayer.songs.mp3player.audio.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.SongAdapter_FullScreen;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaySong;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class RotateImageFragment extends Fragment implements AdapterView.OnItemClickListener{
    Context mcontext;
    @Bind(R.id.lv_song)
    ListView lv_song;
    static SongAdapter_FullScreen adapter;
    static ArrayList<Song> mlistSong = new ArrayList<>();
    public RotateImageFragment() {
        // Required empty public constructor
    }
    ReceiverPlaying receiverPlaying = new ReceiverPlaying();
    BroadcastReceiver receiver;

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rotate_image, container, false);
        mcontext = view.getContext();
        ButterKnife.bind(this, view);
        try {
            lv_song.setOnItemClickListener(this);
            mlistSong = MusicPlayerActivity.musicSrv.getSongs();
            for (Song s : mlistSong) {
                if (s.equals(mlistSong.get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                    s.setSelect(true);
                } else s.setSelect(false);
            }
            adapter = new SongAdapter_FullScreen(mcontext, R.layout.item_song_playing, mlistSong);
            lv_song.setAdapter(adapter);
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("HIEN25661", "" + intent.getBooleanExtra("MESSAGE", false));
                    try {
                        for (Song s : mlistSong) {
                            if (s.equals(mlistSong.get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                                s.setSelect(true);
                                if (intent.getBooleanExtra("MESSAGE", false) == true) {
                                    s.setIsPlaying(true);
                                } else {
                                    s.setIsPlaying(false);
                                }
                            } else {
                                s.setSelect(false);
                                s.setIsPlaying(false);
                            }

                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception ex) {

                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION.UPDATE_SONG_ACTION);
            mcontext.registerReceiver(receiver, filter);

        }catch (Exception ex){

        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //MusicPlayerActivity.musicSrv.playSong(position);
        //MusicPlayerActivity.musicSrv.showNotification(MusicPlayerActivity.musicSrv.getSongs().get(MusicPlayerActivity.musicSrv.getSongPosn()),true);
        EventBus.getDefault().post(new TransitPlaySong(mlistSong, position, true));
        for (Song s : mlistSong){
            if(s.equals(mlistSong.get(position))){
                s.setSelect(true);
            }
            else s.setSelect(false);
        }
        adapter.notifyDataSetChanged();
    }
    public static class ReceiverPlaying extends BroadcastReceiver{
        public ReceiverPlaying(){

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
            String action = intent.getAction();
            Log.e("Hasassasasasasasa", "Hien");
            switch (action) {
                case Constants.ACTION.PLAY_ACTION:
                    if (MusicPlayerActivity.musicSrv.isPlaying()) {

                        //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), false);
                    } else {

                        //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()),true);
                    }
                    for (Song s : mlistSong) {
                        if (s.equals(mlistSong.get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                            s.setSelect(true);
                        } else s.setSelect(false);
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case Constants.ACTION.PREV_ACTION:
                    if (MusicPlayerActivity.musicSrv != null) {

                    }
                    for (Song s : mlistSong) {
                        if (s.equals(mlistSong.get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                            s.setSelect(true);
                        } else s.setSelect(false);
                    }
                    adapter.notifyDataSetChanged();
                    //imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), musicSrv.isPlaying());
                    break;
                case Constants.ACTION.NEXT_ACTION:
                    if (MusicPlayerActivity.musicSrv != null) {

                    }
                    for (Song s : mlistSong) {
                        if (s.equals(mlistSong.get(MusicPlayerActivity.musicSrv.getSongPosn()))) {
                            s.setSelect(true);
                        } else s.setSelect(false);
                    }
                    adapter.notifyDataSetChanged();
                    //imvPlaypause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                    //showNotification(musicSrv.getSongs().get(musicSrv.getSongPosn()), musicSrv.isPlaying());
                    break;
                case Constants.ACTION.UPDATE_SONG_ACTION:
                    break;
                default:
                    break;
            }
        }catch (Exception ex){}
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("list playing Tab");
    }
}
