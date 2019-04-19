package i.am.shiro.imageviewerpoc.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;
import i.am.shiro.imageviewerpoc.widget.OnZoneTapListener;
import i.am.shiro.imageviewerpoc.widget.PageSnapWidget;
import i.am.shiro.imageviewerpoc.widget.ScrollPositionListener;

import static android.support.v4.view.ViewCompat.requireViewById;

public class ImagePagerFragment extends Fragment implements GoToPageDialogFragment.Parent {

    private View controlsOverlay;
    private View browseModeChooserOverlay;
    private LinearLayoutManager llm;
    private ImageRecyclerAdapter adapter;
    private SeekBar seekBar;
    private TextView pageNumber;
    private RecyclerView recyclerView;
    private int currentPosition;
    private PageSnapWidget pageSnapWidget;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_viewer, container, false);

        initPager(view);
        initControlsOverlay(view);
        if (PrefsMockup.DIRECTION_NONE == PrefsMockup.readingDirection) initBrowseModeChooser(view);
        initBrowseModeChooser(view);

        ViewModelProviders.of(requireActivity())
                .get(ImageViewerViewModel.class)
                .getImages()
                .observe(this, this::onImagesChanged);

        return view;
    }

    private void onImagesChanged(List<String> images) {
        adapter.setImageUris(images);
        seekBar.setMax(images.size() - 1);
    }

    private void initPager(View rootView) {
        adapter = new ImageRecyclerAdapter();

        recyclerView = requireViewById(rootView, R.id.image_viewer_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new ScrollPositionListener(this::onCurrentPositionChange));

        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);

        pageSnapWidget = new PageSnapWidget(recyclerView)
                .setPageSnapEnabled(true)
                .setFlingFactor(50);

        OnZoneTapListener onZoneTapListener = new OnZoneTapListener(recyclerView)
                .setOnLeftZoneTapListener(this::onLeftTap)
                .setOnRightZoneTapListener(this::onRightTap)
                .setOnMiddleZoneTapListener(this::onMiddleTap);

        adapter.setItemTouchListener(onZoneTapListener);
    }

    private void onCurrentPositionChange(int position) {
        currentPosition = position;
        seekBar.setProgress(currentPosition);
    }

    private void initBrowseModeChooser(View rootView) {
        browseModeChooserOverlay = requireViewById(rootView, R.id.image_viewer_browse_mode_chooser_overlay);
        browseModeChooserOverlay.setVisibility(View.VISIBLE);

        View ltrButton = requireViewById(browseModeChooserOverlay, R.id.chooseHorizontalLtr);
        ltrButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_LTR, PrefsMockup.ORIENTATION_HORIZONTAL));

        View rtlButton = requireViewById(browseModeChooserOverlay, R.id.chooseHorizontalRtl);
        rtlButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_RTL, PrefsMockup.ORIENTATION_HORIZONTAL));

        View verticalButton = requireViewById(browseModeChooserOverlay, R.id.chooseVertical);
        verticalButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_LTR, PrefsMockup.ORIENTATION_VERTICAL));
    }

    private void chooseBrowseMode(int readingDirection, int orientation) {
        PrefsMockup.readingDirection = readingDirection;
        PrefsMockup.orientation = orientation;

        llm.setReverseLayout(PrefsMockup.DIRECTION_RTL == readingDirection);
        llm.setOrientation(getOrientation());

        browseModeChooserOverlay.setVisibility(View.INVISIBLE);

        pageSnapWidget.setPageSnapEnabled(PrefsMockup.ORIENTATION_VERTICAL != orientation);
    }

    private void initControlsOverlay(View rootView) {
        controlsOverlay = requireViewById(rootView, R.id.image_viewer_controls_overlay);
        // Tap back button
        View backButton = requireViewById(rootView, R.id.viewer_back_btn);
        backButton.setOnClickListener(v -> quitActivity());
        // Tap settings button
        View settingsButton = requireViewById(rootView, R.id.viewer_settings_btn);
        settingsButton.setOnClickListener(v -> launchSettings());
        // Page number button
        pageNumber = requireViewById(rootView, R.id.viewer_pagenumber_text);
        pageNumber.setOnClickListener(v -> GoToPageDialogFragment.show(this));
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
                String text = String.format("%s / %s", progress + 1, seekBar.getMax() + 1);
                pageNumber.setText(text);
                if (fromUser) toPage(progress);
            }
        });
    }

    public boolean onKeyDown(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            previousPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            nextPage();
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

    private int getOrientation() {
        if (PrefsMockup.orientation == PrefsMockup.ORIENTATION_HORIZONTAL) {
            return LinearLayoutManager.HORIZONTAL;
        } else {
            return LinearLayoutManager.VERTICAL;
        }
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

    @Override
    public void toPage(int pageNum) {
        int itemCount = recyclerView.getAdapter().getItemCount();
        if ((currentPosition == pageNum) || (pageNum >= itemCount)) return;
        currentPosition = pageNum;
        recyclerView.smoothScrollToPosition(currentPosition);
        seekBar.setProgress(currentPosition);
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
        // TODO AlphaAnimation to make it appear progressively
        if (View.VISIBLE == controlsOverlay.getVisibility()) {
            controlsOverlay.setVisibility(View.INVISIBLE);
        } else {
            controlsOverlay.setVisibility(View.VISIBLE);
        }
    }
}
