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
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.GenresAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.manager.SongsManager;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.Genres;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitSongListEvent;
import media.musicplayer.songs.mp3player.audio.new_adapter.New_Genres_Adapter;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;
import media.musicplayer.songs.mp3player.audio.widgets.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenreFragment extends Fragment implements AdapterView.OnItemClickListener {
    RecyclerView lv_genges;
    ArrayList<Genres> mlistGenres = new ArrayList<>();
    New_Genres_Adapter mAdapter;
    Context mContext;
    ArrayList<Song> allSongList = new ArrayList<>();

    public GenreFragment() {
    }

    @SuppressLint("ValidFragment")
    public GenreFragment(Context context, ArrayList<Song> mlist) {
//        boolean check = false;
//
//        for (Song song : mlist) {
//            Genres genres = new Genres();
//            genres.setGenres_name(song.getGenre(context));
//            check = false;
//            for (int i = 0; i < mlistGenres.size(); i++) {
//                if (genres.getGenres_name().equalsIgnoreCase(mlistGenres.get(i).getGenres_name())) {
//                    check = true;
//                }
//            }
//            if (check == false) {
//                mlistGenres.add(genres);
//            }
//        }
        allSongList = mlist;
        mlistGenres = SongsManager.buildGenresLibrary(context);
//        for (Genres genres:mlistGenres) {
//            for (Song song : mlist){
//                if(genres.getGenres_name()!= null && song.getGenre(context).equalsIgnoreCase(genres.getGenres_name())){
//                    genres.getMlistSong().add(song);
//                }
//            }
//        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);
        mContext = view.getContext();
        Fabric.with(mContext,new Crashlytics());
        lv_genges = (RecyclerView) view.findViewById(R.id.listview_genres);
        mAdapter = new New_Genres_Adapter(mlistGenres,mContext);
        lv_genges.setAdapter(mAdapter);
        lv_genges.setLayoutManager(new LinearLayoutManager(mContext));
        lv_genges.setHasFixedSize(true);
       // lv_genges.setOnItemClickListener(this);
        mAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Genres genres = (Genres) (mlistGenres.get(position));
                EventBus.getDefault().post(new TransitSongListEvent(getSongFromGenre(genres), genres.getGenres_name(), FragmentTag.GENRE));
            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Genres genres = (Genres) (parent.getItemAtPosition(position));
        EventBus.getDefault().post(new TransitSongListEvent(getSongFromGenre(genres), genres.getGenres_name(), FragmentTag.GENRE));
    }

    public ArrayList<Song> getSongFromGenre(Genres genres) {
        ArrayList<Song> genreSong = new ArrayList<>();
        ArrayList<String> uriSong = new ArrayList<>();

        //get list duong dan voi genres name
        for (Map.Entry<String, String> e : SongsManager.mGenresHashMap.entrySet()) {
            String value = e.getValue();
            if (genres.getGenres_name().equals(value)) {
                String key = e.getKey();
                uriSong.add(key);
            }
        }

        //tu list duogn dan tim bai hat song
        for (String songUri : uriSong) {
            for (Song song : allSongList) {
                if (song.getPath().equals(songUri)) {
                    genreSong.add(song);
                    break;
                }
            }
        }

        return genreSong;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Genres Tab");
    }
}
