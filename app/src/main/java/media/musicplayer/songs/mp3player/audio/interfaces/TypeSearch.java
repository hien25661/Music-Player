package media.musicplayer.songs.mp3player.audio.interfaces;

/**
 * Created by hiennguyen on 4/5/16.
 */
public enum TypeSearch {
    SONG(0), ARTIST(1), ALBUM(2), SECTIONSONG(3), SECTIONARTIST(4), SECTIONALBUM(5),GENRES(6);

    private final int value;
    private TypeSearch(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
