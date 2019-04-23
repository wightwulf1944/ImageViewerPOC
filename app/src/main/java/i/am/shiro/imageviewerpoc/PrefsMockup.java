package i.am.shiro.imageviewerpoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import timber.log.Timber;

public class PrefsMockup {

    private static SharedPreferences sharedPreferences;

    private static final int VERSION = 1;

    public static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int savedVersion = sharedPreferences.getInt(Key.PREFS_VERSION_KEY, VERSION);
        if (savedVersion != VERSION) {
            Timber.d("Shared Prefs Key Mismatch! Clearing Prefs!");
            sharedPreferences.edit()
                    .clear()
                    .apply();
        }
    }

    public static void registerPrefsChangedListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static boolean isViewerResumeLastLeft() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_RESUME_LAST_LEFT, Default.PREF_VIEWER_RESUME_LAST_LEFT);
    }

    public static void setViewerResumeLastLeft(boolean resumeLastLeft) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_RESUME_LAST_LEFT, resumeLastLeft)
                .apply();
    }

    public static boolean isViewerKeepScreenOn() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_KEEP_SCREEN_ON, Default.PREF_VIEWER_KEEP_SCREEN_ON);
    }

    public static void setViewerKeepScreenOn(boolean keepScreenOn) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_KEEP_SCREEN_ON, keepScreenOn)
                .apply();
    }

    public static int getViewerResizeMode() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_VIEWER_IMAGE_DISPLAY, Integer.toString(Default.PREF_VIEWER_IMAGE_DISPLAY)));
    }

    public static void setViewerResizeMode(int resizeMode) {
        sharedPreferences.edit()
                .putString(Key.PREF_VIEWER_IMAGE_DISPLAY, Integer.toString(resizeMode))
                .apply();
    }

    public static int getViewerBrowseMode() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_VIEWER_BROWSE_MODE, Integer.toString(Default.PREF_VIEWER_BROWSE_MODE)));
    }

    public static int getViewerDirection() {
        return (getViewerBrowseMode() == Constant.PREF_VIEWER_BROWSE_RTL) ? Constant.PREF_VIEWER_DIRECTION_RTL : Constant.PREF_VIEWER_DIRECTION_LTR;
    }

    public static int getViewerOrientation() {
        return (getViewerBrowseMode() == Constant.PREF_VIEWER_BROWSE_TTB) ? Constant.PREF_VIEWER_ORIENTATION_VERTICAL : Constant.PREF_VIEWER_ORIENTATION_HORIZONTAL;
    }

    public static void setViewerBrowseMode(int browseMode) {
        sharedPreferences.edit()
                .putString(Key.PREF_VIEWER_BROWSE_MODE, Integer.toString(browseMode))
                .apply();
    }


    public static final class Key {
        static final String PREFS_VERSION_KEY = "prefs_version";
        public static final String PREF_VIEWER_RESUME_LAST_LEFT = "pref_viewer_resume_last_left";
        public static final String PREF_VIEWER_KEEP_SCREEN_ON = "pref_viewer_keep_screen_on";
        public static final String PREF_VIEWER_IMAGE_DISPLAY = "pref_viewer_image_display";
        public static final String PREF_VIEWER_BROWSE_MODE = "pref_viewer_browse_mode";
    }

    public static final class Default {
        public static final boolean PREF_VIEWER_RESUME_LAST_LEFT = true;
        public static final boolean PREF_VIEWER_KEEP_SCREEN_ON = true;
        public static final int PREF_VIEWER_IMAGE_DISPLAY = Constant.PREF_VIEWER_DISPLAY_FIT;
        public static final int PREF_VIEWER_BROWSE_MODE = Constant.PREF_VIEWER_BROWSE_NONE;
    }

    public static final class Constant {
        public static final int PREF_VIEWER_DISPLAY_FIT = 0;
        public static final int PREF_VIEWER_DISPLAY_FILL = 1;
        public static final int PREF_VIEWER_BROWSE_NONE = -1;
        public static final int PREF_VIEWER_BROWSE_LTR = 0;
        public static final int PREF_VIEWER_BROWSE_RTL = 1;
        public static final int PREF_VIEWER_BROWSE_TTB = 2;
        public static final int PREF_VIEWER_DIRECTION_LTR = 0;
        public static final int PREF_VIEWER_DIRECTION_RTL = 1;
        public static final int PREF_VIEWER_ORIENTATION_HORIZONTAL = 0;
        public static final int PREF_VIEWER_ORIENTATION_VERTICAL = 1;
    }
}
