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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.listener.OnZoneTapListener;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;

import static android.support.v4.view.ViewCompat.requireViewById;
import static java.lang.Math.abs;

public class ImagePagerFragment extends Fragment {

    private View controlsOverlay;
    private View browseModeChooserOverlay;
    private LinearLayoutManager llm;
    private PagerController pagerController;
    private ImageRecyclerAdapter adapter;
    private SeekBar seekBar;
    private TextView pageNumber;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_viewer, container, false);

        initPager(view);
        initControlsOverlay(view);
        if (PrefsMockup.DIRECTION_NONE == PrefsMockup.readingDirection) initBrowseModeChooser(view);

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

        OnZoneTapListener onZoneTapListener = new OnZoneTapListener(recyclerView)
                .setOnLeftZoneTapListener(pagerController::onLeftTap)
                .setOnRightZoneTapListener(pagerController::onRightTap)
                .setOnMiddleZoneTapListener(pagerController::onMiddleTap);

        adapter.setItemTouchListener(onZoneTapListener);
    }

    private void initBrowseModeChooser(View rootView) {
        browseModeChooserOverlay = requireViewById(rootView, R.id.image_viewer_browse_mode_chooser_overlay);
        browseModeChooserOverlay.setVisibility(View.VISIBLE);

        View ltrButton = requireViewById(rootView, R.id.chooseHorizontalLtr);
        ltrButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_LTR, PrefsMockup.ORIENTATION_HORIZONTAL));

        View rtlButton = requireViewById(rootView, R.id.chooseHorizontalRtl);
        rtlButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_RTL, PrefsMockup.ORIENTATION_HORIZONTAL));

        View verticalButton = requireViewById(rootView, R.id.chooseVertical);
        rtlButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_LTR, PrefsMockup.ORIENTATION_VERTICAL));
    }

    private void chooseBrowseMode(int readingDirection, int orientation) {
        PrefsMockup.readingDirection = readingDirection;
        PrefsMockup.orientation = orientation;

        llm.setReverseLayout(PrefsMockup.DIRECTION_RTL == readingDirection);
        llm.setOrientation(getOrientation());

        browseModeChooserOverlay.setVisibility(View.INVISIBLE);
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

    private int getOrientation() {
        if (PrefsMockup.orientation == PrefsMockup.ORIENTATION_HORIZONTAL) {
            return LinearLayoutManager.HORIZONTAL;
        } else {
            return LinearLayoutManager.VERTICAL;
        }
    }

    public final class PagerController extends PagerSnapHelper {

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

        private void onLeftTap() {
            if (PrefsMockup.DIRECTION_LTR == PrefsMockup.readingDirection)
                previousPage();
            else
                nextPage();
        }

        private void onRightTap() {
            if (PrefsMockup.DIRECTION_LTR == PrefsMockup.readingDirection)
                nextPage();
            else
                previousPage();
        }

        private void onMiddleTap() {
            controlsOverlay.setVisibility(View.VISIBLE); // TODO AlphaAnimation to make it appear progressively
        }
    }
}
