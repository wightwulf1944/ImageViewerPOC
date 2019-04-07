package i.am.shiro.imageviewerpoc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector;

import java.util.Arrays;
import java.util.List;

import i.am.shiro.imageviewerpoc.util.Debouncer;

import static android.support.v4.view.ViewCompat.requireViewById;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * TODO
 * <p>
 * Even with smaller tap zones, they still accidentally consume swipe events. Consider manually
 * implementing gesture detection and have the whole screen be 1 big tap zone to fix this.
 */
public class ImagePagerFragment extends Fragment {

    private final static double PAGER_ZONE_WIDTH = 0.1; // 10%


    private final List<String> data = Arrays.asList("a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a");

    private final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

    private final Debouncer<MotionEvent> downEventDebouncer = new Debouncer<>(300, this::handleDownEvent);


    private RecyclerView recyclerView;

    private View touchOverlay;

    private boolean pagingEnabled;

    private int currentPosition;

    private GestureDetectorCompat mDetector;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetectorCompat(this.getContext(), new MyGestureListener());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(data);

        View snapFab = requireViewById(view, R.id.fab_snap);
        snapFab.setOnClickListener(v -> togglePaging());

        recyclerView = requireViewById(view, R.id.recycler_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new CurrentPositionListener());

        touchOverlay = requireViewById(view, R.id.touch_overlay);
        touchOverlay.setOnTouchListener((v, event) -> mDetector.onTouchEvent(event));

        pagerSnapHelper.attachToRecyclerView(recyclerView);

        pagingEnabled = true;

        return view;
    }

    private void enablePaging() {
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        pagingEnabled = true;
    }

    private void disablePaging() {
        pagerSnapHelper.attachToRecyclerView(null);
        pagingEnabled = false;
    }

    private void togglePaging() {
        if (pagingEnabled) {
            disablePaging();
        } else {
            enablePaging();
        }
    }

    private void nextPage() {
        if (!pagingEnabled) return;
        if (currentPosition == data.size() - 1) return;
        recyclerView.smoothScrollToPosition(currentPosition + 1);
    }

    private void previousPage() {
        if (!pagingEnabled) return;
        if (currentPosition == 0) return;
        recyclerView.smoothScrollToPosition(currentPosition - 1);
    }

    private class CurrentPositionListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState != SCROLL_STATE_IDLE) return;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    private void handleDownEvent(final MotionEvent event) {
        double pagerZoneWidth = touchOverlay.getWidth() * PAGER_ZONE_WIDTH;
        if (event.getX() < pagerZoneWidth) previousPage();
        else if (event.getX() > touchOverlay.getWidth() - pagerZoneWidth) nextPage();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.i(DEBUG_TAG,"onDown: " + event.toString());
            downEventDebouncer.submit(event);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.i(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            downEventDebouncer.clear();
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i(DEBUG_TAG, "onScroll: " + e1.toString() + e2.toString());
            downEventDebouncer.clear();
            return false;
        }
    }
}
