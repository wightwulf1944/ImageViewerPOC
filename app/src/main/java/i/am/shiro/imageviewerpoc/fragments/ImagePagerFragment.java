package i.am.shiro.imageviewerpoc.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageRecyclerAdapter adapter;
    private SeekBar seekBar;
    private TextView pageNumber;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_viewer, container, false);
        pagerTapZoneWidth = getResources().getDimensionPixelSize(R.dimen.tap_zone_width);

        initPager(view);
        initControlsOverlay(view);
        if (PrefsMockup.DIRECTION_NONE == PrefsMockup.readingDirection) initDirectionChooser(view);

        return view;
    }

    private void initPager(View rootView) {
        adapter = new ImageRecyclerAdapter();

        RecyclerView recyclerView = requireViewById(rootView, R.id.image_viewer_recycler);
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
    }

    private void initDirectionChooser(View rootView) {
        directionChooserOverlay = requireViewById(rootView, R.id.image_viewer_direction_chooser_overlay);
        directionChooserOverlay.setVisibility(View.VISIBLE);

        View ltrButton = requireViewById(rootView, R.id.chooseDirectionLtr);
        ltrButton.setOnClickListener(v -> chooseReadingDirection(PrefsMockup.DIRECTION_LTR));

        View rtlButton = requireViewById(rootView, R.id.chooseDirectionRtl);
        rtlButton.setOnClickListener(v -> chooseReadingDirection(PrefsMockup.DIRECTION_RTL));
    }

    private void chooseReadingDirection(int readingDirection) {
        PrefsMockup.readingDirection = readingDirection;
        directionChooserOverlay.setVisibility(View.INVISIBLE);
        llm.setReverseLayout(PrefsMockup.DIRECTION_RTL == readingDirection);
    }

    private void initControlsOverlay(View rootView) {
        controlsOverlay = requireViewById(rootView, R.id.image_viewer_controls_overlay);
        // Tap center of screen
        controlsOverlay.setOnClickListener(v -> controlsOverlay.setVisibility(View.INVISIBLE));
        // Tap back button
        View backButton = requireViewById(rootView, R.id.viewer_back_btn);
        backButton.setOnClickListener(v -> quitActivity());
        // Tap settings button
        View settingsButton = requireViewById(rootView, R.id.viewer_settings_btn);
        settingsButton.setOnClickListener(v -> launchSettings());
        // Page number button
        pageNumber = requireViewById(rootView, R.id.viewer_pagenumber_text);
        pageNumber.setOnClickListener(v -> inputPageManually());
        // Slider
        seekBar = requireViewById(rootView, R.id.viewer_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pageNumber.setText(progress + " / " + seekBar.getMax());
                if (fromUser) pagerController.toPage(progress);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Has to be called there because the adapter hasn't observed the image list yet when called from onCreateView
        seekBar.setMax(adapter.getItemCount());
    }

    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            pagerController.previousPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            pagerController.nextPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            quitActivity();
        }
        return false;
    }

    private void quitActivity() {
        Activity a = getActivity();
        if (a != null) a.onBackPressed();
    }

    private void launchSettings() {
        Toast.makeText(getContext(), "Settings not implemented yet", Toast.LENGTH_SHORT).show();
    }

    private void inputPageManually() {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        alert.setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
            if (input.getText().length() > 0)
                pagerController.toPage(Integer.parseInt(input.getText().toString()));
        });
        alert.setNegativeButton(android.R.string.cancel, null);
        alert.show();

        input.postDelayed(() -> {
            Activity a = getActivity();
            if (input.requestFocus() && a != null) {
                InputMethodManager imm = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
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
            seekBar.setProgress(currentPosition);
        }

        void previousPage() {
            if (currentPosition == 0) return;
            recyclerView.smoothScrollToPosition(--currentPosition);
            seekBar.setProgress(currentPosition);
        }

        void toPage(int pageNum) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            if ((currentPosition == pageNum) || (pageNum >= itemCount)) return;
            currentPosition = pageNum;
            recyclerView.smoothScrollToPosition(currentPosition);
            seekBar.setProgress(currentPosition);
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
                seekBar.setProgress(currentPosition);
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
                controlsOverlay.setVisibility(View.VISIBLE); // TODO AlphaAnimation to make it appear progressively
            }
            return false;
        }
    }
}
