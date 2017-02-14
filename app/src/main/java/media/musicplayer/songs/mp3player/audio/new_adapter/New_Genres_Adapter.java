package media.musicplayer.songs.mp3player.audio.new_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.Artist;
import media.musicplayer.songs.mp3player.audio.model.Genres;
import media.musicplayer.songs.mp3player.audio.new_adapter.new_inteface.OnItemClickListener;

public class New_Genres_Adapter extends RecyclerView.Adapter<New_Genres_Adapter.MyViewHolder> {

    Context mcontext;
    int layoutResourceId;
    ArrayList<Genres> mlist;
    private OnItemClickListener clickListener;
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_genres_name;
        TextView tv_subname;

        public MyViewHolder(View view) {
            super(view);
            tv_genres_name = (TextView) view
                    .findViewById(R.id.tv_genresname);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }


    public New_Genres_Adapter(ArrayList<Genres> mlist, Context mConText) {
        this.mlist = mlist;
        this.mcontext = mConText;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genres, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Genres item = mlist.get(position);

        holder.tv_genres_name.setText(item.getGenres_name());
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
