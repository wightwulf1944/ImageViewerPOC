package i.am.shiro.imageviewerpoc;

public class PrefsMockup {

    public static final int RESIZE_MODE_FIT = 0;
    public static final int RESIZE_MODE_FILL = 1;

    public static final int DIRECTION_NONE = -1;
    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;

    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;


    public static int resizeMode = RESIZE_MODE_FIT;
    public static int readingDirection = DIRECTION_NONE;
    public static int orientation = ORIENTATION_HORIZONTAL;
    public static boolean snapping = true;

    public static boolean resume_where_last_finished = true;
}
