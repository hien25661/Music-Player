package media.musicplayer.songs.mp3player.audio.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Song;

/**
 * Created by hiennguyen on 4/2/16.
 */
public class PlaylistSongAdapter extends ArrayAdapter<Song> {
    Context mcontext;
    int layoutResourceId;
    ArrayList<Song> mlist;


    public PlaylistSongAdapter(Context context, int textViewResourceId, ArrayList<Song> objects) {
        super(context, textViewResourceId, objects);
        this.mcontext=context;
        this.layoutResourceId =textViewResourceId;
        this.mlist=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)mcontext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.tv_songname = (TextView) row
                    .findViewById(R.id.tv_name);
            holder.tv_subname = (TextView) row.findViewById(R.id.tv_subname);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Song item = mlist.get(position);

        holder.tv_songname.setText(item.getSongName());
        holder.tv_subname.setText((item.getArtist() == null || item.getArtist().equalsIgnoreCase(""))? mcontext.getString(R.string.Unknown) : item.getArtist());

        return row;
    }
    static class ViewHolder {
        TextView tv_songname;
        TextView tv_subname;

    }
}
