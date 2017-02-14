package media.musicplayer.songs.mp3player.audio.model;

import java.io.File;

/**
 * Created by Hien on 7/2/2016.
 */
public class Item {
    public String file;
    public File mFile;
    public int icon;
    public int numbofFiles;

    public Item(File mFile,String file, Integer icon) {
        this.mFile = mFile;
        this.file = file;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return file;
    }
}