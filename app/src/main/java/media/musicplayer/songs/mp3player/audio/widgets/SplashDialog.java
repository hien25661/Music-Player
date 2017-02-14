package media.musicplayer.songs.mp3player.audio.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import media.musicplayer.songs.mp3player.audio.R;

/**
 * Created by Duc-Nguyen on 4/11/2016.
 */
public class SplashDialog extends Dialog {
    public SplashDialog(Context context) {
        super(context);
    }

    public SplashDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public SplashDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.splash_dialog);
//        this.getWindow().setBackgroundDrawable(
//                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }
}
