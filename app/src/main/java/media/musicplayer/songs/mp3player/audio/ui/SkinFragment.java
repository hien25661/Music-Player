package media.musicplayer.songs.mp3player.audio.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.SkinAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SkinFragment extends Fragment {
    @Bind(R.id.skinGrid)
    GridView gridView;
    SkinAdapter skinAdapter;
    List<Integer> mListUrl;
    public SkinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_skin, container, false);
        ButterKnife.bind(this, v);
        handle();
        return v;
    }

    public void handle() {
        skinAdapter = new SkinAdapter(getActivity());
        mListUrl = new ArrayList<>();
        mListUrl.add(R.drawable.b1);
        mListUrl.add(R.drawable.b2);
        mListUrl.add(R.drawable.b3);
        mListUrl.add(R.drawable.b4);
        mListUrl.add(R.drawable.b5);
        mListUrl.add(R.drawable.b6);
        mListUrl.add(R.drawable.b7);
        mListUrl.add(R.drawable.b8);
        mListUrl.add(R.drawable.b9);
        mListUrl.add(R.drawable.b10);
        skinAdapter.setmListUrl(mListUrl);
        gridView.setAdapter(skinAdapter);
    }

}
