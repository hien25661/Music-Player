package media.musicplayer.songs.mp3player.audio.model;

import java.util.ArrayList;

/**
 * Created by Hien on 10/29/2016.
 */
public class TransitAlbumSongListEvent {
    private ArrayList<Song> mlist = new ArrayList<>();
    private String title;
    public FragmentTag fragmentTag;
    private Album mAlbum;

    public TransitAlbumSongListEvent(ArrayList<Song> mlist, String title, FragmentTag fragmentTag, Album album) {
        this.mlist = mlist;
        this.title = title;
        this.fragmentTag = fragmentTag;
        this.mAlbum = album;
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


    public Album getAlbum() {
        return mAlbum;
    }

    public void setAlbum(Album mAlbum) {
        this.mAlbum = mAlbum;
    }
}
