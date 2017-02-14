package media.musicplayer.songs.mp3player.audio.model;

import java.util.ArrayList;

/**
 * Created by hiennguyen on 4/2/16.
 */
public class Genres {
    private String genres_name;
    private ArrayList<Song> mlistSong = new ArrayList<>();

    public String getGenres_name() {
        return genres_name;
    }

    public void setGenres_name(String genres_name) {
        if (genres_name != null)
        this.genres_name = genres_name;
    }

    public ArrayList<Song> getMlistSong() {
        return mlistSong;
    }

    public void setMlistSong(ArrayList<Song> mlistSong) {
        this.mlistSong = mlistSong;
    }

    public Genres(String genres_name, ArrayList<Song> mlistSong) {
        this.genres_name = genres_name;
        this.mlistSong = mlistSong;
    }

    public Genres() {
        this.genres_name = "unknown";
        this.mlistSong = new ArrayList<>();
    }
}
