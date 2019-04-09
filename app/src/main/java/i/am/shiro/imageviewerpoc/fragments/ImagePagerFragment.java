package i.am.shiro.imageviewerpoc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.util.Debouncer;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;
import timber.log.Timber;

import static android.support.v4.view.ViewCompat.requireViewById;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * TODO
 * <p>
 */
public class ImagePagerFragment extends Fragment {

    private final static double PAGER_ZONE_WIDTH = 0.1; // 10%

    private final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

    private final Debouncer<MotionEvent> downEventDebouncer = new Debouncer<>(300, this::handleDownEvent);


    private RecyclerView recyclerView;

    private boolean pagingEnabled;

    private int currentPosition;

    private GestureDetectorCompat mDetector;

    private ImageRecyclerAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetectorCompat(this.getContext(), new MyGestureListener());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_viewer, container, false);

        View snapFab = requireViewById(view, R.id.fab_snap);
        snapFab.setOnClickListener(v -> togglePaging());

        adapter = new ImageRecyclerAdapter();

        recyclerView = requireViewById(view, R.id.recycler_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new CurrentPositionListener());
        recyclerView.setOnTouchListener((v, event) -> mDetector.onTouchEvent(event));

        enablePaging();

        ViewModelProviders.of(requireActivity())
                .get(ImageViewerViewModel.class)
                .getImages()
                .observe(this, adapter::setImageUris);

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
        if (currentPosition == adapter.getItemCount() - 1) return;
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
        double pagerZoneWidth = recyclerView.getWidth() * PAGER_ZONE_WIDTH;
        if (event.getX() < pagerZoneWidth) previousPage();
        else if (event.getX() > recyclerView.getWidth() - pagerZoneWidth) nextPage();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            Timber.i("onDown: %s", event.toString());
            downEventDebouncer.submit(event);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            Timber.i("onFling: " + e1.toString() + e2.toString());
            downEventDebouncer.clear();
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Timber.i("onScroll: " + e1.toString() + e2.toString());
            downEventDebouncer.clear();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}
