package i.am.shiro.imageviewerpoc.listener;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class OnTouchGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    private final GestureDetectorCompat gestureDetector;
//    private final SubsamplingScaleImageView imageView;

    public OnTouchGestureListener(Context context, OnTapListener tapListener) {
//        this.imageView = imageView;
        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//                if (imageView.isReady())
                    return tapListener.onTap(e);
//                else return false;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public interface OnTapListener {
        boolean onTap(MotionEvent e);
    }
}
