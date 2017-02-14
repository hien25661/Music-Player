package media.musicplayer.songs.mp3player.audio.model;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by hiennguyen on 4/2/16.
 */
public class Album extends ArrayList<Song> {
    private String album_name;
    private ArrayList<Song> mlistSong = new ArrayList<>();
    private String album_art_url;
    public String getAlbum_name() {
        return album_name;
    }
    public Uri album_art_uri;
    public void setAlbum_name(String album_name) {
        if (album_name != null)
        this.album_name = album_name;
    }

    public ArrayList<Song> getMlistSong() {
        return mlistSong;
    }

    public void setMlistSong(ArrayList<Song> mlistSong) {
        this.mlistSong = mlistSong;
    }

    public Album(String album_name, ArrayList<Song> mlistSong) {
        this.album_name = album_name;
        this.mlistSong = mlistSong;
    }

    public String getAlbum_art_url() {
        return album_art_url;
    }

    public void setAlbum_art_url(String album_art_url) {
        this.album_art_url = album_art_url;
    }

    public Album() {
        this.album_name = "unknown";
        this.mlistSong = new ArrayList<>();
    }

    public Uri getAlbum_art_uri() {
        return album_art_uri;
    }

    public void setAlbum_art_uri(Uri album_art_uri) {
        this.album_art_uri = album_art_uri;
    }
}
