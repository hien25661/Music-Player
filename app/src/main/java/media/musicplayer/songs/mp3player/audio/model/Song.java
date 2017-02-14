package media.musicplayer.songs.mp3player.audio.model;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import media.musicplayer.songs.mp3player.audio.R;

/**
 * Created by hiennguyen on 4/2/16.
 */
public class Song {
    private String album = "";
    private int id_playlist;
    private int id_genres;
    private String path = "";
    private String date_added = "";
    private String artist = "";
    private byte[] art;
    private String genre;
    private String duration;
    int idSong;
    int idPlaylist;
    String songName;
    String year;
    //    String album_art_path;
    boolean isSelect;
    private boolean isPlaying;
    int idMusic;
    long albumId;
    String album_art_uri;

    public Song() {
        album = "";
        path = "";
        date_added = "0";
        artist = "Unknown";
        duration = "0";
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public Uri getAlbum_art_uri() {
        if (album_art_uri != null)
            return Uri.parse(album_art_uri);
        return Uri.parse("");
    }

    public String getAlbum_art_uriDB() {
        if (album_art_uri != null)
            return album_art_uri;
        return "";
    }

    public void setAlbum_art_uri(String album_art_uri) {
        this.album_art_uri = album_art_uri;
    }

    public int getIdMusic() {
        return idMusic;
    }

    public void setIdMusic(int idMusic) {
        this.idMusic = idMusic;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

//    public String getAlbum_art_path() {
//        return album_art_path;
//    }
//
//    public void setAlbum_art_path(String album_art_path) {
//        this.album_art_path = album_art_path;
//    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getIdSong() {
        return idSong;
    }

    public void setIdSong(int idSong) {
        this.idSong = idSong;
    }

    public int getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getGenre() {
//        genre = getGenreFromSong(mcontext, this);
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId_playlist() {
        return id_playlist;
    }

    public void setId_playlist(int id_playlist) {
        this.id_playlist = id_playlist;
    }

    public int getId_genres() {
        return id_genres;
    }

    public void setId_genres(int id_genres) {
        this.id_genres = id_genres;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public byte[] getArt() {
        return art;
    }

    public void setArt(byte[] art) {
        this.art = art;
    }


    public String getDuration() {
        if (duration == null) duration ="0";
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    private static String[] genresProjection = {
            MediaStore.Audio.Genres.NAME,
            MediaStore.Audio.Genres._ID
    };

    public String getGenreFromSong(Context context, Song song) {
        Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", song.getIdMusic());
        String genres = "<unknown>";
        Cursor genresCursor = context.getContentResolver().query(uri,
                genresProjection, null, null, null);
        int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

        if (genresCursor.moveToFirst()) {
            do {
                genres = genresCursor.getString(genre_column_index);
            } while (genresCursor.moveToNext());
        }
        genresCursor.close();
        return genres;
    }

    public String getAlbumArtFromSong(Context context, Song song) {
        Cursor artCursor = null;
        String albumArt = null;
        try {
            artCursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.AlbumColumns.ALBUM_ART},
                    MediaStore.Audio.Media._ID + " =?",
                    new String[]{String.valueOf(song.getAlbumId())},
                    null);
            if (artCursor != null) {
                if (artCursor.moveToNext()) {
                    if (artCursor.getString(0) != null) {
                        albumArt = "file://" + artCursor.getString(0);
                    }

                } else {
                    albumArt = null;
                }
            }
        } finally {
            if (artCursor != null) artCursor.close();
        }
        return albumArt;
    }


//     * Builds a HashMap of all albums and their individual songs count.
//     */
//    public static void buildAlbumsLibrary(Context mContext) {
//        ArrayList<Album> albumArrayList = new ArrayList<>();
//        Cursor albumsCursor = mContext.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.NUMBER_OF_SONGS },
//                null,
//                null,
//                null);
//
//        if (albumsCursor==null)
//            return;
//
//        for (int i=0; i < albumsCursor.getCount(); i++) {
//            albumsCursor.moveToPosition(i);
//            mSongsCountMap.put(albumsCursor.getString(0) + albumsCursor.getString(1), albumsCursor.getInt(2));
//        }
//
//        albumsCursor.close();
//    }
}
