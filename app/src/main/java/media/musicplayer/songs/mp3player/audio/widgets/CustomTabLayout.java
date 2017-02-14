package media.musicplayer.songs.mp3player.audio.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by SF on 22/04/2016.
 */
public class CustomTabLayout extends TabLayout {
    public CustomTabLayout(Context context) {
        super(context);
        init();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
    Typeface mTypeface;

    private void init() {
        mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/OregonBold.ttf");
    }

    @Override
    public void addTab(Tab tab) {
        super.addTab(tab);
        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());

        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.NORMAL);
            }
        }
    }

}

