package media.musicplayer.songs.mp3player.audio.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Item;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.utils.TimerUtil;

/**
 * Created by Hien on 7/2/2016.
 */
public class FolderAdapter extends BaseAdapter {
    Context mcontext;
    Item[] mList;
    ArrayList<Song> mListSongs = new ArrayList<>();

    public FolderAdapter(Context context, Item[] objects, ArrayList<Song> mListSong) {
        this.mcontext = context;
        this.mList = objects;
        this.mListSongs = mListSong;
    }


    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public Object getItem(int position) {
        return mList[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
            row = inflater.inflate(R.layout.item_folder, parent, false);
            holder = new ViewHolder();
            holder.tv_songname = (TextView) row
                    .findViewById(R.id.tv_name);
            holder.tv_subname = (TextView) row.findViewById(R.id.tv_subname);
            holder.album_art = (ImageView) row.findViewById(R.id.imv_thumbnail);
            holder.tv_numSongs = (TextView) row.findViewById(R.id.tv_numb);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Item item = mList[position];


        if (item.mFile != null) {
            int index = getPositionInSongList(item.mFile);
            if (index < 0) {
                holder.tv_songname.setText(item.file);
                holder.tv_subname.setVisibility(View.GONE);
                holder.tv_numSongs.setVisibility(View.VISIBLE);
                holder.tv_numSongs.setText("" + item.numbofFiles + (item.numbofFiles > 1 ? " " + mcontext.getString(R.string.Songs).toLowerCase() : " " + mcontext.getString(R.string.Songs).toLowerCase()));
            } else {

                Song song = mListSongs.get(index);
                holder.tv_songname.setText(song.getSongName());
                holder.tv_subname.setVisibility(View.VISIBLE);
                holder.tv_subname.setText(song.getArtist());
                holder.tv_numSongs.setVisibility(View.VISIBLE);
                String duration = "" + TimerUtil.milliSecondsToTimer(Long.parseLong(song.getDuration()));
                if (duration.equals("0:00")) {
                    duration = "";
                }
                holder.tv_numSongs.setText(duration);
                Glide.with(mcontext).load(song.getAlbum_art_uri()).error(R.drawable.disc_mini).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).crossFade().into(holder.album_art);
            }
        } else {
            holder.tv_songname.setText(item.file);
            holder.tv_subname.setVisibility(View.GONE);
            holder.tv_numSongs.setVisibility(View.GONE);
        }
        holder.album_art.setImageResource(item.icon);
        return row;
    }

    static class ViewHolder {
        ImageView album_art;
        TextView tv_songname;
        TextView tv_subname;
        TextView tv_numSongs;
    }

    private int getPositionInSongList(File f) {
        int index = -1;
        if (f != null && f.exists()) {
            for (int i = 0; i < mListSongs.size(); i++) {
                if (mListSongs.get(i).getPath().equals(f.getAbsolutePath())) {
                    return i;
                }
            }
        }
        return index;
    }

    private int getFile(String dirPath) {
        File f = new File(dirPath);
        File[] files = f.listFiles();
        int numberFile = 0;

        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    numberFile += getFile(file.getAbsolutePath());
                } else if (checkExistInSongList(file)) {
                    numberFile++;
                }
            }

        return numberFile;
    }

    private boolean checkExistInSongList(File file) {
        String path = file.getAbsolutePath();
        for (Song song : mListSongs) {
            if (song.getPath().equals(file.getAbsolutePath()))
                return true;
        }
        return false;
    }
}
