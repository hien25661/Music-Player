package media.musicplayer.songs.mp3player.audio.utils;

/**
 * Created by hiennguyen on 4/16/16.
 */
public class Constants {
    public static final String INTENTFILTER_TIMER = "media.musicplayer.songs.mp3player.audio.action.updatetimer";
    public static final String HOCUS_FOCUS_TIMER = "hokusFokusTimer";
    public static final String HOCUS_FOCUS_TIMER_MILLI_SECOND = "hokusFokusTimerMilliSecond";
    public interface ACTION {
        public final static String MAIN_ACTION = "media.musicplayer.songs.mp3player.audio.action.main";
        public final static String PREV_ACTION = "media.musicplayer.songs.mp3player.audio.action.prev";
        public final static String PLAY_ACTION = "media.musicplayer.songs.mp3player.audio.action.play";
        public final static String NEXT_ACTION = "media.musicplayer.songs.mp3player.audio.action.next";
        public final static String CLOSE_ACTION = "media.musicplayer.songs.mp3player.audio.action.close";
        public final static String STARTFOREGROUND_ACTION = "media.musicplayer.songs.mp3player.audio.action.startforeground";
        public final static String STOPFOREGROUND_ACTION = "media.musicplayer.songs.mp3player.audio.action.stopforeground";
        public final static String UPDATE_SONG_ACTION = "media.musicplayer.songs.mp3player.audio.action.updatesong";

    }
    public final static String ID_BG = "ID_BG";
    public final static String URL_BG = "URL_BG";
    public final static String MUSIC_PLAYER = "musicplayer";
    public final static String LAST_PLAYED = "LAST_PLAYED";
    public final static String FAVORITE = "FAVORITE";
    public final static String HOURSET= "hourset";
    public final static String MINUTESET= "minuteset";
    public interface NOTIFICATION_ID {
        public final static int FOREGROUND_SERVICE = 101;
    }
    public final static String EQ_ENABLE = "EQ_ENABLE";
    public static final String ENABLE_PLUGIN = "enalble_plugin";
    public static final String ENABLE_SHAKE = "enalble_shake";
    public static final String ENABLE_LOCK = "enalble_lock";
}
