package media.musicplayer.songs.mp3player.audio.new_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Album;
import media.musicplayer.songs.mp3player.audio.model.Artist;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;

public class New_Artist_Adapter extends RecyclerView.Adapter<New_Artist_Adapter.MyViewHolder> {

    Context mcontext;
    int layoutResourceId;
    ArrayList<Artist> mlist;
    private OnItemClickListener clickListener;
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_artist_name;
        TextView tv_subname;

        public MyViewHolder(View view) {
            super(view);
            tv_artist_name = (TextView) view
                    .findViewById(R.id.tv_artistname);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }


    public New_Artist_Adapter(ArrayList<Artist> mlist, Context mConText) {
        this.mlist = mlist;
        this.mcontext = mConText;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Artist item = mlist.get(position);

        holder.tv_artist_name.setText(item.getName_artist());
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
