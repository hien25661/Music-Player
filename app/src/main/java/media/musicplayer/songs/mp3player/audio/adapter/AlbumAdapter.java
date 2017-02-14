package media.musicplayer.songs.mp3player.audio.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Album;

/**
 * Created by hiennguyen on 4/3/16.
 */
public class AlbumAdapter extends ArrayAdapter<Album> {
    Context mcontext;
    int layoutResourceId;
    ArrayList<Album> mlist;


    public AlbumAdapter(Context context, int textViewResourceId, ArrayList<Album> objects) {
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
            holder.tv_albumname = (TextView) row
                    .findViewById(R.id.tv_albumname);
            holder.tv_subname = (TextView) row.findViewById(R.id.tv_subname);
            holder.album_art = (ImageView) row.findViewById(R.id.album_art);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Album item = mlist.get(position);

        holder.tv_albumname.setText(item.getAlbum_name());
        if (item.getAlbum_art_uri() != null) {
            Glide.with(mcontext).load(item.getAlbum_art_uri()).error(R.drawable.music_album).into(holder.album_art);
        } else {
            Glide.with(mcontext).load(R.drawable.music_album).error(R.drawable.music_album).into(holder.album_art);
        }

        return row;
    }

    static class ViewHolder {
        ImageView album_art;
        TextView tv_albumname;
        TextView tv_subname;

    }
}
