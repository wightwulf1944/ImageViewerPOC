package i.am.shiro.imageviewerpoc.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;

public class Helper {

    public static void setStatusBarButtonsVisibility(Activity activity, boolean visible) {
        // SYSTEM_UI_FLAG_IMMERSIVE is only available since Kitkat. Hiding the system UI
        // without this flag is pointless, as any screentap would show the system UI again...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions;
            if (visible) {
                uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            } else {
                uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
