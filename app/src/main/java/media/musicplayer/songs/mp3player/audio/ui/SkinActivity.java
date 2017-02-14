package media.musicplayer.songs.mp3player.audio.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import api.ChooserType;
import api.ChosenImage;
import api.ImageChooserListener;
import api.ImageChooserManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.adapter.SkinAdapter;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.model.CloseApp;
import media.musicplayer.songs.mp3player.audio.model.SingleBackground;
import media.musicplayer.songs.mp3player.audio.player.MusicService;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.RealPathUtil;

public class SkinActivity extends AppCompatActivity implements ImageChooserListener {
    @Bind(R.id.skinGrid)
    GridView gridView;
    SkinAdapter skinAdapter;
    List<Integer> mListUrl;
    @Bind(R.id.imvRoot)
    ImageView imvRoot;
    @Bind(R.id.rlRoot)
    RelativeLayout rlRoot;
    SharedPreferences sharedPreferences;
    boolean isInit;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (!isInit) {
            updateSizeInfo();
            isInit = true;
        }
    }

    public static int heightRl;

    public void updateSizeInfo() {
        heightRl = rlRoot.getHeight();
        Log.e("heightRl", " " + heightRl);
        handle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        ButterKnife.bind(this);
        Fabric.with(this,new Crashlytics());
        settingBackground();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.skin_setting);
    }

    public void settingBackground() {
        sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE);
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(this, imvRoot, type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.skin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_settings:
                openLibrary();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openLibrary() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 123);
        chooseImage();
    }

    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String mediaPath;

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, false);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void closeApp(CloseApp event) {
        try {
            stopService(new Intent(this, MusicService.class));
        } catch (Exception ex) {

        }
        Intent mIntent = new Intent(this, MusicPlayerActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.putExtra("EXIT", true);
        startActivity(mIntent);
        finish();
    }

    public void handle() {
        skinAdapter = new SkinAdapter(this);
        mListUrl = new ArrayList<>();
        mListUrl.add(R.drawable.b1);
        mListUrl.add(R.drawable.b2);
        mListUrl.add(R.drawable.b3);
        mListUrl.add(R.drawable.b4);
        mListUrl.add(R.drawable.b5);
        mListUrl.add(R.drawable.b6);
        mListUrl.add(R.drawable.b7);
        mListUrl.add(R.drawable.b8);
        mListUrl.add(R.drawable.b9);
        mListUrl.add(R.drawable.b10);
        skinAdapter.setmListUrl(mListUrl);
        gridView.setAdapter(skinAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mListUrl.size() - 1) {
                    openLibrary();
                } else {
                    sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE);
                    sharedPreferences.edit().putInt(Constants.ID_BG, position).commit();
                    settingBackground();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("Skin Screen");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            Uri selectedImageURI = data.getData();
//            String realPath = "";
//            // SDK < API11
//            if (Build.VERSION.SDK_INT < 11) {
//                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
//            }
//            // SDK >= 11 && SDK < 19
//            else if (Build.VERSION.SDK_INT < 19) {
//                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
//
//                // SDK > 19 (Android 4.4)
//            } else {
//                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
//            }
//
//            Log.e("onActivityResult: ", " " + realPath);
//            sharedPreferences.edit().putString(Constants.URL_BG, realPath).commit();
//            sharedPreferences.edit().putInt(Constants.ID_BG, 99).commit();
//            finish();
//        }
        if (resultCode == RESULT_OK) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    // Should be called if for some reason the ImageChooserManager is null (Due
    // to destroying of activity for low memory situations)
    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, false);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(mediaPath);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.e("onImageChosen", "onImageChosen");
                if (image != null) {
                    String picturePath = image.getFilePathOriginal();
                    sharedPreferences.edit().putString(Constants.URL_BG, picturePath).commit();
                    sharedPreferences.edit().putInt(Constants.ID_BG, 99).commit();
                    finish();
                }
            }
        });
    }

    @Override
    public void onError(String reason) {
        Toast.makeText(SkinActivity.this, "Image error. Please choose another !!!", Toast.LENGTH_SHORT).show();
    }
}
