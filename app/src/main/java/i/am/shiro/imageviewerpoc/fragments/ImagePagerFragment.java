package i.am.shiro.imageviewerpoc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.listener.OnTouchGestureListener;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;

import static android.support.v4.view.ViewCompat.requireViewById;
import static java.lang.Math.abs;

/**
 * TODO
 * <p>
 */
public class ImagePagerFragment extends Fragment {

    private int pagerTapZoneWidth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_viewer, container, false);
        pagerTapZoneWidth = getResources().getDimensionPixelSize(R.dimen.tap_zone_width);

        ImageRecyclerAdapter adapter = new ImageRecyclerAdapter();

        RecyclerView recyclerView = requireViewById(view, R.id.recycler_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        ViewModelProviders.of(requireActivity())
                .get(ImageViewerViewModel.class)
                .getImages()
                .observe(this, adapter::setImageUris);

        PagerController pagerController = new PagerController(recyclerView);
        OnTouchGestureListener touchGestureListener = new OnTouchGestureListener(getContext(), pagerController);
        adapter.setGestureListener(touchGestureListener);

        return view;
    }

    public final class PagerController extends PagerSnapHelper implements OnTouchGestureListener.OnTapListener {

        private final RecyclerView recyclerView;

        private final int scrollingThresholdVelocity;

        private int currentPosition;

        PagerController(@NonNull RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            scrollingThresholdVelocity = recyclerView.getMinFlingVelocity() * 50;
            attachToRecyclerView(recyclerView);
        }

        void nextPage() {
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (currentPosition == itemCount - 1) return;
            recyclerView.smoothScrollToPosition(++currentPosition);
        }

        void previousPage() {
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

        @Override
        public boolean onTap(MotionEvent e) {
            if (e.getX() < pagerTapZoneWidth) {
                previousPage();
            } else if (e.getX() > recyclerView.getWidth() - pagerTapZoneWidth) {
                nextPage();
            }
            return false;
        }
    }
}
