package i.am.shiro.imageviewerpoc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.listener.OnTouchGestureListener;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;

import static android.support.v4.view.ViewCompat.requireViewById;
import static java.lang.Math.abs;

public class ImagePagerFragment extends Fragment {

    private int pagerTapZoneWidth;
    private View controlsOverlay;
    private View directionChooserOverlay;
    private LinearLayoutManager llm;
    private PagerController pagerController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_viewer, container, false);
        pagerTapZoneWidth = getResources().getDimensionPixelSize(R.dimen.tap_zone_width);

        ImageRecyclerAdapter adapter = new ImageRecyclerAdapter();

        RecyclerView recyclerView = requireViewById(view, R.id.image_viewer_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);

        ViewModelProviders.of(requireActivity())
                .get(ImageViewerViewModel.class)
                .getImages()
                .observe(this, adapter::setImageUris);

        pagerController = new PagerController(recyclerView);
        OnTouchGestureListener touchGestureListener = new OnTouchGestureListener(getContext(), pagerController);
        adapter.setGestureListener(touchGestureListener);

        controlsOverlay = requireViewById(view, R.id.image_viewer_controls_overlay);
        controlsOverlay.setOnClickListener(v -> controlsOverlay.setVisibility(View.GONE));

        if (PrefsMockup.DIRECTION_NONE == PrefsMockup.readingDirection) {
            directionChooserOverlay = requireViewById(view, R.id.image_viewer_direction_chooser_overlay);
            directionChooserOverlay.setVisibility(View.VISIBLE);

            View ltrButton = requireViewById(view, R.id.chooseDirectionLtr);
            ltrButton.setOnClickListener(v -> chooseReadingDirection(PrefsMockup.DIRECTION_LTR));

            View rtlButton = requireViewById(view, R.id.chooseDirectionRtl);
            rtlButton.setOnClickListener(v -> chooseReadingDirection(PrefsMockup.DIRECTION_RTL));
        }

        return view;
    }

    private void chooseReadingDirection(int readingDirection) {
        PrefsMockup.readingDirection = readingDirection;
        directionChooserOverlay.setVisibility(View.GONE);
        llm.setReverseLayout(PrefsMockup.DIRECTION_RTL == readingDirection);
    }

    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            pagerController.previousPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            pagerController.nextPage();
            return true;
        }
        return false;
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
            if (e.getX() < pagerTapZoneWidth) { // Left zone
                if (PrefsMockup.DIRECTION_LTR == PrefsMockup.readingDirection)
                    previousPage();
                else
                    nextPage();
            } else if (e.getX() > recyclerView.getWidth() - pagerTapZoneWidth) { // Right zone
                if (PrefsMockup.DIRECTION_LTR == PrefsMockup.readingDirection)
                    nextPage();
                else
                    previousPage();
            } else { // Center zone
                controlsOverlay.setVisibility(View.VISIBLE);
            }
            return false;
        }
    }
}
