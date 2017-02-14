package media.musicplayer.songs.mp3player.audio.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.AlbumAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.interfaces.GetSongFinish;
import media.musicplayer.songs.mp3player.audio.manager.SongsManager;
import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitAlbumSongListEvent;
import media.musicplayer.songs.mp3player.audio.model.TransitSongListEvent;
import media.musicplayer.songs.mp3player.audio.new_adapter.New_Album_Adapter;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment implements GetSongFinish, AdapterView.OnItemClickListener {
    Context mContext;
    RecyclerView lv_album;
    New_Album_Adapter mAdapter;
    public ArrayList<Album> mlistAlbum = new ArrayList<>();
    public static AlbumsFragment sInstance ;
    public AlbumsFragment() {
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public static synchronized AlbumsFragment getInstance(Context context,ArrayList<Song> mlist) {
        if (sInstance == null)
            sInstance = new AlbumsFragment(context,mlist);
        return sInstance;
    }

    @SuppressLint("ValidFragment")
    public AlbumsFragment(Context context, ArrayList<Song> mlist) {
        this.mContext = context;
        Fabric.with(mContext,new Crashlytics());
        SongsManager.buildMediaStoreAlbumArtHash(mContext);
        boolean check = false;
        for (Song song : mlist) {
            Album album = new Album();
            album.setAlbum_name(song.getAlbum());
            Uri albumArtPath = null;
//            albumArtPath = SongsManager.mMediaStoreAlbumArtMap.get(String.valueOf(song.getAlbumId()));
            albumArtPath = song.getAlbum_art_uri();
            Log.e("albumArtPath", " " + albumArtPath+" va "+String.valueOf(song.getAlbumId()));

            if (albumArtPath != null) {
                album.setAlbum_art_uri(albumArtPath);
                Log.e("setAlbum_art_url: ", ""+album.getAlbum_art_uri());
            } else {
                album.setAlbum_art_uri(null);
            }
            Log.e("song.getAlbum()", " " + song.getAlbum());
            check = false;
            for (int i = 0; i < mlistAlbum.size(); i++) {
                if (album.getAlbum_name().trim().equalsIgnoreCase(mlistAlbum.get(i).getAlbum_name().trim())) {
                    check = true;
                }
            }
            if (check == false) {
                mlistAlbum.add(album);
            }
        }
        for (Album album : mlistAlbum) {
            for (Song song : mlist) {
                if (song.getAlbum() != null && song.getAlbum().trim().equalsIgnoreCase(album.getAlbum_name().trim())) {
                    album.getMlistSong().add(song);
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        mContext = view.getContext();
        lv_album = (RecyclerView) view.findViewById(R.id.listview_album);
        //new AsynTaskGetSongLocal(mContext,this).execute();
        mAdapter = new New_Album_Adapter(mlistAlbum,mContext);
        lv_album.setAdapter(mAdapter);
        lv_album.setLayoutManager(new GridLayoutManager(mContext,2));
        lv_album.setHasFixedSize(true);
        //lv_album.setOnItemClickListener(this);
        mAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e("TTT","ABC");
                Album album = (Album)mlistAlbum.get(position);
                EventBus.getDefault().post(new TransitAlbumSongListEvent(album.getMlistSong(), album.getAlbum_name(), FragmentTag.ALBUM,album));
            }
        });
        return view;
    }

    @Override
    public void Finish(ArrayList<Song> mlist) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Album album = (Album) parent.getItemAtPosition(position);
        EventBus.getDefault().post(new TransitAlbumSongListEvent(album.getMlistSong(), album.getAlbum_name(), FragmentTag.ALBUM,album));
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Album Tab");
    }
}
