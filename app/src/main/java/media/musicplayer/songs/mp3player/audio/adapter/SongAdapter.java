package media.musicplayer.songs.mp3player.audio.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.utils.TimerUtil;

/**
 * Created by hiennguyen on 4/2/16.
 */
public class SongAdapter extends ArrayAdapter<Song> {
    Context mcontext;
    int layoutResourceId;
    ArrayList<Song> mlist;
    boolean isChoiceMode;

    public SongAdapter(Context context, int textViewResourceId, ArrayList<Song> objects) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mcontext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.tv_songname = (TextView) row
                    .findViewById(R.id.tv_name);
            holder.tv_subname = (TextView) row.findViewById(R.id.tv_subname);
            holder.tv_albumname = (TextView) row.findViewById(R.id.tv_albumname);
            holder.imv_thumbnail = (ImageView) row.findViewById(R.id.imv_thumbnail);
            holder.tv_stt = (TextView) row.findViewById(R.id.tv_stt);
            holder.tv_duration = (TextView) row.findViewById(R.id.tv_duration);
            holder.linerContent = (LinearLayout) row.findViewById(R.id.liner_content);
            holder.linerOption = (LinearLayout) row.findViewById(R.id.liner_option);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Song item = mlist.get(position);

        if (item.isSelect()) {
            row.setBackgroundColor(Color.argb(80,0,0,180));
        } else {
            row.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.tv_songname.setText(item.getSongName());
        holder.tv_subname.setText((item.getArtist() == null || item.getArtist().equalsIgnoreCase("")) ? mcontext.getString(R.string.Unknown) : item.getArtist());
        holder.tv_albumname.setText(item.getAlbum());
        if(item.getAlbum_art_uri()!=null){
            Log.e("KKK","Vao day");
            Glide.with(mcontext).load(item.getAlbum_art_uri()).error(R.drawable.disc_mini).crossFade().into(holder.imv_thumbnail);
        }else {

            Glide.with(mcontext).load(R.drawable.disc_mini).crossFade().into(holder.imv_thumbnail);
        }
        holder.tv_stt.setText(""+(position+1));
        String duration = ""+ TimerUtil.milliSecondsToTimer(Long.parseLong(item.getDuration()));
        if(duration.equals("0:00")){
            duration = "";
        }
        holder.tv_duration.setText(duration);
        holder.linerOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onShowOption!=null) {
                    onShowOption.clickShowOption(0, position);
                }
            }
        });
        holder.linerContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onShowOption!=null){
                onShowOption.clickShowOption(1,position);
                }
            }
        });
        return row;
    }

    static class ViewHolder {
        TextView tv_songname;
        TextView tv_subname;
        TextView tv_albumname;
        TextView tv_stt;
        TextView tv_duration;
        ImageView imv_thumbnail;
        LinearLayout linerContent;
        LinearLayout linerOption;

    }
    private onShowOption onShowOption;
    public interface onShowOption{
        public void clickShowOption(int view,int pos);
    }
    public void setOnShowOption(onShowOption onShowOption){
        this.onShowOption = onShowOption;
    }

}
