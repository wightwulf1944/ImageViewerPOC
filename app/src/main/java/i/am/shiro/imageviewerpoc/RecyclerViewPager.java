package i.am.shiro.imageviewerpoc;

import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;

@SuppressWarnings("WeakerAccess")
public final class RecyclerViewPager {

    private final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

    private final RecyclerView recyclerView;

    private boolean pagingEnabled;

    public RecyclerViewPager(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
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
}
