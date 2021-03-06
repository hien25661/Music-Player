package media.musicplayer.songs.mp3player.audio.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.claucookie.miniequalizerlibrary.EqualizerView;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Song;

/**
 * Created by hiennguyen on 4/2/16.
 */
public class SongAdapter_FullScreen extends ArrayAdapter<Song> {
    Context mcontext;
    int layoutResourceId;
    ArrayList<Song> mlist;
    boolean isChoiceMode;

    public SongAdapter_FullScreen(Context context, int textViewResourceId, ArrayList<Song> objects) {
        super(context, textViewResourceId, objects);
        this.mcontext = context;
        this.layoutResourceId = textViewResourceId;
        this.mlist = objects;
        isChoiceMode = false;
    }

    public boolean isChoiceMode() {
        return isChoiceMode;
    }

    public void setChoiceMode(boolean choiceMode) {
        isChoiceMode = choiceMode;
    }

    @Override
    public Song getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.tv_songname = (TextView) row
                    .findViewById(R.id.tv_name);
            holder.tv_subname = (TextView) row.findViewById(R.id.tv_subname);
            holder.equalizerView = (EqualizerView)row.findViewById(R.id.equalizer_view);
            holder.equalizerView.stopBars();
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Song item = mlist.get(position);

        if (item.isSelect()) {
            row.setBackgroundColor(Color.argb(80,0,0,180));
           // if(item.isPlaying() == true) {
                holder.equalizerView.animateBars();
        } else {
            row.setBackgroundColor(Color.TRANSPARENT);
            if(holder.equalizerView.isAnimating()){
                holder.equalizerView.stopBars();
            }
        }
        holder.tv_songname.setText(item.getSongName());
        holder.tv_subname.setText((item.getArtist() == null || item.getArtist().equalsIgnoreCase("")) ? mcontext.getString(R.string.Unknown) : item.getArtist());

        return row;
    }

    static class ViewHolder {
        TextView tv_songname;
        TextView tv_subname;
        EqualizerView equalizerView;

    }
}
