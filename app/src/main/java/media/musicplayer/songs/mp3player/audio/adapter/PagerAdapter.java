package media.musicplayer.songs.mp3player.audio.adapter;

/**
 * Created by SF on 01/04/2016.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.ui.AlbumsFragment;
import media.musicplayer.songs.mp3player.audio.ui.ArtistFragment;
import media.musicplayer.songs.mp3player.audio.ui.FolderFragment;
import media.musicplayer.songs.mp3player.audio.ui.GenreFragment;
import media.musicplayer.songs.mp3player.audio.ui.PlaylistFragment;
import media.musicplayer.songs.mp3player.audio.ui.SongsFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<Song> mlist = new ArrayList<>();
    public static final String SONG_FRAGMENT = "SONG_FRAGMENT";
    String tileSong = "";
    //    private Map<Integer, String> mFragmentTags;
//    private FragmentManager mFragmentManager;
    Context context;

    public PagerAdapter(Context context, FragmentManager fm, int NumOfTabs, ArrayList<Song> mlist) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.mlist = mlist;
        this.context = context;
        getStringTitleSong(0);
    }
    public void getStringTitleSong(int numbOfSong){
        tileSong =  context.getResources().getString(R.string.Songs)+" ["+numbOfSong+"]";
    }

    private final int[] TITLES = {R.string.playlist, R.string.Songs, R.string.Album, R.string.Artist, R.string.Genre,R.string.MyFiles};


    @Override
    public CharSequence getPageTitle(int position) {
        String strTitle = "";
        if(position !=1) {
            strTitle =  context.getResources().getString(TITLES[position]);
        }
        else {
            strTitle = tileSong;
        }
        return strTitle;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PlaylistFragment tab1 = new PlaylistFragment(mlist);
                return tab1;
            case 1:
                SongsFragment tab2 = new SongsFragment(mlist);
                return tab2;
            case 2:
                //AlbumsFragment tab3 = new AlbumsFragment(context, mlist);
                AlbumsFragment tab3 = AlbumsFragment.getInstance(context,mlist);
                return tab3;
            case 3:
                ArtistFragment tab4 = new ArtistFragment(mlist);
                return tab4;
            case 4:
                GenreFragment tab5 = new GenreFragment(context, mlist);
                return tab5;
            case 5:
                FolderFragment tab6 = new FolderFragment(mlist);
                return tab6;
            default:
                return null;
        }
    }


    private Fragment mCurrentFragment;

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

}
