package media.musicplayer.songs.mp3player.audio.new_adapter;

import android.content.Context;
import android.graphics.Color;
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

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.Song;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;
import media.musicplayer.songs.mp3player.audio.utils.TimerUtil;

public class New_Album_Adapter extends RecyclerView.Adapter<New_Album_Adapter.MyViewHolder> {

    Context mcontext;
    int layoutResourceId;
    ArrayList<Album> mlist;
    private OnItemClickListener clickListener;
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView album_art;
        TextView tv_albumname;
        TextView tv_subname;

        public MyViewHolder(View view) {
            super(view);
            tv_albumname = (TextView) view
                    .findViewById(R.id.tv_albumname);
            tv_subname = (TextView) view.findViewById(R.id.tv_subname);
            album_art = (ImageView) view.findViewById(R.id.album_art);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }


    public New_Album_Adapter(ArrayList<Album> mlist, Context mConText) {
        this.mlist = mlist;
        this.mcontext = mConText;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Album item = mlist.get(position);

        holder.tv_albumname.setText(item.getAlbum_name());
        if (item.getAlbum_art_uri() != null) {
            Glide.with(mcontext).load(item.getAlbum_art_uri()).error(R.drawable.music_album).into(holder.album_art);
        } else {
            Glide.with(mcontext).load(R.drawable.music_album).error(R.drawable.music_album).into(holder.album_art);
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
