package i.am.shiro.imageviewerpoc.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.annimon.stream.function.IntConsumer;

import static java.lang.Math.abs;

/**
 * Responsible for reporting when the current page has changed and for snapping to page when
 * scrolling has finished
 */
public final class RecyclerViewPageWidget extends PagerSnapHelper {

    private final int scrollingThresholdVelocity;

    private IntConsumer onCurrentPositionChangeListener;

    private boolean isPageSnapEnabled;

    public RecyclerViewPageWidget(@NonNull RecyclerView recyclerView) {
        scrollingThresholdVelocity = recyclerView.getMinFlingVelocity() * 50;
        attachToRecyclerView(recyclerView);
    }

    public RecyclerViewPageWidget setOnCurrentPositionChangeListener(IntConsumer onCurrentPositionChangeListener) {
        this.onCurrentPositionChangeListener = onCurrentPositionChangeListener;
        return this;
    }

    public RecyclerViewPageWidget setPageSnapEnabled(boolean pageSnapEnabled) {
        isPageSnapEnabled = pageSnapEnabled;
        return this;
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        if (!isPageSnapEnabled || abs(velocityX) >= scrollingThresholdVelocity) {
            return false;
        }
        return super.onFling(velocityX, velocityY);
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        View snapView = super.findSnapView(layoutManager);
        if (snapView != null) {
            int currentPosition = layoutManager.getPosition(snapView);
            onCurrentPositionChangeListener.accept(currentPosition);
        }
        return snapView;
    }
}
