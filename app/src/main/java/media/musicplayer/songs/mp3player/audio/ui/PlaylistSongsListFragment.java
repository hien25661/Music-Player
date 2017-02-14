package media.musicplayer.songs.mp3player.audio.ui;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.PlaylistSongAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.interfaces.GetSongFinish;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.task.AsynTaskGetSongLocal;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistSongsListFragment extends Fragment implements GetSongFinish, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    Context mContext;
    ListView listview_song;
    ArrayList<Song> mListSong = new ArrayList<>();
    PlaylistSongAdapter mAdapter;
    AsynTaskGetSongLocal asynTaskGetSongLocal;
    private String title;


    public PlaylistSongsListFragment() {
    }

    @SuppressLint("ValidFragment")
    public PlaylistSongsListFragment(ArrayList<Song> mlist) {
        this.mListSong = mlist;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songlist, container, false);
        listview_song = (ListView) view.findViewById(R.id.listview_song);
        listview_song.setOnItemClickListener(this);
        mContext = view.getContext();
        Fabric.with(mContext,new Crashlytics());
        ((MusicPlayerActivity)getActivity()).setTitle(title);
        // asynTaskGetSongLocal = (AsynTaskGetSongLocal) new AsynTaskGetSongLocal(mContext,this).execute();
        //boolean myValue = this.getArguments().getBoolean("isplaylist");
        mAdapter = new PlaylistSongAdapter(mContext, R.layout.item_song, mListSong);
        listview_song.setAdapter(mAdapter);
        listview_song.setOnItemLongClickListener(this);
        return view;

    }

    @Override
    public void Finish(ArrayList<Song> mlist) {

    }

    public void showOption() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select action: ");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add("Add to playlist");
        arrayAdapter.add("Add to next song");
        arrayAdapter.add("Set as ringtone");
        arrayAdapter.add("Remove from playlist");

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song itemSong = (Song) parent.getItemAtPosition(position);
        //mMediaFragmentListener.onMediaItemSelected(itemSong.getMedia_item());
        // mMediaFragmentListener.onMediaItemSelected(itemSong);
        //showOption();
    }

    public void sortByTitle(int typeSort, boolean decrease) {
        mListSong = bubbleSort(mListSong, typeSort, decrease);
        //if (mAdapter != null)
        mAdapter.notifyDataSetChanged();
    }

    public ArrayList<Song> bubbleSort(ArrayList<Song> arr, int typeSort, boolean des) {
        boolean swapped = true;
        int j = 0;

        Song tmp;
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < arr.size() - j; i++) {
                char first = arr.get(i).getSongName().charAt(0);
                char last = arr.get(i + 1).getSongName().charAt(0);
                if (typeSort == 1) {
                    first = arr.get(i).getArtist().charAt(0);
                    last = arr.get(i + 1).getArtist().charAt(0);
                } else if (typeSort == 2) {
                    first = arr.get(i).getAlbum().charAt(0);
                    last = arr.get(i + 1).getAlbum().charAt(0);
                }
                Log.e("first:", " " + first);
                Log.e("last:", " " + last);

                if (!des) {
                    if (first > last) {
                        tmp = arr.get(i);
                        arr.set(i, arr.get(i + 1));
                        arr.set(i + 1, tmp);
                        swapped = true;
                    }
                } else {
                    if (first < last) {
                        tmp = arr.get(i);
                        arr.set(i, arr.get(i + 1));
                        arr.set(i + 1, tmp);
                        swapped = true;
                    }
                }

            }
        }
        return arr;
    }

    public boolean compareFirstLast(char fisrt, char last) {
        if (fisrt > last)
            return true;
        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //showOption();
        return true;
    }
//    public boolean compareFirstLast(char fisrt, char last)
//    {
//        if (fisrt > last)
//            return true;
//        return false;
//    }
//    public boolean compareFirstLast(char fisrt, char last)
//    {
//        if (fisrt > last)
//            return true;
//        return false;
//    }


    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Playlist Song Tab");
    }
}
