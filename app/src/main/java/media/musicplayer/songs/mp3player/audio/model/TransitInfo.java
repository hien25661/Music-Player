package media.musicplayer.songs.mp3player.audio.model;

import java.util.ArrayList;

/**
 * Created by Duc-Nguyen on 4/10/2016.
 */
public class TransitInfo {
    public int pos;
    public ArrayList<Song> songArrayList;
    public boolean isPlaying;



    public TransitInfo(int pos, ArrayList<Song> songArrayList,boolean isplaying) {
        this.pos = pos;
        this.songArrayList = songArrayList;
        this.isPlaying = isplaying;
    }
}
