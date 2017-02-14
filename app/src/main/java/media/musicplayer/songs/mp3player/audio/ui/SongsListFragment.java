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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.SongAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.interfaces.GetSongFinish;
import media.musicplayer.songs.mp3player.audio.manager.SongController;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.Playlist;
import media.musicplayer.songs.mp3player.audio.model.SingleBackground;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaySong;
import media.musicplayer.songs.mp3player.audio.task.AsynTaskGetSongLocal;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsListFragment extends Fragment implements GetSongFinish, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    Context mContext;
    ListView listview_song;
    public ArrayList<Song> mListSong = new ArrayList<>();
    SongAdapter mAdapter;
    AsynTaskGetSongLocal asynTaskGetSongLocal;
    private String title;
    int songClick;
    SongController songController;
    FragmentTag currentTag = FragmentTag.PLAYLIST;

    @SuppressLint("ValidFragment")
    public SongsListFragment(ArrayList<Song> mlist) {
        this.mListSong = mlist;
    }

    public SongsListFragment() {
    }

    @Bind(R.id.imvRoot)
    ImageView imvRoot;
    SharedPreferences sharedPreferences;

    public void settingBackground(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, context.MODE_PRIVATE);
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(context, imvRoot, type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songlist, container, false);
        ButterKnife.bind(this, view);
        settingBackground(getActivity());
        listview_song = (ListView) view.findViewById(R.id.listview_song);
        // listview_song.setOnItemClickListener(this);
        mContext = view.getContext();
        Fabric.with(mContext, new Crashlytics());
        currentTag = (FragmentTag) (getArguments().get("TAG"));
        Log.e("currentTag", " " + currentTag);
        sharedPreferences = mContext.getSharedPreferences(Constants.MUSIC_PLAYER, mContext.MODE_PRIVATE);
        songController = new SongController(mContext);
        ((MusicPlayerActivity) getActivity()).setTitle(title);
        if (currentTag == FragmentTag.PLAYLIST) {
            sharedPreferences.edit().putInt("currentTag", FragmentTag.PLAYLIST.ordinal()).commit();
        } else if (currentTag == FragmentTag.FAVORITE) {
            sharedPreferences.edit().putInt("currentTag", FragmentTag.FAVORITE.ordinal()).commit();
        } else {
            sharedPreferences.edit().putInt("currentTag", FragmentTag.PLAYLIST.ordinal()).commit();
        }
        // asynTaskGetSongLocal = (AsynTaskGetSongLocal) new AsynTaskGetSongLocal(mContext,this).execute();
        //boolean myValue = this.getArguments().getBoolean("isplaylist");
        for (Song s : mListSong) {
            s.setSelect(false);
        }
        mAdapter = new SongAdapter(mContext, R.layout.item_song, mListSong);
        listview_song.setAdapter(mAdapter);
        // listview_song.setOnItemLongClickListener(this);
        mAdapter.setOnShowOption(new SongAdapter.onShowOption() {
            @Override
            public void clickShowOption(int view, int pos) {
                if (view == 0) {
                    songClick = pos;
                    if (currentTag == FragmentTag.PLAYLIST) {
                        showOptionPlaylist();
                    } else if (currentTag == FragmentTag.FAVORITE) {
                        showOption();
                    } else {
                        showOption();
                    }
                } else {
                    Song itemSong = (Song) mListSong.get(pos);
                    Log.e("itemSong", " " + itemSong.getIdSong());
                    songClick = pos;
                    EventBus.getDefault().post(new TransitPlaySong(mListSong, pos, false));
                }
            }
        });
        return view;

    }

    public void updateList(final ArrayList<Song> mListSong) {
        currentTag = (FragmentTag) (getArguments().get("TAG"));
        Log.e("currentTag", " " + currentTag);
        songController = new SongController(mContext);
        ((MusicPlayerActivity) getActivity()).setTitle(title);
        // asynTaskGetSongLocal = (AsynTaskGetSongLocal) new AsynTaskGetSongLocal(mContext,this).execute();
        //boolean myValue = this.getArguments().getBoolean("isplaylist");
        this.mListSong = mListSong;
        for (Song s : mListSong) {
            s.setSelect(false);
        }
        mAdapter = new SongAdapter(mContext, R.layout.item_song, mListSong);
        listview_song.setAdapter(mAdapter);
        // listview_song.setOnItemLongClickListener(this);
        mAdapter.setOnShowOption(new SongAdapter.onShowOption() {
            @Override
            public void clickShowOption(int view, int pos) {
                if (view == 0) {
                    songClick = pos;
                    if (currentTag == FragmentTag.PLAYLIST) {
                        showOptionPlaylist();
                    } else if (currentTag == FragmentTag.FAVORITE) {
                        showOption();
                    } else {
                        showOption();
                    }
                } else {
                    Song itemSong = (Song) mListSong.get(pos);
                    Log.e("itemSong", " " + itemSong.getIdSong());
                    songClick = pos;
                    EventBus.getDefault().post(new TransitPlaySong(mListSong, pos, false));
                }
            }
        });
    }

    @Override
    public void Finish(ArrayList<Song> mlist) {

    }

    public void showOption() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle(R.string.SelectAction);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getResources().getString(R.string.AddToPlaylist));
        arrayAdapter.add(getResources().getString(R.string.RingToneSet));
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
                                if (currentTag == FragmentTag.FAVORITE) {
//                                    Log.e("HIEN25661", "" + mListSong.get(songClick).getSongName());
                                    Log.e("TOA",""+mListSong.size());
                                    removeFavorite(mListSong.get(songClick));
                                    mListSong.remove(songClick);
                                    mAdapter.notifyDataSetChanged();

                                }
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    private void removeFavorite(Song song) {
        Gson gson = new Gson();
        String listSong = sharedPreferences.getString(Constants.FAVORITE, "");
        Log.e("listSong ", listSong);


        ArrayList<Song> arrayListSong = new ArrayList<>();
        if (!listSong.equals("")) {
            arrayListSong = gson.fromJson(listSong, new TypeToken<ArrayList<Song>>() {
            }.getType());
        }
        int index = -1;
        for (int i = 0; i < arrayListSong.size(); i++) {
            if (song.getPath().equals(arrayListSong.get(i).getPath())) {
                index = i;
                Log.e("VOA", "" + index);
                break;
            }
        }
        if (index > -1) {
            arrayListSong.remove(index);
        }
        gson = new Gson();
        String newList = gson.toJson(arrayListSong);
        Log.e("newList ", newList);

        sharedPreferences.edit().putString(Constants.FAVORITE, newList).commit();
    }

    public void showOptionPlaylist() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle(R.string.SelectAction);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getResources().getString(R.string.AddToPlaylist));
        arrayAdapter.add(getResources().getString(R.string.RingToneSet));
        arrayAdapter.add(getResources().getString(R.string.RemoveFromPlaylist));


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
                                int success = SQLiteDataController.getInstance(getActivity()).deletefileById(mListSong.get(songClick).getIdSong());
                                if (success > 0) {
                                    Utils.showSuccesfulDialog(getActivity());
                                } else {
                                    Utils.showFailedDialog(getActivity());
                                }
                                mListSong.remove(songClick);
                                mAdapter.notifyDataSetChanged();
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
        Log.e("itemSong", " " + itemSong.getIdSong());
        songClick = position;
        EventBus.getDefault().post(new TransitPlaySong(mListSong, position, false));

        // mMediaFragmentListener.onMediaItemSelected(itemSong.getMedia_item());
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
        songClick = position;
        if (currentTag == FragmentTag.PLAYLIST) {
            showOptionPlaylist();
        } else if (currentTag == FragmentTag.FAVORITE) {
            showOption();
        } else {
            showOption();
        }
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
                        if (which == 1) {
                            if (!checkExistInFavorite(mListSong.get(songClick))) {
                                addFavorite(mListSong.get(songClick));
                            }
                        } else {
                            int idPlaylist = playlistList.get(which + 3).getId();
                            mListSong.get(songClick).setIdPlaylist(idPlaylist);
                            SQLiteDataController.getInstance(getActivity()).insertFilePlaylist(mListSong.get(songClick));
                            dialog.dismiss();
                        }
                    }
                });
        builderSingle.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Song list Tab");
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
