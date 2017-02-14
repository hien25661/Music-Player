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
import media.musicplayer.songs.mp3player.audio.model.Artist;

/**
 * Created by hiennguyen on 4/3/16.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {
    Context mcontext;
    int layoutResourceId;
    ArrayList<Artist> mlist;


    public ArtistAdapter(Context context, int textViewResourceId, ArrayList<Artist> objects) {
        super(context, textViewResourceId, objects);
        this.mcontext=context;
        this.layoutResourceId = textViewResourceId;
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
            holder.tv_artist_name = (TextView) row
                    .findViewById(R.id.tv_artistname);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Artist item = mlist.get(position);

        holder.tv_artist_name.setText(item.getName_artist());

        return row;
    }
    static class ViewHolder {
        TextView tv_artist_name;
        TextView tv_subname;

    }
}
