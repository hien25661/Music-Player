package media.musicplayer.songs.mp3player.audio.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.ItemSearch;

/**
 * Created by hiennguyen on 4/5/16.
 */
public class SearchAdapter extends BaseAdapter {
    private ArrayList<ItemSearch> mlist;

    Context mContext;

    public SearchAdapter(Context mContext) {
        this.mContext = mContext;
        mlist = new ArrayList<>();
    }


    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<ItemSearch> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<ItemSearch> mlist) {
        this.mlist = mlist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ViewHolder holder;
        ItemSearch itemSearch = mlist.get(position);
        //if (row == null) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        holder = new ViewHolder();
        int resourceID = R.layout.item_section;
        switch (itemSearch.getTYPE()) {
            case SECTIONSONG:
            case SECTIONALBUM:
            case SECTIONARTIST:
                resourceID = R.layout.item_section;
                row = inflater.inflate(resourceID, parent, false);
                holder.tv_section = (TextView) row
                        .findViewById(R.id.tv_section);
                break;
            case SONG:
            case ARTIST:
                resourceID = R.layout.item_artist;
                row = inflater.inflate(resourceID, parent, false);
                holder.tv_section = (TextView) row
                        .findViewById(R.id.tv_artistname);
                break;
            case ALBUM:
                resourceID = R.layout.item_album;
                row = inflater.inflate(resourceID, parent, false);
                holder.tv_section = (TextView) row
                        .findViewById(R.id.tv_albumname);
                break;
            default:
                resourceID = R.layout.item_artist;
                break;
        }


//            row.setTag(holder);
//        } else {
//            holder = (ViewHolder) row.getTag();
//        }
        row.setBackgroundColor(Color.TRANSPARENT);
        switch (itemSearch.getTYPE()) {
            case SECTIONSONG:
                holder.tv_section.setText(R.string.Songs);
                row.setBackgroundColor(Color.GRAY);
                break;
            case SECTIONALBUM:
                row.setBackgroundColor(Color.GRAY);
                holder.tv_section.setText(R.string.Album);
                break;
            case SECTIONARTIST:
                row.setBackgroundColor(Color.GRAY);
                holder.tv_section.setText(R.string.Artists);
                break;
            case SONG:
                holder.tv_section.setText(itemSearch.getSong().getSongName());
                break;
            case ARTIST:
                holder.tv_section.setText(itemSearch.getArtist().getName_artist());
                break;
            case ALBUM:
                holder.tv_section.setText(itemSearch.getAlbum().getAlbum_name());
                break;
            default:
                break;
        }
        return row;
    }

    static class ViewHolder {
        TextView tv_section;
    }
}
