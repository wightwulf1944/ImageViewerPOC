package i.am.shiro.imageviewerpoc.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.annimon.stream.function.IntConsumer;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static java.lang.Math.abs;

/**
 * Responsible for reporting when the current page has changed and for snapping to page when
 * scrolling has finished
 */
public final class RecyclerViewPageWidget extends PagerSnapHelper {

    private final int scrollingThresholdVelocity;

    private final RecyclerView recyclerView;

    private IntConsumer onCurrentPositionChangeListener;

    private boolean isPageSnapEnabled;

    private PagerScrollListener scrollListener;


    public RecyclerViewPageWidget(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        scrollingThresholdVelocity = recyclerView.getMinFlingVelocity() * 50;
        attachToRecyclerView(recyclerView);
        scrollListener = new PagerScrollListener();
        recyclerView.addOnScrollListener(scrollListener);
    }

    public RecyclerViewPageWidget setOnCurrentPositionChangeListener(IntConsumer onCurrentPositionChangeListener) {
        this.onCurrentPositionChangeListener = onCurrentPositionChangeListener;
        return this;
    }

    public RecyclerViewPageWidget setPageSnapEnabled(boolean pageSnapEnabled) {
        isPageSnapEnabled = pageSnapEnabled;
        if (isPageSnapEnabled) {
            attachToRecyclerView(recyclerView);
        } else {
            attachToRecyclerView(null);
        }
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

    class PagerScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (!isPageSnapEnabled && SCROLL_STATE_IDLE == newState) {
                int currentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition(); // Okay, but that's a humble hack
                onCurrentPositionChangeListener.accept(currentPosition);
            }
        }
    }
}
