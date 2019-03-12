package i.am.shiro.imageviewerpoc;

import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;

public final class RecyclerViewPager {

    final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

    final RecyclerView recyclerView;

    boolean pagingEnabled;

    @SuppressWarnings("WeakerAccess")
    public RecyclerViewPager(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @SuppressWarnings("WeakerAccess")
    public void enablePaging() {
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        pagingEnabled = true;
    }

    @SuppressWarnings("WeakerAccess")
    public void disablePaging() {
        pagerSnapHelper.attachToRecyclerView(null);
        pagingEnabled = false;
    }

    @SuppressWarnings("WeakerAccess")
    public void togglePaging() {
        if (pagingEnabled) {
            disablePaging();
        } else {
            enablePaging();
        }
    }
}
