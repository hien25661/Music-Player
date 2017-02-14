package media.musicplayer.songs.mp3player.audio.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.ArtistAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.model.Artist;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitSongListEvent;
import media.musicplayer.songs.mp3player.audio.new_adapter.New_Artist_Adapter;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;
import media.musicplayer.songs.mp3player.audio.widgets.LinearLayoutManager;

/**
 *
 */
public class ArtistFragment extends Fragment implements AdapterView.OnItemClickListener{
    ArrayList<Artist> mlistArtist = new ArrayList<>();
    RecyclerView lv_artist;
    New_Artist_Adapter mAdapter;
    Context mContext;

    public ArtistFragment() {
    }
    @SuppressLint("ValidFragment")
    public ArtistFragment(ArrayList<Song> mlist) {
        boolean check = false;

        for (Song song : mlist){
            Artist artist = new Artist();
            artist.setName_artist((song.getArtist() == null || song.getArtist().equalsIgnoreCase(""))?"Unknow":song.getArtist());
            check = false;
            for(int i = 0 ; i < mlistArtist.size();i++){
                if(artist.getName_artist().equalsIgnoreCase(mlistArtist.get(i).getName_artist())){
                    check = true;
                }

            }
            if(check == false) {
                mlistArtist.add(artist);
            }
        }
        for (Artist artist:mlistArtist) {
            for (Song song : mlist){
                if(song.getArtist() != null && artist.getName_artist()!= null && song.getArtist().equalsIgnoreCase(artist.getName_artist())){
                    artist.getMlist_Song().add(song);
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        mContext = view.getContext();
        Fabric.with(mContext,new Crashlytics());
        lv_artist = (RecyclerView)view.findViewById(R.id.listview_artist);
        mAdapter = new New_Artist_Adapter(mlistArtist,mContext);
        lv_artist.setAdapter(mAdapter);
        lv_artist.setLayoutManager(new LinearLayoutManager(mContext));
        lv_artist.setHasFixedSize(true);
        //lv_artist.setOnItemClickListener(this);
        mAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Artist artist = (Artist) mlistArtist.get(position);
                EventBus.getDefault().post(new TransitSongListEvent(artist.getMlist_Song(),artist.getName_artist(), FragmentTag.ARTIST));
            }
        });
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Artist artist = (Artist) parent.getItemAtPosition(position);
        EventBus.getDefault().post(new TransitSongListEvent(artist.getMlist_Song(),artist.getName_artist(), FragmentTag.ARTIST));
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Artist Tab");
    }
}
