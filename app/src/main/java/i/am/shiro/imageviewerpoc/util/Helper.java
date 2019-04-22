package i.am.shiro.imageviewerpoc.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Helper {

    public static void setStatusBarButtonsVisibility(Activity activity, boolean visible) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (!visible) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } else {
            View decorView = window.getDecorView();
            int uiOptions;
            if (visible) {
                uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            } else {
                uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
