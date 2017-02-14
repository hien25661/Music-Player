package media.musicplayer.songs.mp3player.audio.new_adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;
import media.musicplayer.songs.mp3player.audio.utils.TimerUtil;

public class New_Song_Adapter extends RecyclerView.Adapter<New_Song_Adapter.MyViewHolder> {

    ArrayList<Song> mlist;
    boolean isChoiceMode;
    Context mcontext;
    private OnItemClickListener clickListener;
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_songname;
        TextView tv_subname;
        TextView tv_albumname;
        TextView tv_stt;
        TextView tv_duration;
        ImageView imv_thumbnail;
        LinearLayout linerContent;
        LinearLayout linerOption;

        public MyViewHolder(View view) {
            super(view);
            tv_songname = (TextView) view.findViewById(R.id.tv_name);
            tv_subname = (TextView) view.findViewById(R.id.tv_subname);
            tv_albumname = (TextView) view.findViewById(R.id.tv_albumname);
            imv_thumbnail = (ImageView) view.findViewById(R.id.imv_thumbnail);
            tv_stt = (TextView) view.findViewById(R.id.tv_stt);
            tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            linerContent = (LinearLayout) view.findViewById(R.id.liner_content);
            linerOption = (LinearLayout) view.findViewById(R.id.liner_option);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }


    public New_Song_Adapter(ArrayList<Song> songsList, Context mConText) {
        this.mlist = songsList;
        isChoiceMode = false;
        this.mcontext = mConText;
    }

    public boolean isChoiceMode() {
        return isChoiceMode;
    }

    public void setChoiceMode(boolean choiceMode) {
        isChoiceMode = choiceMode;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Song item = mlist.get(position);

        if (item.isSelect()) {
            holder.itemView.setBackgroundColor(Color.argb(80, 0, 0, 180));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.tv_songname.setText(item.getSongName());
        holder.tv_subname.setText((item.getArtist() == null || item.getArtist().equalsIgnoreCase("")) ? mcontext.getString(R.string.Unknown) : item.getArtist());
        holder.tv_albumname.setText(item.getAlbum());
        if(item.getAlbum_art_uri()!=null){
            Log.e("KKK", "Vao day");
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
                if (onShowOption != null) {
                    onShowOption.clickShowOption(0, position);
                }
            }
        });
        holder.linerContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShowOption != null) {
                    onShowOption.clickShowOption(1, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
    private onShowOption onShowOption;
    public interface onShowOption{
        public void clickShowOption(int view,int pos);
    }
    public void setOnShowOption(onShowOption onShowOption){
        this.onShowOption = onShowOption;
    }
}
