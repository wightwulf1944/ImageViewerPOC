package i.am.shiro.imageviewerpoc.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import i.am.shiro.imageviewerpoc.R;
import timber.log.Timber;

import static java.lang.Math.abs;

public class GalleryRecyclerView extends RecyclerView {

    private final GestureDetectorCompat gestureDetector;

    private final int pagerTapZoneWidth;

    private int currentPosition;

    public GalleryRecyclerView(Context context) {
        this(context, null);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new GestureDetectorCompat(context, new OnGestureListener());
        pagerTapZoneWidth = getResources().getDimensionPixelSize(R.dimen.tap_zone_width);

        GalleryPagerHelper galleryPagerHelper = new GalleryPagerHelper();
        galleryPagerHelper.attachToRecyclerView(this);
    }

    public void nextPage() {
        int itemCount = getAdapter().getItemCount();
        if (currentPosition == itemCount - 1) return;
        smoothScrollToPosition(++currentPosition);
    }

    public void previousPage() {
        if (currentPosition == 0) return;
        smoothScrollToPosition(--currentPosition);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Timber.w("onInterceptTouchEvent %s", ev);
        gestureDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Timber.w("onTouchEvent %s", ev);
        gestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    private final class OnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (e.getX() < pagerTapZoneWidth) {
                previousPage();
            } else if (e.getX() > getWidth() - pagerTapZoneWidth) {
                nextPage();
            }
            return false;
        }
    }

    private final class GalleryPagerHelper extends PagerSnapHelper {

        @Override
        public boolean onFling(int velocityX, int velocityY) {
            int scrollingThresholdVelocity = getMinFlingVelocity() * 50;
            if (abs(velocityX) >= scrollingThresholdVelocity) {
                return false;
            }
            return super.onFling(velocityX, velocityY);
        }

        @Nullable
        @Override
        public View findSnapView(RecyclerView.LayoutManager layoutManager) {
            View snapView = super.findSnapView(layoutManager);
            if (snapView != null) {
                currentPosition = layoutManager.getPosition(snapView);
            }
            return snapView;
        }
    }
}
