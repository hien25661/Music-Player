package media.musicplayer.songs.mp3player.audio.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.database.SQLiteDataController;
import media.musicplayer.songs.mp3player.audio.model.CloseApp;
import media.musicplayer.songs.mp3player.audio.model.EqualizerInfo;
import media.musicplayer.songs.mp3player.audio.model.SingleBackground;
import media.musicplayer.songs.mp3player.audio.model.UpdateEQ;
import media.musicplayer.songs.mp3player.audio.player.MusicService;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.LocaleHelper;
import media.musicplayer.songs.mp3player.audio.widgets.EqualizerView;
import media.musicplayer.songs.mp3player.audio.widgets.RoundKnobButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EqualizerActivity extends AppCompatActivity {
    //    @Bind(R.id.hgBass)
//    HGDial hgBass;
//    @Bind(R.id.hgVolume)
//    HGDial hgVolume;
//    @Bind(R.id.hgVirtual)
//    HGDial hgVirtual;
    @Bind(R.id.equalizerView)
    EqualizerView equalizerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.hgBassContainer)
    RelativeLayout hgBassContainer;
    @Bind(R.id.hgVirtualContainer)
    RelativeLayout hgVirtualContainer;
    @Bind(R.id.hgVolumeContainer)
    RelativeLayout hgVolumeContainer;
    ImageView onoff_imv;
    RoundKnobButton bassBtn, volumeBtn, virtualBtn;
    int bassBoostLevel, virtualLevel;
    int sizeBtn = 350;
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();
    EqualizerInfo currentEqualizerInfo = new EqualizerInfo();
    public final static String EQ_PREF = "equalizer";
    public final static String EQ_ENABLE = "eqenable";
    boolean isEnableEQ;
    ActionBar actionBar;
    TextView title;
    boolean isOn;
    @Bind(R.id.spinner)
    Spinner spinner;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    ArrayList<EqualizerInfo> infoArrayList = new ArrayList<>();
    ArrayList<String> listEqualizerString = new ArrayList<>();

    public boolean isOn() {
        return isOn;
    }

    ArrayAdapter<String> dataAdapter;

    public void setOn(boolean on) {
        isOn = on;
    }

    @Bind(R.id.imvRoot)
    ImageView imvRoot;
    private AudioManager mAudioManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        //hgBass.registerCallback(this);
//        hgVolume.registerCallback(this);
//        hgVirtual.registerCallback(this);
        settingBackground();
        sizeBtn = getResources().getInteger(R.integer.size_btn);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.Equalizer);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_actionbar_equalizer);
        onoff_imv = (ImageView) findViewById(R.id.onoff_imv);
        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.Equalizer);


        sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        String equalizer = sharedPreferences.getString(EQ_PREF, "");
        isEnableEQ = sharedPreferences.getBoolean(EQ_ENABLE, true);
        if (!equalizer.equals("")) {
            currentEqualizerInfo = gson.fromJson(equalizer, EqualizerInfo.class);
            Log.e("currentEqualiz ban1:", " " + currentEqualizerInfo.getBand1Value());
            equalizerView.setEqualizerInfo(currentEqualizerInfo);
        } else {
            equalizerView.setEqualizerInfo(currentEqualizerInfo);
        }
        onoff_imv.setSelected(isEnableEQ);
        // on/off equalizer
        onoff_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onoff_imv.setSelected(!onoff_imv.isSelected());
                isOn = onoff_imv.isSelected();
                sharedPreferences.edit().putBoolean(EQ_ENABLE, onoff_imv.isSelected()).commit();

                if (MusicPlayerActivity.musicSrv.getEqualizerHelper() != null) {
                    try {
                        MusicPlayerActivity.musicSrv.getEqualizerHelper().releaseEQObjects();
                        MusicPlayerActivity.musicSrv.initAudioFX();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        bassBtn = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff,
                sizeBtn, sizeBtn);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        hgBassContainer.addView(bassBtn, lp);
        bassBtn.setRotorPercentage(currentEqualizerInfo.getBassValue() / 10);

        bassBtn.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
            }

            public void onRotate(final int percentage) {
                //Toast.makeText(EqualizerActivity.this, "New state:" + percentage, Toast.LENGTH_SHORT).show();
                bassBoostLevel = percentage * 10;
                bassBtn.setCurrentPercent(bassBoostLevel);
                if (MusicPlayerActivity.musicSrv.getEqualizerHelper() != null)
                    MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentBassBoost().setStrength((short) bassBoostLevel);
                currentEqualizerInfo.setBassValue(bassBoostLevel);
//                bassBoostLevel = (short) bassPercent;
                updateEQinSharePref();
            }
        });

        volumeBtn = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff,
                sizeBtn, sizeBtn);
        hgVolumeContainer.addView(volumeBtn, lp);
        this.mAudioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        final int maxVolume = MusicPlayerActivity.musicSrv.getmAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        final int currentVolume = MusicPlayerActivity.musicSrv.getmAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
        final int maxVolume = mAudioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        final int currentVolume = mAudioManger.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.e("maxVolume ", " " + maxVolume + "  " + currentVolume);
        volumeBtn.setRotorPercentage(Math.round(currentVolume * 100f / maxVolume));
        volumeBtn.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
            }

            public void onRotate(final int percentage) {
                int volumeCurrent = Math.round(maxVolume * percentage / 100f);
                volumeBtn.setCurrentPercent(volumeCurrent);

                MusicPlayerActivity.musicSrv.getmAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC,
                        volumeCurrent, 0);


            }
        });


        virtualBtn = new RoundKnobButton(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff,
                sizeBtn, sizeBtn);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        hgVirtualContainer.addView(virtualBtn, lp);
        virtualBtn.setRotorPercentage(currentEqualizerInfo.getVirtualValue() / 10);
        virtualBtn.SetListener(new RoundKnobButton.RoundKnobButtonListener() {
            public void onStateChange(boolean newstate) {
            }

            public void onRotate(final int percentage) {
                Log.e("virtualBtn ", "" + percentage);
                virtualLevel = percentage * 10;
                virtualBtn.setCurrentPercent(virtualLevel);
                if (MusicPlayerActivity.musicSrv.getEqualizerHelper() != null)
                    MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentVirtualizer().setStrength((short) virtualLevel);
//                virtualLevel = (short) percentage;
                currentEqualizerInfo.setVirtualValue(virtualLevel);
                updateEQinSharePref();
            }
        });
        //setup spinner
        updateList();

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, listEqualizerString);

        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        buildSavePresetDialog().show();
                        break;
                    default:
                        EqualizerInfo equalizerInfo = new EqualizerInfo();
                        equalizerInfo = infoArrayList.get(position);
                        bassBoostLevel = equalizerInfo.getBassValue();
                        virtualLevel = equalizerInfo.getVirtualValue();
                        equalizerView.updateView(equalizerInfo);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateLanguage();
    }

    public void updateList() {
        //setup spinner
        infoArrayList.clear();
        infoArrayList.add(new EqualizerInfo());
        infoArrayList.add(new EqualizerInfo());
        infoArrayList.addAll(SQLiteDataController.getInstance(this).getAllListEQPresets());
        listEqualizerString.clear();
        listEqualizerString.add("Current Preset");
        listEqualizerString.add("New Preset");
        for (int i = 2; i < infoArrayList.size(); i++) {
            listEqualizerString.add(infoArrayList.get(i).getNameEqualizer());
        }
    }

    public Activity getActivity() {
        return this;
    }

    public void updateLanguage() {
        int lang = sharedPreferences.getInt(MoreFragment.LANG, 0);

        switch (lang) {
            case 0:
                LocaleHelper.setLocale(getActivity(), "en");
                break;
            case 1:
                LocaleHelper.setLocale(getActivity(), "fr");
                break;
            case 2:
                LocaleHelper.setLocale(getActivity(), "de");
                break;
            case 3:
                LocaleHelper.setLocale(getActivity(), "it");
                break;
            case 4:
                LocaleHelper.setLocale(getActivity(), "es");
                break;
            case 5:
                LocaleHelper.setLocale(getActivity(), "pt", "PT");

                break;
            case 6:
                LocaleHelper.setLocale(getActivity(), "pt", "BR");

                break;
            case 7:
                LocaleHelper.setLocale(getActivity(), "ru");
                break;
            case 8:
                LocaleHelper.setLocale(getActivity(), "ja");
                break;
            case 9:
                LocaleHelper.setLocale(getActivity(), "tr");
                break;
        }
        updateView();
    }

    @Bind(R.id.save_as_preset_text)
    TextView saveEqualizerBtn;
    //    @Bind(R.id.load_preset_text)
//    TextView loadEqualizerBtn;
    @Bind(R.id.bassTitle)
    TextView bassTitle;
    @Bind(R.id.virtualTitle)
    TextView virtualTitle;
    @Bind(R.id.volumeTitle)
    TextView volumeTitle;

    public void updateView() {
        if (actionBar != null)
            actionBar.setTitle(R.string.Setting);
        bassTitle.setText(R.string.bass);
        virtualTitle.setText(R.string.virtual);
        volumeTitle.setText(R.string.volume);
        saveEqualizerBtn.setText(R.string.save_preset);
//        loadEqualizerBtn.setText(R.string.load_preset);
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

    @Subscribe
    public void updateEQ(UpdateEQ event) {
        Log.e("Subscribe", "updateEQ");
        updateEQinSharePref();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateEQinSharePref() {
        sharedPreferences.edit().putString(EQ_PREF, gson.toJson(equalizerView.getEqualizerInfo())).commit();

    }

    @OnClick(R.id.loadEqualizer)
    public void LoadPreset() {
        buildLoadPresetDialog().show();
    }

    @OnClick(R.id.saveEqualizer)
    public void SavePreset() {
        buildSavePresetDialog().show();
    }


    /**
     * Builds the "Load Preset" dialog. Does not call the show() method, so this
     * should be done manually after calling this method.
     *
     * @return A fully built AlertDialog reference.
     */
    private AlertDialog buildLoadPresetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Get a cursor with the list of EQ presets.
        final Cursor cursor = SQLiteDataController.getInstance(this).getAllEQPresets();

        //Set the dialog title.
        builder.setTitle(R.string.load_preset);
        builder.setCursor(cursor, new DialogInterface.OnClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Close the dialog.
                dialog.dismiss();
                if (which == 0) {
                    buildSavePresetDialog().show();
                    return;
                }
                cursor.moveToPosition(which);
                //Pass on the equalizer values to the appropriate fragment.
                EqualizerInfo equalizerInfo = new EqualizerInfo();
                equalizerInfo.setBand1Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_50_HZ)));
                equalizerInfo.setBand2Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_130_HZ)));
                equalizerInfo.setBand3Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_320_HZ)));
                equalizerInfo.setBand4Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_800_HZ)));
                equalizerInfo.setBand5Value(cursor.getInt(cursor.getColumnIndex(SQLiteDataController.EQ_2000_HZ)));
                equalizerInfo.setVirtualValue(cursor.getShort(cursor.getColumnIndex(SQLiteDataController.VIRTUALIZER)));
                equalizerInfo.setBassValue(cursor.getShort(cursor.getColumnIndex(SQLiteDataController.BASS_BOOST)));
                bassBoostLevel = equalizerInfo.getBassValue();
                virtualLevel = equalizerInfo.getVirtualValue();

                equalizerView.updateView(equalizerInfo);
                updateEQinSharePref();
                // update rotate
                if (cursor != null)
                    cursor.close();

            }

        }, SQLiteDataController.PRESET_NAME);

        return builder.create();

    }

    /**
     * Builds the "Save Preset" dialog. Does not call the show() method, so you
     * should do this manually when calling this method.
     *
     * @return A fully built AlertDialog reference.
     */
    private AlertDialog buildSavePresetDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.add_new_equalizer_preset_dialog_layout, null);

        final EditText newPresetNameField = (EditText) dialogView.findViewById(R.id.new_preset_name_text_field);
//        newPresetNameField.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Light"));
        newPresetNameField.setPaintFlags(newPresetNameField.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        //Set the dialog title.
        builder.setTitle(R.string.save_preset);
        builder.setView(dialogView);
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();

            }

        });

        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Get the preset name from the text field.
                String presetName = newPresetNameField.getText().toString();
                EqualizerInfo currentEqualizer = equalizerView.getEqualizerInfo();
                //Add the preset and it's values to the DB.
                SQLiteDataController.getInstance(EqualizerActivity.this).addNewEQPreset(presetName,
                        (short) currentEqualizer.getBand1Value(),
                        (short) currentEqualizer.getBand2Value(),
                        (short) currentEqualizer.getBand3Value(),
                        (short) currentEqualizer.getBand4Value(),
                        (short) currentEqualizer.getBand5Value(),
                        (short) bassBoostLevel,
                        (short) virtualLevel);

                Toast.makeText(EqualizerActivity.this, R.string.preset_saved, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                updateList();
                dataAdapter.notifyDataSetChanged();
                spinner.setSelection(listEqualizerString.size() - 1);
            }

        });

        return builder.create();

    }

    public void settingBackground() {
        sharedPreferences = getSharedPreferences(Constants.MUSIC_PLAYER, MODE_PRIVATE);
        int type = sharedPreferences.getInt(Constants.ID_BG, 0);
        SingleBackground.getInstance().settingBackground(this, imvRoot, type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            settingBackground();
        } catch (Exception e) {

        }
        MainApplication.getInstance().trackScreenView("Equalizer Activity");
    }
}
