package media.musicplayer.songs.mp3player.audio.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.SortPlain;

/**
 * Created by SF on 04/04/2016.
 */
public class SortDialog extends Dialog {
    @Bind(R.id.rbGroup)
    public RadioGroup radioGroup;
    @Bind(R.id.rbTitle)
    public RadioButton rbTitle;
    @Bind(R.id.rbArtist)
    public RadioButton rbArtist;
    @Bind(R.id.rbAlbum)
    public RadioButton rbAlbum;
    @Bind(R.id.cbSort)
    public CheckBox cbSort;
    @Bind(R.id.btnSort)
    public Button btnSort;
    @Bind(R.id.btnCancel)
    public Button btnCancel;
    Context mContext;

    public SortDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    int currentRb;
    boolean isCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.sort_dialog);
        ButterKnife.bind(this);
        this.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbTitle:
                        currentRb = 0;
                        break;
                    case R.id.rbArtist:
                        currentRb = 1;
                        break;
                    case R.id.rbAlbum:
                        currentRb = 2;
                        break;
                    case R.id.rbDuration:
                        currentRb = 3;
                        break;
                    case R.id.rbDateAdd:
                        currentRb = 4;
                        break;
//                    case R.id.rbYear:
//                        currentRb = 5;
//                        break;
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SortPlain(currentRb, isCheck));
                dismiss();
            }
        });
        cbSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck = isChecked;
            }
        });

    }

//    @OnClick(R.id.btnCancel)
//    public void dismissDialog() {
//        dismiss();
//        return;
//    }
//
//    @OnClick(R.id.btnSort)
//    public void sort() {
//        EventBus.getDefault().post(new SortPlain(currentRb, isCheck));
//        dismiss();
//        return;
//    }
}
