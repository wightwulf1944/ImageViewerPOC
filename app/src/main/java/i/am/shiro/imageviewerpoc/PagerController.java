package i.am.shiro.imageviewerpoc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import i.am.shiro.imageviewerpoc.fragments.ImagePagerFragment;

import static java.lang.Math.abs;

/**
 * Temporary controller class for convenience to make development easier. Planned to merge stable
 * code back to {@link ImagePagerFragment}
 */
public final class PagerController extends PagerSnapHelper {

    private final RecyclerView recyclerView;

    private final int scrollingThresholdVelocity;

    private int currentPosition;

    public PagerController(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        Context context = recyclerView.getContext();
        GestureDetectorCompat gestureDetector = new GestureDetectorCompat(context, new OnGestureListener());
        recyclerView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        int minFlingVelocity = recyclerView.getMinFlingVelocity();
        int maxFlingVelocity = recyclerView.getMaxFlingVelocity();
        scrollingThresholdVelocity = (minFlingVelocity + maxFlingVelocity) / 2;

        attachToRecyclerView(recyclerView);
    }

    public void nextPage() {
        int itemCount = recyclerView.getAdapter().getItemCount();
        if (currentPosition == itemCount - 1) return;
        recyclerView.smoothScrollToPosition(++currentPosition);
    }

    public void previousPage() {
        if (currentPosition == 0) return;
        recyclerView.smoothScrollToPosition(--currentPosition);
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
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

    private final class OnGestureListener extends GestureDetector.SimpleOnGestureListener {

        private final static double PAGER_ZONE_WIDTH = 0.1; // 10%

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            double pagerZoneWidth = recyclerView.getWidth() * PAGER_ZONE_WIDTH;
            if (e.getX() < pagerZoneWidth) {
                previousPage();
                return true;
            } else if (e.getX() > recyclerView.getWidth() - pagerZoneWidth) {
                nextPage();
                return true;
            }
            return false;
        }
    }
}
