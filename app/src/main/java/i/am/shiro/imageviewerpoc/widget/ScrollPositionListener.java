package i.am.shiro.imageviewerpoc.widget;

import android.support.v7.widget.RecyclerView;

import com.annimon.stream.function.IntConsumer;

public final class ScrollPositionListener extends RecyclerView.OnScrollListener {

    private final IntConsumer onPositionChangeListener;

    public ScrollPositionListener(IntConsumer onPositionChangeListener) {
        this.onPositionChangeListener = onPositionChangeListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        float extent = recyclerView.computeHorizontalScrollExtent();
        float offset = recyclerView.computeHorizontalScrollOffset();
        if (extent == 0 && offset == 0) {
            extent = recyclerView.computeVerticalScrollExtent();
            offset = recyclerView.computeVerticalScrollOffset();
        }
        if (extent == 0 && offset == 0) {
            return;
        }
        
        int currentPosition = Math.round(offset / extent);
        onPositionChangeListener.accept(currentPosition);
    }
}
