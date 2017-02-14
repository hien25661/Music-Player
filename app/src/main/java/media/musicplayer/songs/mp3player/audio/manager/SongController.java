package media.musicplayer.songs.mp3player.audio.manager;

import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

import media.musicplayer.songs.mp3player.audio.model.Song;

/**
 * Created by SF on 14/04/2016.
 */
public class SongController {
    Context mContext;

    public SongController(Context mContext) {
        this.mContext = mContext;
    }

    public boolean setRingTone(Song song) {
        if (song.getPath() != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, song.getPath());
            values.put(MediaStore.MediaColumns.TITLE, song.getSongName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg");
            values.put(MediaStore.Audio.Media.ARTIST, (song.getArtist() != null) ? song.getArtist() : "");
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(song.getPath());
            mContext.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + song.getPath() + "\"", null);
            Uri newUri = mContext.getContentResolver().insert(uri, values);
            try {
                RingtoneManager.setActualDefaultRingtoneUri(mContext,
                        RingtoneManager.TYPE_RINGTONE, newUri);
                return true;
            } catch (Exception e) {
                return false;
            }

        } else {
            return false;
        }
    }
}
