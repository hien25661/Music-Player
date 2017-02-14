package media.musicplayer.songs.mp3player.audio.interfaces;

/**
 * Created by Hien on 6/18/2016.
 */
public interface AccelerometerListener {
    public void onAccelerationChanged(float x, float y, float z);

    public void onShake(float force, boolean isNext);
}
