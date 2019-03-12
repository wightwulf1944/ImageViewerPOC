package i.am.shiro.imageviewerpoc;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

@SuppressWarnings("WeakerAccess")
public final class RecyclerViewPager {

    private final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

    private final RecyclerView recyclerView;

    private boolean pagingEnabled;

    private int currentPosition;

    public RecyclerViewPager(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(new PageChangeSubject(i -> currentPosition = i));
    }

    public void enablePaging() {
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        pagingEnabled = true;
    }

    public void disablePaging() {
        pagerSnapHelper.attachToRecyclerView(null);
        pagingEnabled = false;
    }

    public void togglePaging() {
        if (pagingEnabled) {
            disablePaging();
        } else {
            enablePaging();
        }
    }

    public void nextPage() {
        if (!pagingEnabled) return;
        if (currentPosition == recyclerView.getChildCount() - 1) return;
        recyclerView.smoothScrollToPosition(currentPosition + 1);
    }

    public void previousPage() {
        if (!pagingEnabled) return;
        if (currentPosition == 0) return;
        recyclerView.smoothScrollToPosition(currentPosition - 1);
    }

    private class PageChangeSubject extends RecyclerView.OnScrollListener {

        private final IntConsumer pageChangeListener;

        private PageChangeSubject(IntConsumer pageChangeListener) {
            this.pageChangeListener = pageChangeListener;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == SCROLL_STATE_IDLE) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                pageChangeListener.accept(currentPosition);
            }
        }
    }

    @FunctionalInterface
    private interface IntConsumer {
        void accept(int i);
    }
}
