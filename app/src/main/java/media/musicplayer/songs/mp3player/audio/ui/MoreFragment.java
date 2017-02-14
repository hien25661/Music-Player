package media.musicplayer.songs.mp3player.audio.ui;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import media.musicplayer.songs.mp3player.audio.BuildConfig;
import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.application.MainApplication;
import media.musicplayer.songs.mp3player.audio.receiver.AlarmReciever;
import media.musicplayer.songs.mp3player.audio.utils.Constants;
import media.musicplayer.songs.mp3player.audio.utils.LocaleHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {

    public static String FRAGMENT_SKIN_TAG = "SkinFragment";
    @Bind(R.id.version)
    TextView version;
    @Bind(R.id.curLanguage)
    TextView curLangegue;
    @Bind(R.id.rlLanguage)
    RelativeLayout rlLanguage;
    SharedPreferences sharedPreferences;
    @Bind(R.id.titleVersion)
    TextView titleVersion;
    @Bind(R.id.titleLanguage)
    TextView titleLanguage;

    @Bind(R.id.titleMoreapp)
    TextView titleMoreapp;

    @Bind(R.id.titlePlug)
    TextView titleHeadphone;
    @Bind(R.id.detailPlug)
    TextView titlePlugEnable;

    @Bind(R.id.titleRateus)
    TextView titleRateus;

    @Bind(R.id.rlTimer)
    RelativeLayout rlTimer;
    @Bind(R.id.titleTimer)
    TextView titleTimer;
    @Bind(R.id.curTimer)
    TextView curTimer;

    @Bind(R.id.titleShake)
    TextView titleShake;
    @Bind(R.id.detailShake)
    TextView titleDetailShake;

    @Bind(R.id.titleLock)
    TextView titleLock;
    @Bind(R.id.detailLock)
    TextView titleDetailLock;

//    @Bind(R.id.titleRemoveads)
//    TextView titleRemoveads;
    @Bind(R.id.chk_shake)
    CheckBox chk_shake;
    @Bind(R.id.chk_plug)
    CheckBox chk_plug;
    @Bind(R.id.chk_lock)
    CheckBox chk_lock;
    @Bind(R.id.titleSkin)
    TextView titleSkin;
    public MoreFragment() {
        // Required empty public constructor
    }

    public void updateCurrentView() {
        titleVersion.setText(R.string.Version);
        titleLanguage.setText(R.string.language);
        titleMoreapp.setText(R.string.more_app);
        titleRateus.setText(R.string.rate_us);
        titleHeadphone.setText(R.string.plugin);
        titleShake.setText(R.string.shake);
        titleSkin.setText(R.string.skin_setting);
//        titleRemoveads.setText(R.string.remove_ads);
        ((SettingActivity) getActivity()).setTitleString();

    }

    void restartApplication() {
        Intent i = new Intent(getActivity(), RestartActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(i);
    }

    @OnClick(R.id.rlRateus)
    public void rateUs() {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    public static final String MORE_APP = "Miniclues+Entertainment";

    @OnClick(R.id.rlMoreApp)
    public void MoreApp() {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
        Uri uri = Uri.parse("market://developer?id=" + MORE_APP);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/developer?id=" + MORE_APP)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, v);
        String versionName = BuildConfig.VERSION_NAME;
        version.setText("" + versionName);
        sharedPreferences = getActivity().getSharedPreferences(Constants.MUSIC_PLAYER, Context.MODE_PRIVATE);
        int lang = sharedPreferences.getInt(LANG, 0);
        switch (lang) {
            case 0:
                LocaleHelper.setLocale(getActivity(), "en");
                curLangegue.setText(R.string.English);
                break;
            case 1:
                curLangegue.setText(R.string.France);
                LocaleHelper.setLocale(getActivity(), "fr");

                break;
            case 2:
                curLangegue.setText(R.string.Deutsch);
                LocaleHelper.setLocale(getActivity(), "de");

                break;
            case 3:
                curLangegue.setText(R.string.Italiano);
                LocaleHelper.setLocale(getActivity(), "it");

                break;
            case 4:
                curLangegue.setText(R.string.Espanol);
                LocaleHelper.setLocale(getActivity(), "es");

                break;
            case 5:
                curLangegue.setText(R.string.Portugues);
                LocaleHelper.setLocale(getActivity(), "pt");
                break;
            case 6:
                curLangegue.setText(R.string.PortuguesBR);
                LocaleHelper.setLocale(getActivity(), "vi");

                break;
            case 7:
                curLangegue.setText(R.string.Pycck);
                LocaleHelper.setLocale(getActivity(), "ru");

                break;
            case 8:
                curLangegue.setText(R.string.Japan);
                LocaleHelper.setLocale(getActivity(), "ja");

                break;
            case 9:
                curLangegue.setText(R.string.Turkey);
                LocaleHelper.setLocale(getActivity(), "tr");

                break;
        }
        boolean isPlayPlugin = sharedPreferences.getBoolean(Constants.ENABLE_PLUGIN,true);
        titlePlugEnable.setText((isPlayPlugin)?"Enabled":"Disabled");
        chk_plug.setChecked((isPlayPlugin)? true : false);

        boolean isShaking = sharedPreferences.getBoolean(Constants.ENABLE_SHAKE,false);
        titleDetailShake.setText((isShaking)?"Enabled":"Disabled");
        chk_shake.setChecked((isShaking)? true : false);

        boolean isLocked = sharedPreferences.getBoolean(Constants.ENABLE_LOCK,true);
        titleDetailLock.setText((isLocked)?"Enabled":"Disabled");
        chk_lock.setChecked((isLocked)? true:false);

        chk_plug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Constants.ENABLE_PLUGIN, isChecked).commit();
            }
        });
        chk_shake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Constants.ENABLE_SHAKE, isChecked).commit();
            }
        });
        chk_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Constants.ENABLE_LOCK, isChecked).commit();
            }
        });

        updateCurrentView();
//        EventBus.getDefault().post(new RestartEvent());
        return v;
    }

    @OnClick(R.id.rlSkin)
    public void onClickSkin() {
        Intent mIntent = new Intent(getActivity(), SkinActivity.class);
        startActivity(mIntent);
    }

    public static final String LANG = "language";
    public static final String TIMER = "timer";

    @OnClick(R.id.rlLanguage)
    public void showDialogLanguge() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.language);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getActivity().getResources().getString(R.string.English));
        arrayAdapter.add(getActivity().getResources().getString(R.string.France));
        arrayAdapter.add(getActivity().getResources().getString(R.string.Deutsch));
        arrayAdapter.add(getActivity().getResources().getString(R.string.Italiano));
        arrayAdapter.add(getActivity().getResources().getString(R.string.Espanol));
        arrayAdapter.add(getActivity().getResources().getString(R.string.Portugues));
        arrayAdapter.add(getActivity().getResources().getString(R.string.PortuguesBR));
        arrayAdapter.add(getActivity().getResources().getString(R.string.Pycck));
        arrayAdapter.add(getActivity().getResources().getString(R.string.Japan));
        arrayAdapter.add(getActivity().getResources().getString(R.string.Turkey));

        builderSingle.setNegativeButton(
                R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        dialog.dismiss();

                        switch (which) {
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
                        updateCurrentView();
                        curLangegue.setText(strName);
                        sharedPreferences.edit().putInt(LANG, which).commit();
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.mess_change_language)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        dialog.dismiss();
                                    }
                                }).show();
//                        restartApplication();
                    }
                });
        builderSingle.show();
    }

    boolean validated = false;
    Calendar c;

    @OnClick(R.id.rlTimer)
    public void showDialogSetTimer() {
        validated = false;
        c = Calendar.getInstance();
        final TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (validated == true) {
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            c.set(Calendar.SECOND, 0);
                            curTimer.setText("" + hourOfDay + " : " + minute);

                            scheduleAlarm(c.getTimeInMillis());
                            sharedPreferences.edit().putString(TIMER, "" + hourOfDay + " : " + minute).commit();
                        }
                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
        tpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    validated = false;
                    tpd.dismiss();
                }
            }
        });
        tpd.setButton(DialogInterface.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    validated = true;
                }
            }
        });
        tpd.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().trackScreenView("More Tab");
        curTimer.setText(sharedPreferences.getString(TIMER, getString(R.string.timernotset)));
    }

    public void scheduleAlarm(long milisecond) {
        Long time = milisecond;

        Intent intentAlarm = new Intent(getActivity(), AlarmReciever.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(getActivity(), 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

    }
    /*@OnClick(R.id.rlPlug)
    public void showDialogHeadphoneSetting() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.plug_earphone);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getActivity().getResources().getString(R.string.enable));
        arrayAdapter.add(getActivity().getResources().getString(R.string.disable));
        builderSingle.setNegativeButton(
                R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        dialog.dismiss();
                        boolean isPlaying = true;
                        switch (which) {
                            case 0:
                                isPlaying = true;
                                break;
                            case 1:
                                isPlaying = false;
                                break;
                        }
                        updateCurrentView();
                        titlePlugEnable.setText(strName);
                        sharedPreferences.edit().putBoolean(Constants.ENABLE_PLUGIN, isPlaying).commit();
                        new AlertDialog.Builder(getActivity())
                                .setMessage((isPlaying)? (R.string.mess_change_plugin_in):(R.string.mess_change_plugin_out))
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        dialog.dismiss();
                                    }
                                }).show();
//                        restartApplication();
                    }
                });
        builderSingle.show();
    }
    @OnClick(R.id.rlShake)
    public void showDialogShakeSetting() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.shake_setting);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getActivity().getResources().getString(R.string.enable));
        arrayAdapter.add(getActivity().getResources().getString(R.string.disable));
        builderSingle.setNegativeButton(
                R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        dialog.dismiss();
                        boolean isShaking = true;
                        switch (which) {
                            case 0:
                                isShaking = true;
                                break;
                            case 1:
                                isShaking = false;
                                break;
                        }
                        updateCurrentView();
                        titleDetailShake.setText(strName);
                        sharedPreferences.edit().putBoolean(Constants.ENABLE_SHAKE, isShaking).commit();
                        new AlertDialog.Builder(getActivity())
                                .setMessage((isShaking)? (R.string.mess_change_shake_on):(R.string.mess_change_shake_off))
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        dialog.dismiss();
                                    }
                                }).show();
//                        restartApplication();
                    }
                });
        builderSingle.show();
    }

    @OnClick(R.id.rlLock)
    public void showDialogLockSetting() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.lock);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_item);
        arrayAdapter.add(getActivity().getResources().getString(R.string.enable));
        arrayAdapter.add(getActivity().getResources().getString(R.string.disable));
        builderSingle.setNegativeButton(
                R.string.Cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        dialog.dismiss();
                        boolean isLocked = true;
                        switch (which) {
                            case 0:
                                isLocked = true;
                                break;
                            case 1:
                                isLocked = false;
                                break;
                        }
                        updateCurrentView();
                        titleDetailLock.setText(strName);
                        sharedPreferences.edit().putBoolean(Constants.ENABLE_LOCK, isLocked).commit();
                        new AlertDialog.Builder(getActivity())
                                .setMessage((isLocked)? (R.string.mess_change_lock_on):(R.string.mess_change_lock_off))
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                        dialog.dismiss();
                                    }
                                }).show();
//                        restartApplication();
                    }
                });
        builderSingle.show();
    }*/
}
