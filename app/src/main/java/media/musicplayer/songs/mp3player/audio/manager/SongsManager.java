package media.musicplayer.songs.mp3player.audio.manager;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.Artist;
import media.musicplayer.songs.mp3player.audio.model.Genres;
import media.musicplayer.songs.mp3player.audio.model.Song;


/**
 * Created by hiennguyen on 4/2/16.
 */
public class SongsManager {


    final String MEDIA_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/";
    private ArrayList<Song> songsList = new ArrayList<>();
    private String mp3Pattern = ".mp3";
    final String IGNORE_MEDIA_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/Android";

    // Constructor
    public SongsManager() {

    }

    private String[] STAR = {"*"};
    private String[] SELECTION = new String[]{
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID};

    public ArrayList<Song> ListAllSongs(Context context) {
        Cursor cursor;
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        if (isSdPresent()) {
            cursor = context.getContentResolver().query(allsongsuri, SELECTION, selection, null, null);
            int id_column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            if (cursor != null) {
                Log.e("cursor song: ", " " + cursor.getCount());
                if (cursor.moveToFirst()) {


                    do {
                        Song song = new Song();
                        String songname = cursor
                                .getString(cursor
                                        .getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String fullpath = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATA));
                        song.setPath(fullpath);
                        song.setSongName(songname);
                        song.setArtist(cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                        song.setAlbum(cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                        song.setDuration(cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DURATION)));
                        song.setDate_added(cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));

//                        String genres = "<unknown>";
                        int musicId = Integer.parseInt(cursor.getString(id_column_index));
                        song.setIdMusic(musicId);

//                        Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
//                        Cursor genresCursor = context.getContentResolver().query(uri,
//                                genresProjection, null, null, null);
//                        int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
//                        if (genresCursor != null) {
//                            if (genresCursor.moveToLast()) {
////                                do {
//                                    genres = genresCursor.getString(genre_column_index);
////                                } while (genresCursor.moveToNext());
//                            }
//                            genresCursor.close();
//                        }
//                        song.setGenre(genres);
                        long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        song.setAlbumId(albumId);
                        Uri sArtworkUri = Uri
                                .parse("content://media/external/audio/albumart");
                        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                        song.setAlbum_art_uri(albumArtUri.toString());
//                        Cursor artCursor = null;
//                        String albumArt = null;
//                        try {
//                            artCursor = context.getContentResolver().query(
//                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                                    new String[]{MediaStore.Audio.AlbumColumns.ALBUM_ART},
//                                    MediaStore.Audio.Media._ID + " =?",
//                                    new String[]{String.valueOf(albumId)},
//                                    null);
//                            if (artCursor != null) {
//                                if (artCursor.moveToNext()) {
//                                    if (artCursor.getString(0) != null) {
//                                        albumArt = "file://" + artCursor.getString(0);
//                                    }
//
//                                } else {
//                                    albumArt = null;
//                                }
//                            }
//                        } finally {
//                            // this gets called even if there is an exception somewhere above
//                            if (artCursor != null)
//                                artCursor.close();
//                        }
//                        song.setAlbum_art_path(albumArt);
//                        Log.e("TAG", "Hien " + albumArt);
                        songsList.add(song);
                        //addSongToList(new File(fullpath), genres);

//                        String albumname = cursor.getString(cursor
//                                .getColumnIndex(MediaStore.Audio.Media.ALBUM));

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
        return songsList;
    }


    public static boolean isSdPresent() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    public ArrayList<Artist> getArtistSong(ArrayList<Song> mlist) {
        ArrayList<Artist> mlistArtist = new ArrayList<>();
        boolean check = false;

        for (Song song : mlist) {
            Artist artist = new Artist();
            artist.setName_artist((song.getArtist() == null || song.getArtist().equalsIgnoreCase("")) ? "Unknow" : song.getArtist());
            check = false;
            for (int i = 0; i < mlistArtist.size(); i++) {
                if (artist.getName_artist().equalsIgnoreCase(mlistArtist.get(i).getName_artist())) {
                    check = true;
                }
            }
            if (check == false) {
                mlistArtist.add(artist);
            }
        }
        for (Artist artist : mlistArtist) {
            for (Song song : mlist) {
                if (song.getArtist() != null && artist.getName_artist() != null && song.getArtist().equalsIgnoreCase(artist.getName_artist())) {
                    artist.getMlist_Song().add(song);
                }
            }
        }
        return mlistArtist;
    }

    public ArrayList<Album> getAlbumSong(ArrayList<Song> mlist) {
        ArrayList<Album> mlistAlbum = new ArrayList<>();
        boolean check = false;
        for (Song song : mlist) {
            Album album = new Album();
            album.setAlbum_name(song.getAlbum());
            Log.e("song.getAlbum()", " " + song.getAlbum());
            check = false;
            for (int i = 0; i < mlistAlbum.size(); i++) {
                if (album.getAlbum_name().equalsIgnoreCase(mlistAlbum.get(i).getAlbum_name())) {
                    check = true;
                }
            }
            if (check == false) {
                mlistAlbum.add(album);
            }
        }
        for (Album album : mlistAlbum) {
            for (Song song : mlist) {
                if (song.getAlbum() != null && song.getAlbum().equalsIgnoreCase(album.getAlbum_name())) {
                    album.getMlistSong().add(song);
                }
            }
        }
        return mlistAlbum;

    }

    public static HashMap<String, Uri> mMediaStoreAlbumArtMap = new HashMap<String, Uri>();

    /**
     * Builds a HashMap of all albums and their album art path.
     */
    public static void buildMediaStoreAlbumArtHash(Context context) {
        Cursor albumsCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.ALBUM_ID},
                MediaStore.Audio.Media.IS_MUSIC + "=1",
                null,
                null);

        final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        if (albumsCursor == null)
            return;

        for (int i = 0; i < albumsCursor.getCount(); i++) {
            albumsCursor.moveToPosition(i);
            Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumsCursor.getLong(0));
            mMediaStoreAlbumArtMap.put(albumsCursor.getString(0), albumArtUri);
        }

        albumsCursor.close();
    }

    /**
     * Returns a Uri of a specific genre in MediaStore.
     * The genre is specified using the genreId parameter.
     */
    private static Uri makeGenreUri(String genreId) {
        String CONTENTDIR = MediaStore.Audio.Genres.Members.CONTENT_DIRECTORY;
        return Uri.parse(new StringBuilder().append(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI.toString())
                .append("/")
                .append(genreId)
                .append("/")
                .append(CONTENTDIR)
                .toString());
    }

    public static HashMap<String, String> mGenresHashMap = new HashMap<String, String>();

    /**
     * Builds a HashMap of all songs and their genres.
     */
    public static ArrayList<Genres> buildGenresLibrary(Context mContext) {
        ArrayList<Genres> mGenreList = new ArrayList<>();
        //Get a cursor of all genres in MediaStore.
        Cursor genresCursor = mContext.getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME},
                null,
                null,
                null);

        //Iterate thru all genres in MediaStore.
        for (genresCursor.moveToFirst(); !genresCursor.isAfterLast(); genresCursor.moveToNext()) {
            String genreId = genresCursor.getString(0);
            String genreName = genresCursor.getString(1);
            Genres genre = new Genres();
            if (genreName == null || genreName.isEmpty() ||
                    genreName.equals(" ") || genreName.equals("   ") ||
                    genreName.equals("    "))
                genreName = "<unknown>";
            genre.setGenres_name(genreName);
            mGenreList.add(genre);
            /* Grab a cursor of songs in the each genre id. Limit the songs to
             * the user defined folders using mMediaStoreSelection.
        	 */
            Cursor cursor = mContext.getContentResolver().query(makeGenreUri(genreId),
                    new String[]{MediaStore.Audio.Media.DATA},
                    null,
                    null,
                    null);

            //Add the songs' file paths and their genre names to the hash.
            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    mGenresHashMap.put(cursor.getString(0), genreName);
//                    mGenresSongCountHashMap.put(genreName, cursor.getCount());
                }
                cursor.close();
            }
        }

        if (genresCursor != null)
            genresCursor.close();
        return mGenreList;
    }

    public static String getAlbumArt(Context context, long albumId) {
        Cursor artCursor = null;
        String albumArt = null;
        try {
            artCursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.AlbumColumns.ALBUM_ART},
                    MediaStore.Audio.Media._ID + " =?",
                    new String[]{String.valueOf(albumId)},
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
            // this gets called even if there is an exception somewhere above
            if (artCursor != null)
                artCursor.close();
        }
        return albumArt;
    }

}
