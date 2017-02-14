package media.musicplayer.songs.mp3player.audio.utils;

/**
 * Created by Duc-Nguyen on 4/19/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Locale;

/**
 * This class is used to change your application locale and persist this change for the next time
 * that your app is going to be used.
 * <p/>
 * You can also change the locale of your application on the fly by using the setLocale method.
 * <p/>
 * Created by gunhansancar on 07/10/15.
 */
public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private static final String SELECTED_COUNTRY = "Locale.Helper.Selected.Country";

    public static void onCreate(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        if (lang.equals("pt")) {
            String country = getPersistedDataCountry(context);
            setLocale(context, lang, country);
        } else {
            setLocale(context, lang);
        }
    }

    public static void onCreate(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        Log.e("lang onCreate"," "+lang);

        if (lang.equals("pt")) {
            String country = getPersistedDataCountry(context);
            setLocale(context, lang, country);
        } else {
            setLocale(context, lang);
        }
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static void setLocale(Context context, String language) {
        persist(context, language);
        updateResources(context, language);
    }

    public static void setLocale(Context context, String language, String sub) {
        persist(context, language, sub);
        updateResources(context, language, sub);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = context.getSharedPreferences("language",context.MODE_PRIVATE);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static String getPersistedDataCountry(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("language",context.MODE_PRIVATE);
        return preferences.getString(SELECTED_COUNTRY, "PT");
    }

    private static void persist(Context context, String language, String country) {
        SharedPreferences preferences = context.getSharedPreferences("language",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.putString(SELECTED_COUNTRY, country);
        editor.commit();
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences("language",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, language);
        editor.commit();
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        Log.e("configuration.locale"," "+configuration.locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private static void updateResources(Context context, String language, String sub) {
        Locale locale = new Locale(language, sub);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
