package media.musicplayer.songs.mp3player.audio.utils;

import android.app.Activity;
import android.widget.Toast;

import media.musicplayer.songs.mp3player.audio.R;

/**
 * Created by Duc-Nguyen on 4/12/2016.
 */
public class Utils {
    public static void showSuccesfulDialog(Activity homeActivity) {
        // TODO Auto-generated method stub
        //new SweetAlertDialog(homeActivity).setTitleText(homeActivity,
        //		R.string.success).show();
        Toast.makeText(homeActivity,
                homeActivity.getResources().getString(R.string.success),
                Toast.LENGTH_SHORT).show();

    }

    public static void showFailedDialog(Activity homeActivity) {
        // TODO Auto-generated method stub
        //new SweetAlertDialog(homeActivity).setTitleText(homeActivity,
        //		R.string.failed).show();
        Toast.makeText(homeActivity,
                homeActivity.getResources().getString(R.string.failed),
                Toast.LENGTH_SHORT).show();

    }

    public static boolean checkValidate(String newFolderName) {
        if (newFolderName.trim().equals("")) {
            return false;
        }
        return true;
    }
}
