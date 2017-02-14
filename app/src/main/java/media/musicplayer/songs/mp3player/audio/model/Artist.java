package media.musicplayer.songs.mp3player.audio.model;

import java.util.ArrayList;

/**
 * Created by hiennguyen on 4/3/16.
 */
public class Artist {
    private String name_artist;
    private ArrayList<Song> mlist_Song = new ArrayList<>();

    public String getName_artist() {
        return name_artist;
    }

    public void setName_artist(String name_artist) {
        this.name_artist = name_artist;
    }

    public ArrayList<Song> getMlist_Song() {
        return mlist_Song;
    }

    public void setMlist_Song(ArrayList<Song> mlist_Song) {
        this.mlist_Song = mlist_Song;
    }
}
