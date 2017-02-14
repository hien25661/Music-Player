package media.musicplayer.songs.mp3player.audio.model;

import java.util.ArrayList;

/**
 * Created by Duc-Nguyen on 4/3/2016.
 */
public class TransitPlaylistSongListEvent {

    private ArrayList<Song> mlist = new ArrayList<>();
    private String title;

    public TransitPlaylistSongListEvent(ArrayList<Song> mlist, String title) {
        this.setMlist(mlist);
        this.title = title;

    }

    public ArrayList<Song> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<Song> mlist) {
        this.mlist = mlist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
