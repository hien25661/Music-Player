package media.musicplayer.songs.mp3player.audio.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.BlurTransformation;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 * Created by SF on 27/04/2016.
 */
public class SingleBackground {
    public static SingleBackground instance;

    public static SingleBackground getInstance() {
        if (instance == null) {
            instance = new SingleBackground();
        }
        return instance;
    }

    public int getCurrentBackground(int type) {
        int idBackground = R.drawable.b1;
        switch (type) {
            case 0:
                idBackground = R.drawable.b1;
                break;
            case 1:
                idBackground = R.drawable.b2;
                break;
            case 2:
                idBackground = R.drawable.b3;
                break;
            case 3:
                idBackground = R.drawable.b4;
                break;
            case 4:
                idBackground = R.drawable.b5;
                break;
            case 5:
                idBackground = R.drawable.b6;
                break;
            case 6:
                idBackground = R.drawable.b7;
                break;
            case 7:
                idBackground = R.drawable.b8;
                break;
            case 8:
                idBackground = R.drawable.b9;
                break;
            default:
                idBackground = R.drawable.b1;
                break;
        }
        return idBackground;
    }

    public static void settingBackground(Context context, ImageView view, int type) {
        Log.e("settingBackground: ", " " + type);
        if (type == 99) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.MUSIC_PLAYER, context.MODE_PRIVATE);
            String urlBg = sharedPreferences.getString(Constants.URL_BG, null);
            Log.e("urlBg: ", " " + urlBg);

            if (urlBg != null) {
                Glide.with(context).load(urlBg)
                        .error(R.drawable.b1).override(360, 640).into(view);

            } else {
                type = 0;
                Glide.with(context).load(SingleBackground.getInstance().getCurrentBackground(type)).override(360, 640).into(view);
            }
        } else {
            Glide.with(context).load(SingleBackground.getInstance().getCurrentBackground(type)).override(360, 640).into(view);
        }
    }

}
