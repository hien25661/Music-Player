package media.musicplayer.songs.mp3player.audio.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.SearchAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.interfaces.TypeSearch;
import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.Artist;
import media.musicplayer.songs.mp3player.audio.model.FragmentTag;
import media.musicplayer.songs.mp3player.audio.model.ItemSearch;
import media.musicplayer.songs.mp3player.audio.model.SearchModel;
import media.musicplayer.songs.mp3player.audio.model.SingleBackground;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.model.TransitPlaySong;
import media.musicplayer.songs.mp3player.audio.model.TransitSongListEvent;
import media.musicplayer.songs.mp3player.audio.utils.Constants;

/**
 *
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener {
    ArrayList<ItemSearch> mlistSearch = new ArrayList<>();
    ListView lv_search;
    Context mContext;
    SearchAdapter searchAdapter;
    EditText editTextSearch;
    SharedPreferences sharedPreferences;
    ImageView imvRoot;

    public SearchFragment(ArrayList<ItemSearch> mList) {
        this.mlistSearch = mList;
    }

    public void settingBackground(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences(Constants.MUSIC_PLAYER, mContext.MODE_PRIVATE);
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(mContext, imvRoot, type);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mContext = view.getContext();
        imvRoot = (ImageView) view.findViewById(R.id.imvRoot);
        try {
            settingBackground(mContext);
        } catch (Exception e) {

        }
        ((MusicPlayerActivity) getActivity()).setTitle(R.string.search_queue_title);
        lv_search = (ListView) view.findViewById(R.id.listview_search);
        editTextSearch = (EditText) view.findViewById(R.id.edt_search);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EventBus.getDefault().post(new SearchModel(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lv_search.setOnItemClickListener(this);
        setSearchAdapter(mlistSearch);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemSearch itemSearch = (ItemSearch) parent.getItemAtPosition(position);
        switch (itemSearch.getTYPE()) {
            case SECTIONSONG:
            case SECTIONARTIST:
            case SECTIONALBUM:
                break;
            case SONG:
                Song song = itemSearch.getSong();
                ArrayList<Song> SongList = new ArrayList<>();
                SongList.add(song);
                EventBus.getDefault().post(new TransitPlaySong(SongList, 0, false));
                break;
            case ARTIST:
                Artist artist = itemSearch.getArtist();
                EventBus.getDefault().post(new TransitSongListEvent(artist.getMlist_Song(), artist.getName_artist(), FragmentTag.ARTIST));
                break;
            case ALBUM:
                Album album = itemSearch.getAlbum();
                EventBus.getDefault().post(new TransitSongListEvent(album.getMlistSong(), album.getAlbum_name(), FragmentTag.ALBUM));
                break;
            default:
                break;
        }
    }

    public void setSearchAdapter(ArrayList<ItemSearch> mlistSearch) {
        this.mlistSearch = mlistSearch;
        searchAdapter = new SearchAdapter(mContext);
        searchAdapter.setMlist(mlistSearch);
        lv_search.setAdapter(searchAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Search Screen");
    }
}
