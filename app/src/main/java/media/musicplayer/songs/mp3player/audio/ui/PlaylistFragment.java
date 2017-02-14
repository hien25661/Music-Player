package media.musicplayer.songs.mp3player.audio.ui;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.PlaylistAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.Playlist;
import media.musicplayer.songs.mp3player.audio.model.ReloadAdapter;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitSongListEvent;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {

    @Bind(R.id.listview_artist)
    ListView lvPlaylist;
    Playlist playlist;
    PlaylistAdapter mAdapter;
    Context mContext;
    SharedPreferences pref;
    public ArrayList<Song> mListSong = new ArrayList<>();

    @SuppressLint("ValidFragment")
    public PlaylistFragment(ArrayList<Song> mListSong) {
        // Required empty public constructor
        this.mListSong = mListSong;
    }


    public PlaylistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, v);
        mContext = v.getContext();
        handle();
        return v;
    }

    public boolean isCreatePlaylist;
    int idClick;

    public void reloadPlaylist() {
        Log.e("reloadPlaylist", "reloadPlaylist");
        if (playlist != null)
            playlist.getFolderListFromDB(mContext, mListSong);
        if (mAdapter != null)

            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("hidden: ", " " + hidden);
        if (!hidden) {
            reloadPlaylist();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            reloadPlaylist();
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                Log.d("MyFragment", "Not visible anymore.  Stopping audio.");
                // TODO stop audio playback
            }
        }
    }

    public void handle() {
        Fabric.with(mContext,new Crashlytics());
        playlist = new Playlist(getActivity());
//        gson = new Gson();
        pref = getContext().getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        if (pref.getBoolean("isCreatePlaylist", false)) {
            //playlist.getListFromSharePref(mContext);
            playlist.getFolderListFromDB(mContext, mListSong);

        } else {
            playlist.createRootPlaylist(mContext, mListSong);
            pref.edit().putBoolean("isCreatePlaylist", true).commit();
        }
        mAdapter = new PlaylistAdapter(mContext, R.layout.item_artist, playlist.getPlaylistList());
        lvPlaylist.setAdapter(mAdapter);
        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Playlist clickPlaylist = (Playlist) mAdapter.getItem(position);
                if (position < 3) {
//                    Intent mIntent = new Intent(getActivity(),EqualizerActivity.class);
//                    startActivity(mIntent);
                    EventBus.getDefault().post(new TransitSongListEvent(playlist.getSonglistByPosition(position), clickPlaylist.getPlaylistName(), FragmentTag.PLAYLIST));
                }
                else if(position == 4){
                    EventBus.getDefault().post(new TransitSongListEvent(playlist.getSonglistByPosition(position), clickPlaylist.getPlaylistName(), FragmentTag.FAVORITE));
                }
                else {
                    Log.e("clickPlaylist.getId(): ", " " + clickPlaylist.getId());

                    EventBus.getDefault().post(new TransitSongListEvent(playlist.getSongById(clickPlaylist.getId()), clickPlaylist.getPlaylistName(), FragmentTag.PLAYLIST));
                }
                //addNewFolderAndPutFile();
            }
        });


        lvPlaylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                idClick = position;
                if (idClick > 3)
                    showOption();
                return true;
            }
        });
    }

    public void addNewFolderAndPutFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter playlist name:");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input
        // as a password, and will mask the text
        // input.setInputType(InputType.TYPE_CLASS_TEXT |
        // InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFolderName = input.getText().toString();
                if (!checkValidate(newFolderName)) {
                    showFailedDialog(getActivity());
                    return;
                }
                // add file to folder playlist
                // add file to folder playlist
                Playlist folder = new Playlist();
                folder.setPlaylistName(newFolderName);
                long idFolder = SQLiteDataController.getInstance(getActivity()).insertFolder(folder);
                folder.setId((int) idFolder);
                reloadAdapter(new ReloadAdapter(folder, 0));

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    public void reloadAdapter(ReloadAdapter event) {
        Log.e("event", " " + event.action);
        switch (event.action) {
            //create new playlist
            case 0:
                playlist.addPlaylist(event.newPlaylist);
                break;
            //rename
            case 1:
                playlist.renamePlaylist(idClick, event.newName);
                break;
            //delete
            case 2:
                playlist.deletePlaylist(idClick);
                break;

        }
        mAdapter.notifyDataSetChanged();
        showSuccesfulDialog(getActivity());
    }

    public static void showSuccesfulDialog(Activity homeActivity) {
        // TODO Auto-generated method stub
        //new SweetAlertDialog(homeActivity).setTitleText(homeActivity,
        //		R.string.success).show();
        Toast.makeText(homeActivity,
                homeActivity.getResources().getString(R.string.success),
                Toast.LENGTH_SHORT).show();

    }

    public static void showFailedDialog(Activity homeActivity) {
        // TODO Auto-generated method stub
        //new SweetAlertDialog(homeActivity).setTitleText(homeActivity,
        //		R.string.failed).show();
        Toast.makeText(homeActivity,
                homeActivity.getResources().getString(R.string.failed),
                Toast.LENGTH_SHORT).show();

    }

    public boolean checkValidate(String newFolderName) {
        if (newFolderName.trim().equals("")) {
            return false;
        }
        return true;
    }

    public void showOption() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.SelectAction);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getResources().getString(R.string.Play));
        arrayAdapter.add(getResources().getString(R.string.Rename));
        arrayAdapter.add(getResources().getString(R.string.Delete));


        builderSingle.setNegativeButton(
                R.string.Cancel,
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
                        switch (which) {
                            case 0:
                                break;
                            case 1:
                                dialogRename();
                                break;
                            case 2:
                                reloadAdapter(new ReloadAdapter("", 2));
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    public void dialogRename() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter new name:");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input
        // as a password, and will mask the text
        // input.setInputType(InputType.TYPE_CLASS_TEXT |
        // InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFolderName = input.getText().toString();
                if (!checkValidate(newFolderName)) {
                    showFailedDialog(getActivity());
                    return;
                }
                // add file to folder playlist
                // add file to folder playlist

                reloadAdapter(new ReloadAdapter(newFolderName, 1));

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Playlist Tab");
    }
}