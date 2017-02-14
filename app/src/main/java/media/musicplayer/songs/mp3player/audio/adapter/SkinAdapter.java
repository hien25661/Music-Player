package media.musicplayer.songs.mp3player.audio.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.List;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.ui.SkinActivity;

/**
 * Created by Duc-Nguyen on 4/2/2016.
 */
public class SkinAdapter extends BaseAdapter {
    List<Integer> mListUrl;
    Context context;
    private LayoutInflater mInflater;
    int actionbarheight;

    public SkinAdapter(Context context) {
        this.context = context;
        mListUrl = new ArrayList<>();
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionbarheight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
    }

    public List<Integer> getmListUrl() {
        return mListUrl;
    }

    public void setmListUrl(List<Integer> mListUrl) {
        this.mListUrl = mListUrl;
    }

    public void addItem(int item) {
        mListUrl.add(item);
    }

    @Override
    public int getCount() {
        return mListUrl.size();
    }

    @Override
    public Object getItem(int position) {
        return mListUrl.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        ViewHolder holder = null;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_skingrid, parent, false);
            convertView.setMinimumHeight(SkinActivity.heightRl / 2 );
            holder = new ViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.imvPic);
            convertView.setTag(holder);
            holder.mImageView.setMinimumHeight(SkinActivity.heightRl / 2);
            holder.mImageView.setMaxHeight(SkinActivity.heightRl / 2 );
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //Picasso.with(context).load(mListUrl.get(position)).into(holder.mImageView);
        //demo
//        holder.mImageView.setImageResource(mListUrl.get(position));
        Glide.with(context).load(mListUrl.get(position)).signature(new StringSignature(""+System.currentTimeMillis())).into(holder.mImageView);
        return convertView;
    }

    public static class ViewHolder {
        public ImageView mImageView;
    }
}
