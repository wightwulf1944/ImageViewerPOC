package i.am.shiro.imageviewerpoc.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class Helper {

    public static int dpToPixel(Context context, int dp) {
        float scaleFactor =
                (1.0f / DisplayMetrics.DENSITY_DEFAULT)
                        * context.getResources().getDisplayMetrics().densityDpi;

        return (int) (dp * scaleFactor);
    }
}
