package media.musicplayer.songs.mp3player.audio.model;

import media.musicplayer.songs.mp3player.audio.interfaces.TypeSearch;

/**
 * Created by hiennguyen on 4/5/16.
 */
public class ItemSearch {
    private TypeSearch TYPE;
    private Song song;
    private Artist artist;
    private Album album;


    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }


    public TypeSearch getTYPE() {
        return TYPE;
    }

    public void setTYPE(TypeSearch TYPE) {
        this.TYPE = TYPE;
    }
}
