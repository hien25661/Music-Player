package media.musicplayer.songs.mp3player.audio.adapter;

/**
 * Created by Duc-Nguyen on 4/5/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Playlist;


/**
 * Created by ducnguyen on 4/5/16.
 */
public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    Context mcontext;
    int layoutResourceId;
    ArrayList<Playlist> mlist;


    public PlaylistAdapter(Context context, int textViewResourceId, ArrayList<Playlist> objects) {
        super(context, textViewResourceId, objects);
        this.mcontext = context;
        this.layoutResourceId = textViewResourceId;
        this.mlist = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.tv_artist_name = (TextView) row
                    .findViewById(R.id.tv_artistname);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Playlist item = mlist.get(position);

        holder.tv_artist_name.setText(item.getPlaylistName());

        return row;
    }

    static class ViewHolder {
        TextView tv_artist_name;
        TextView tv_subname;

    }
}

