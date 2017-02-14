package media.musicplayer.songs.mp3player.audio.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * Created by Hien-Nguyen on 4/5/2016.
 */
public class Playlist {
    int id;
    String playlistName;
    ArrayList<Song> songArrayList;
    ArrayList<Playlist> playlistList;
    Context context;

    public Playlist(Context context) {
        playlistName = "";
        this.context = context;
        songArrayList = new ArrayList<>();
        playlistList = new ArrayList<>();
    }

    public Playlist() {
        this.playlistName = "";
        songArrayList = new ArrayList<>();
        playlistList = new ArrayList<>();
    }

    public Playlist(int id, String playlistName) {
        this.id = id;
        this.playlistName = playlistName;
        songArrayList = new ArrayList<>();
        playlistList = new ArrayList<>();
    }

    public Playlist(int id, String playlistName, ArrayList<Song> songArrayList) {
        this.id = id;
        this.playlistName = playlistName;
        this.songArrayList = songArrayList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    public ArrayList<Song> getSongById(int idPlaylist) {
        return SQLiteDataController.getInstance(context).getAllFileByID(idPlaylist);
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList = songArrayList;
    }

    public ArrayList<Playlist> getPlaylistList() {
        return playlistList;
    }

    public ArrayList<Song> getSonglistByPosition(int position) {
        return this.playlistList.get(position).getSongArrayList();
    }

    public void setPlaylistList(ArrayList<Playlist> playlistList) {
        this.playlistList = playlistList;
    }

    //Method manager playlist
    public void addPlaylist(Playlist playlist) {
        //SQLiteDataController.getInstance(context).insertFolder(playlist);
        this.playlistList.add(playlist);
    }

    public void deletePlaylist(int id) {
        SQLiteDataController.getInstance(context).deletefolder(this.playlistList.get(id).getId());
        this.playlistList.remove(id);


    }

    public void renamePlaylist(int id, String newName) {
        this.playlistList.get(id).setPlaylistName(newName);
        SQLiteDataController.getInstance(context).updateFolder(this.playlistList.get(id));
    }

    public void createRootPlaylist(Context context, ArrayList<Song> songList) {
        playlistList.clear();
        playlistList.add(new Playlist(1, context.getResources().getString(R.string.RecentlyAdd)));
        playlistList.add(new Playlist(2, context.getResources().getString(R.string.LastPlay)));
        playlistList.add(new Playlist(3, context.getResources().getString(R.string.MyRecord)));
        playlistList.add(new Playlist(4, context.getResources().getString(R.string.MyPlaylist)));
        playlistList.add(new Playlist(5, context.getResources().getString(R.string.Favorite)));

        SQLiteDataController.getInstance(context).insertFolder(playlistList.get(0));
        SQLiteDataController.getInstance(context).insertFolder(playlistList.get(1));
        SQLiteDataController.getInstance(context).insertFolder(playlistList.get(2));
        SQLiteDataController.getInstance(context).insertFolder(playlistList.get(3));
        long a = SQLiteDataController.getInstance(context).insertFolder(playlistList.get(4));
        Log.e("SQLiteDataController  ", ""+a );
        addRecentlyPlaylist(songList);
    }

    public final static long fivedays = 86400 * 5 * 1000;

    public void addRecentlyPlaylist(ArrayList<Song> mListSong) {
        ArrayList<Song> newList = new ArrayList<>();
        //newList = bubbleSort(mListSong);
        long currentTime = System.currentTimeMillis();
        Log.e("currentTime: ", " " + currentTime);
        for (Song song : mListSong) {
            Log.e("song.getDate_added()", " " + song.getDate_added()+ " "+song.getSongName());
            if (currentTime - Long.parseLong(song.getDate_added())*1000 < fivedays)
                newList.add(song);
        }
        playlistList.get(0).setSongArrayList(newList);
    }

    public ArrayList<Song> bubbleSort(ArrayList<Song> arr) {
        boolean swapped = true;
        int j = 0;

        Song tmp;
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < arr.size() - j; i++) {
                char first, last;
                String fistDuration = arr.get(i).getDate_added();
                String lastDuration = arr.get(i + 1).getDate_added();
                if (Long.valueOf(fistDuration) > Long.valueOf(lastDuration)) {
                    first = "a".charAt(0);
                    last = "b".charAt(0);
                } else {
                    first = "b".charAt(0);
                    last = "a".charAt(0);
                }

                Log.e("first:", " " + first);
                Log.e("last:", " " + last);

//                if (!des) {
                if (first > last) {
                    tmp = arr.get(i);
                    arr.set(i, arr.get(i + 1));
                    arr.set(i + 1, tmp);
                    swapped = true;
                }
//                } else {
//                    if (first < last) {
//                        tmp = arr.get(i);
//                        arr.set(i, arr.get(i + 1));
//                        arr.set(i + 1, tmp);
//                        swapped = true;
//                    }
//                }

            }
        }
        return arr;
    }

    public void getListFromSharePref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        String playListString = sharedPreferences.getString("playlist", "");
        Gson gson = new Gson();
        if (!playListString.equals(""))
            playlistList = gson.fromJson(playListString, new TypeToken<ArrayList<Playlist>>() {
            }.getType());
    }

    public void getFolderListFromDB(Context context, ArrayList<Song> songList) {
        playlistList.clear();
        playlistList.add(new Playlist(1, context.getResources().getString(R.string.RecentlyAdd)));
        playlistList.add(new Playlist(2, context.getResources().getString(R.string.LastPlay)));
        playlistList.add(new Playlist(3, context.getResources().getString(R.string.MyRecord)));
        playlistList.add(new Playlist(4, context.getResources().getString(R.string.MyPlaylist)));
        playlistList.add(new Playlist(5, context.getResources().getString(R.string.Favorite)));
        ArrayList<Playlist> dataPlaylist = new ArrayList<>();
        dataPlaylist = SQLiteDataController.getInstance(context).getAllFolder();
        if (dataPlaylist.size() > 5) {
            for (int i = 5; i < dataPlaylist.size(); i++) {
                playlistList.add(dataPlaylist.get(i));
            }
        }
        addRecentlyPlaylist(songList);
        addLastPlayed();
        addRecordFile(songList);
        addFavorite();
    }

    public void addLastPlayed() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        String playListString = sharedPreferences.getString(Constants.LAST_PLAYED, "");
        Gson gson = new Gson();
        Log.e("playListString ", playListString);
        if (!playListString.equals("")) {
            ArrayList<Song> songArrayList = gson.fromJson(playListString, new TypeToken<ArrayList<Song>>() {
            }.getType());
            playlistList.get(1).setSongArrayList(songArrayList);
        }

    }
    public void addFavorite() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        String playListString = sharedPreferences.getString(Constants.FAVORITE, "");
        Gson gson = new Gson();
        Log.e("playListString ", playListString);
        if (!playListString.equals("")) {
            ArrayList<Song> songArrayList = gson.fromJson(playListString, new TypeToken<ArrayList<Song>>() {
            }.getType());
            playlistList.get(4).setSongArrayList(songArrayList);
        }

    }

    public void addRecordFile(ArrayList<Song> songArrayList) {
        ArrayList<Song> audioList = new ArrayList<>();
        for (Song itemSong : songArrayList) {
            if (itemSong.getPath().contains("/Sounds/"))
            {
                audioList.add(itemSong);
            }
        }
        playlistList.get(2).setSongArrayList(audioList);
    }
}
