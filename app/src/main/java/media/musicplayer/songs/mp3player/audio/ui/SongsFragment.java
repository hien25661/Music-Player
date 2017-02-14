package media.musicplayer.songs.mp3player.audio.ui;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.SongAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.interfaces.GetSongFinish;
import media.musicplayer.songs.mp3player.audio.manager.SongController;
import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.Playlist;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitAlbumSongListEvent;
import media.musicplayer.songs.mp3player.audio.model.TransitNextSong;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaySong;
import media.musicplayer.songs.mp3player.audio.model.TransitSongListEvent;
import media.musicplayer.songs.mp3player.audio.new_adapter.New_Song_Adapter;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;
import media.musicplayer.songs.mp3player.audio.task.AsynTaskGetSongLocal;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.Utils;
import media.musicplayer.songs.mp3player.audio.widgets.SortDialog;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class SongsFragment extends Fragment implements GetSongFinish, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {
    Context mContext;
    RecyclerView listview_song;
    ArrayList<Song> mListSong = new ArrayList<>();
    SongAdapter mAdapter;
    AsynTaskGetSongLocal asynTaskGetSongLocal;
    FloatingActionButton fbSetting, fbSearch, fbSuffer, fbSort, fbMultiSelect;
    ArrayList<Song> mListSelect = new ArrayList<>();
    @Bind(R.id.tv_numSongs)
    TextView tv_numOfSongs;
    SharedPreferences sharedPreferences;
    @SuppressLint("ValidFragment")
    public SongsFragment(ArrayList<Song> mlist) {
        this.mListSong = mlist;
    }
    New_Song_Adapter mNewAdapter;
    public SongsFragment() {
    }

    //d
    int songClick;
    LinearLayout ln_tab1;
    LinearLayout ln_tab2;
    FloatingActionButton fbBack, fbAddPlaylist, fbPlayPlaylist, fbDelete;
    FloatingActionsMenu mFloatingActionsMenu;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        listview_song = (RecyclerView) view.findViewById(R.id.listview_song);
        mFloatingActionsMenu = (FloatingActionsMenu)view.findViewById(R.id.fab_parent);
        mFloatingActionsMenu.expand();
        //listview_song.setOnItemClickListener(this);
        mContext = view.getContext();
        ButterKnife.bind(this, view);
        Fabric.with(mContext, new Crashlytics());
        tv_numOfSongs.setText(" " + mListSong.size());
        // asynTaskGetSongLocal = (AsynTaskGetSongLocal) new AsynTaskGetSongLocal(mContext,this).execute();
        mAdapter = new SongAdapter(mContext, R.layout.item_song, mListSong);
        mNewAdapter = new New_Song_Adapter(mListSong,mContext);
        mNewAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Song itemSong = (Song) mListSong.get(position);

                if (mAdapter.isChoiceMode()) {
                    mListSong.get(position).setSelect(!itemSong.isSelect());
                    mAdapter.notifyDataSetChanged();
                    return;
                }

                // mMediaFragmentListener.onMediaItemSelected(itemSong);
                songClick = position;
//        ArrayList<Song> songArrayList = new ArrayList<>();
//        songArrayList.add(itemSong);
                EventBus.getDefault().post(new TransitPlaySong(mListSong, songClick, false));
            }
        });
        listview_song.setAdapter(mNewAdapter);
        listview_song.setLayoutManager(new media.musicplayer.songs.mp3player.audio.widgets.LinearLayoutManager(mContext));
        listview_song.setHasFixedSize(true);
        //listview_song.setOnItemLongClickListener(this);
        fbSetting = (FloatingActionButton) view.findViewById(R.id.fb_setting);
        fbSearch = (FloatingActionButton) view.findViewById(R.id.fb_search);
        fbSuffer = (FloatingActionButton) view.findViewById(R.id.fb_suffer);
        fbSort = (FloatingActionButton) view.findViewById(R.id.fb_sort);
        fbMultiSelect = (FloatingActionButton) view.findViewById(R.id.fb_multiselect);
        ln_tab1 = (LinearLayout) view.findViewById(R.id.fab_1);
        ln_tab2 = (LinearLayout) view.findViewById(R.id.fab_2);

        fbBack = (FloatingActionButton) view.findViewById(R.id.fb_back_to_tab1);
        fbAddPlaylist = (FloatingActionButton) view.findViewById(R.id.fb_add_playlist);
        fbPlayPlaylist = (FloatingActionButton) view.findViewById(R.id.fb_play_select);
        fbDelete = (FloatingActionButton) view.findViewById(R.id.fb_delete_select);

        fbSetting.setOnClickListener(this);
        fbSearch.setOnClickListener(this);
        fbSuffer.setOnClickListener(this);
        fbSort.setOnClickListener(this);
        fbMultiSelect.setOnClickListener(this);

        fbBack.setOnClickListener(this);
        fbAddPlaylist.setOnClickListener(this);
        fbPlayPlaylist.setOnClickListener(this);
        fbDelete.setOnClickListener(this);
        sharedPreferences = mContext.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);

        mNewAdapter.setOnShowOption(new New_Song_Adapter.onShowOption() {
            @Override
            public void clickShowOption(int viewid,int pos) {
                if(viewid == 0) {
                    if (mAdapter.isChoiceMode()) {
                        return;
                    }
                    songClick = pos;
                    resetSelect();
                    mListSong.get(pos).setSelect(true);
                    showOption();
                }
                else {
                    Song itemSong = (Song) mListSong.get(pos);

                    if (mAdapter.isChoiceMode()) {
                        mListSong.get(pos).setSelect(!itemSong.isSelect());
                        mAdapter.notifyDataSetChanged();
                        return;
                    }

                    // mMediaFragmentListener.onMediaItemSelected(itemSong);
                    songClick = pos;
//        ArrayList<Song> songArrayList = new ArrayList<>();
//        songArrayList.add(itemSong);
                    EventBus.getDefault().post(new TransitPlaySong(mListSong, songClick, false));
                }
            }
        });

        return view;

    }

    @Override
    public void Finish(ArrayList<Song> mlist) {

    }

    ArrayList<Playlist> playlistList = new ArrayList<>();

    public void showListPlaylist() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.SelectPlaylist);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        playlistList = SQLiteDataController.getInstance(getActivity()).getAllFolder();
        ArrayList<String> titlePlaylist = new ArrayList<>();
        titlePlaylist.add(getResources().getString(R.string.NewPlaylist));
        for (int i = 3; i < playlistList.size(); i++) {
            titlePlaylist.add(playlistList.get(i).getPlaylistName());

        }

        arrayAdapter.addAll(titlePlaylist);

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
                        if (which == 0) {
                            addNewFolderAndPutFile();
                        } else if (which == 2) {
                            if(!checkExistInFavorite(mListSong.get(songClick))){
                                addFavorite(mListSong.get(songClick));
                            }
                        } else {
                            int idPlaylist = playlistList.get(which + 2).getId();
                            Log.e("idPlaylist: ", " " + idPlaylist + " " + which + "  " + playlistList.get(which + 2).getPlaylistName());
                            getListSelect(mListSong);
                            for (Song song : mListSelect) {
                                song.setIdPlaylist(idPlaylist);
                            }
                            SQLiteDataController.getInstance(getActivity()).insertListFilePlaylist(mListSelect);
                            dialog.dismiss();
                        }
                    }
                });
        builderSingle.show();
    }
    private void addFavorite(Song song) {
        Gson gson = new Gson();
        String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
        Log.e("listSong ", listSong);


        ArrayList<Song> arrayListSong = new ArrayList<>();
        if (!listSong.equals("")) {
            arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
            }.getType());
        }
        arrayListSong.add(song);
        gson = new Gson();
        String newList = gson.toJson(arrayListSong);
        Log.e("newList ", newList);

        sharedPreferences.edit().putString(Constants.FAVORITE, newList).commit();
    }
    private boolean checkExistInFavorite(Song song) {
        Gson gson = new Gson();
        String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
        Log.e("listSong ", listSong);


        ArrayList<Song> arrayListSong = new ArrayList<>();
        if (!listSong.equals("")) {
            arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
            }.getType());
        }
        for (Song itemSong : arrayListSong) {
            if (itemSong.getPath().equals(song.getPath())) {
                return true;
            }
        }
        return false;
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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFolderName = input.getText().toString();
                if (!Utils.checkValidate(newFolderName)) {
                    Utils.showFailedDialog(getActivity());
                    return;
                }
                // add file to folder playlist
                // add file to folder playlist
                Playlist folder = new Playlist();
                folder.setPlaylistName(newFolderName);
                long idFolder = SQLiteDataController.getInstance(getActivity()).insertFolder(folder);
                folder.setId((int) idFolder);
                getListSelect(mListSong);
                for (Song song : mListSelect) {
                    song.setIdPlaylist((int) idFolder);
                }
                SQLiteDataController.getInstance(getActivity()).insertListFilePlaylist(mListSelect);

//                reloadAdapter(new ReloadAdapter(folder, 0));
                Utils.showSuccesfulDialog(getActivity());
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

    SongController songController;

    public void showOption() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.SelectAction);
        songController = new SongController(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getResources().getString(R.string.AddToPlaylist));
        arrayAdapter.add(getResources().getString(R.string.RingToneSet));
        arrayAdapter.add(getResources().getString(R.string.NextPlay));
        arrayAdapter.add(getResources().getString(R.string.Delete));
        arrayAdapter.add(getResources().getString(R.string.GotoAlbum));

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
                                showListPlaylist();
                                break;
                            case 1:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (Settings.System.canWrite(mContext)) {
                                        boolean isSuccess = songController.setRingTone(mListSong.get(songClick));
                                        if (isSuccess) {
                                            Utils.showSuccesfulDialog(getActivity());
                                        } else {
                                            Utils.showFailedDialog(getActivity());
                                        }
                                    } else {
                                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivityForResult(intent, REQUEST_CHANGE_SETTING);
                                    }
                                    return;
                                }
                                boolean isSuccess = songController.setRingTone(mListSong.get(songClick));
                                if (isSuccess) {
                                    Utils.showSuccesfulDialog(getActivity());
                                } else {
                                    Utils.showFailedDialog(getActivity());
                                }
                                break;
                            case 2:
                                EventBus.getDefault().post(new TransitNextSong(mListSong, songClick, false));
                                break;
                            case 3:
                                ArrayList<Song> tempList = new ArrayList<>(mListSong);
                                for (Iterator<Song> songIte = tempList.listIterator(); songIte.hasNext(); ) {
                                    Song a = songIte.next();
                                    if (a.isSelect()) {
                                        File file = new File(a.getPath());
                                        file.delete();
                                        songIte.remove();
                                    }
                                }
                                mListSong.clear();
                                mListSong.addAll(tempList);
                                mAdapter.notifyDataSetChanged();
                                break;
                            case 4:
                                AlbumsFragment albumsFragment = AlbumsFragment.getInstance(mContext, mListSong);
                                Log.e("BOA", "size " + albumsFragment.mlistAlbum.size());
                                Song song = mListSong.get(songClick);
                                for (Album album : albumsFragment.mlistAlbum) {
                                    if (isExistInAlbum(song, album)) {
                                        //EventBus.getDefault().post(new TransitSongListEvent(album.getMlistSong(), album.getAlbum_name(), FragmentTag.ALBUM));
                                        EventBus.getDefault().post(new TransitAlbumSongListEvent(album.getMlistSong(), album.getAlbum_name(), FragmentTag.ALBUM,album));
                                        break;
                                    }
                                }

                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Song itemSong = (Song) parent.getItemAtPosition(position);

        if (mAdapter.isChoiceMode()) {
            mListSong.get(position).setSelect(!itemSong.isSelect());
            mAdapter.notifyDataSetChanged();
            return;
        }

        // mMediaFragmentListener.onMediaItemSelected(itemSong);
        songClick = position;
//        ArrayList<Song> songArrayList = new ArrayList<>();
//        songArrayList.add(itemSong);
        EventBus.getDefault().post(new TransitPlaySong(mListSong, songClick, false));
    }

    public void getListSelect(ArrayList<Song> allsong) {
        mListSelect.clear();
        for (Song item : allsong) {
            if (item.isSelect())
                mListSelect.add(item);
        }
    }

    public void resetSelect() {
        for (Song song : mListSong) {
            song.setSelect(false);
        }
        mListSelect.clear();
        mAdapter.notifyDataSetChanged();
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

                switch (typeSort) {
                    case 1:
                        first = arr.get(i).getArtist().charAt(0);
                        last = arr.get(i + 1).getArtist().charAt(0);
                        break;
                    case 2:
                        first = arr.get(i).getAlbum().charAt(0);
                        last = arr.get(i + 1).getAlbum().charAt(0);
                        break;
                    case 3:
                        String fistDuration = arr.get(i).getDuration();
                        String lastDuration = arr.get(i + 1).getDuration();
                        if (Integer.valueOf(fistDuration) > Integer.valueOf(lastDuration)) {
                            first = "a".charAt(0);
                            last = "b".charAt(0);
                        } else {
                            first = "b".charAt(0);
                            last = "a".charAt(0);
                        }
                        break;
                    case 4:
                        String fistDate = arr.get(i).getDate_added();
                        String lastDate = arr.get(i + 1).getDate_added();
                        if (Long.valueOf(fistDate) > Long.valueOf(lastDate)) {
                            first = "a".charAt(0);
                            last = "b".charAt(0);
                        } else {
                            first = "b".charAt(0);
                            last = "a".charAt(0);
                        }
                        break;

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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter.isChoiceMode()) {
            return false;
        }
        songClick = position;
        resetSelect();
        mListSong.get(position).setSelect(true);
        showOption();

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_setting:
                Intent mIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(mIntent);
                break;
            case R.id.fb_search:
                ((MusicPlayerActivity) getActivity()).transitToSearch();
                break;
            case R.id.fb_suffer:
                Random rand = new Random();
                int songPosn = rand
                        .nextInt((mListSong.size() - 1) - 0 + 1) + 0;
                Log.e("mListSong", "" + mListSong.size());

                EventBus.getDefault().post(new TransitPlaySong(mListSong, songPosn, false));
                break;
            case R.id.fb_sort:
                new SortDialog(getActivity()).show();
                break;
            case R.id.fb_multiselect:
                //listview_song.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                changeToMultiChoice(true);
                ln_tab1.setVisibility(View.GONE);
                ln_tab2.setVisibility(View.VISIBLE);
                break;
            case R.id.fb_back_to_tab1:
                ln_tab1.setVisibility(View.VISIBLE);
                ln_tab2.setVisibility(View.GONE);
                resetSelect();
                changeToMultiChoice(false);
                break;
            case R.id.fb_add_playlist:
                showListPlaylist();
                break;
            case R.id.fb_play_select:
                Log.e("fb_play_select", "fb_play_select");
                getListSelect(mListSong);
                if (mListSelect.size() > 0)
                    EventBus.getDefault().post(new TransitPlaySong(mListSelect, 0, false));
                break;
            case R.id.fb_delete_select:
                Log.e("fb_delete_select", "fb_delete_select" + mListSong.size());
                ArrayList<Song> tempList = new ArrayList<>(mListSong);
                for (Iterator<Song> songIte = tempList.listIterator(); songIte.hasNext(); ) {
                    Song a = songIte.next();
                    if (a.isSelect()) {
                        File file = new File(a.getPath());
                        file.delete();
                        songIte.remove();
                    }
                }
                mListSong.clear();
                mListSong.addAll(tempList);
                mAdapter.notifyDataSetChanged();
                break;

        }
    }

    public void changeToMultiChoice(boolean isMulti) {
        mAdapter.setChoiceMode(isMulti);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Song Tab");
    }
    private boolean isExistInAlbum(Song song,Album album){
        for (Song mSong : album.getMlistSong()){
            if(song.getPath().equals(mSong.getPath())){
                return true;
            }
        }
        return false;
    }
    private static final int REQUEST_CHANGE_SETTING = 100;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHANGE_SETTING) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(mContext)) {
                    // Do stuff here
                    Log.e("VOA", "CAN write");
                    boolean isSuccess = songController.setRingTone(mListSong.get(songClick));
                    if (isSuccess) {
                        Utils.showSuccesfulDialog(getActivity());
                    } else {
                        Utils.showFailedDialog(getActivity());
                    }
                }
            }
            return;
        }
    }
}
