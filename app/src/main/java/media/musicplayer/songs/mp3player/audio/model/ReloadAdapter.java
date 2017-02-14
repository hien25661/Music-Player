package media.musicplayer.songs.mp3player.audio.model;

/**
 * Created by SF on 06/04/2016.
 */
public class ReloadAdapter {
    public Playlist newPlaylist;
    public int action;
    public String newName = "";
    public ReloadAdapter(Playlist newPlaylist,int action) {
        this.newPlaylist = newPlaylist;
        this.action = action;
    }
    public ReloadAdapter(String newName,int action) {
        this.newName = newName;
        this.action = action;
    }
}
