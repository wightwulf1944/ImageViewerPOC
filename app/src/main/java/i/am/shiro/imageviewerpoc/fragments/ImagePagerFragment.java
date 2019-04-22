package i.am.shiro.imageviewerpoc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.activities.ImageViewerPrefsActivity;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;
import i.am.shiro.imageviewerpoc.widget.OnZoneTapListener;
import i.am.shiro.imageviewerpoc.widget.PageSnapWidget;
import i.am.shiro.imageviewerpoc.widget.PrefetchLinearLayoutManager;
import i.am.shiro.imageviewerpoc.widget.ScrollPositionListener;
import i.am.shiro.imageviewerpoc.widget.VolumeKeyListener;

import static android.support.v4.view.ViewCompat.requireViewById;
import static java.lang.String.format;

public class ImagePagerFragment extends Fragment implements GoToPageDialogFragment.Parent,
        BrowseModeDialogFragment.Parent {

    private final static String KEY_HUD_VISIBLE = "hud_visible";

    private View controlsOverlay;
    private PrefetchLinearLayoutManager llm;
    private ImageRecyclerAdapter adapter;
    private SeekBar seekBar;
    private TextView pageNumber;
    private RecyclerView recyclerView;
    private PageSnapWidget pageSnapWidget;

    private int currentPosition;
    private int maxPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_viewer, container, false);

        initPager(view);
        initControlsOverlay(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageViewerViewModel viewModel = ViewModelProviders.of(requireActivity())
                .get(ImageViewerViewModel.class);

        viewModel
                .getImages()
                .observe(this, this::onImagesChanged);

        if (savedInstanceState == null) {
            recyclerView.scrollToPosition(viewModel.getInitialPosition());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HUD_VISIBLE, controlsOverlay.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        boolean hudVisible = false; // Default state at startup
        if (savedInstanceState != null) {
            hudVisible = savedInstanceState.getBoolean(KEY_HUD_VISIBLE, false);
        }
        controlsOverlay.setVisibility(hudVisible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSystemBarsVisible(controlsOverlay.getVisibility() == View.VISIBLE); // System bars are visible only if HUD is visible
        if (PrefsMockup.Constant.PREF_VIEWER_BROWSE_NONE == PrefsMockup.getViewerBrowseMode())
            BrowseModeDialogFragment.invoke(this);
    }

    private void initPager(View rootView) {
        adapter = new ImageRecyclerAdapter();

        VolumeKeyListener volumeKeyListener = new VolumeKeyListener()
                .setOnVolumeDownKeyListener(this::previousPage)
                .setOnVolumeUpKeyListener(this::nextPage);

        recyclerView = requireViewById(rootView, R.id.image_viewer_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new ScrollPositionListener(this::onCurrentPositionChange));
        recyclerView.setOnKeyListener(volumeKeyListener);

        llm = new PrefetchLinearLayoutManager(getContext());
        llm.setItemPrefetchEnabled(true);
        llm.setPreloadItemCount(2);
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

    private void initControlsOverlay(View rootView) {
        controlsOverlay = requireViewById(rootView, R.id.image_viewer_controls_overlay);
        // Tap back button
        View backButton = requireViewById(rootView, R.id.viewer_back_btn);
        backButton.setOnClickListener(v -> onBackClick());
        // Tap settings button
        View settingsButton = requireViewById(rootView, R.id.viewer_settings_btn);
        settingsButton.setOnClickListener(v -> onSettingsClick());
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
                if (fromUser) seekToPosition(progress);
            }
        });
    }

    private void onBackClick() {
        requireActivity().onBackPressed();
    }

    private void onSettingsClick() {
        Intent viewerSettings = new Intent(requireActivity(), ImageViewerPrefsActivity.class);
        startActivityForResult(viewerSettings, 1);
    }

    private void onImagesChanged(List<String> images) {
        maxPosition = images.size() - 1;
        adapter.setImageUris(images);
        seekBar.setMax(maxPosition);
        updatePageDisplay();
    }

    // Scroll listener
    private void onCurrentPositionChange(int position) {
        currentPosition = position;
        seekBar.setProgress(currentPosition);
        updatePageDisplay();
    }

    private void updatePageDisplay() {
        String pageDisplayText = format("%s / %s", currentPosition + 1, maxPosition + 1);
        pageNumber.setText(pageDisplayText);
    }

    @Override
    public void onBrowseModeChange() {
        // In any case, view has to update according to selected prefs
        llm.setReverseLayout(PrefsMockup.Constant.PREF_VIEWER_DIRECTION_RTL == PrefsMockup.getViewerDirection());
        llm.setOrientation(getOrientation());
        pageSnapWidget.setPageSnapEnabled(PrefsMockup.Constant.PREF_VIEWER_ORIENTATION_VERTICAL != PrefsMockup.getViewerOrientation());
        if (PrefsMockup.isViewerKeepScreenOn())
            requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private int getOrientation() {
        if (PrefsMockup.Constant.PREF_VIEWER_ORIENTATION_HORIZONTAL == PrefsMockup.getViewerOrientation()) {
            return LinearLayoutManager.HORIZONTAL;
        } else {
            return LinearLayoutManager.VERTICAL;
        }
    }

    public void nextPage() {
        if (currentPosition == maxPosition) return;
        recyclerView.smoothScrollToPosition(currentPosition + 1);
    }

    public void previousPage() {
        if (currentPosition == 0) return;
        recyclerView.smoothScrollToPosition(currentPosition - 1);
    }

    private void seekToPosition(int position) {
        if (position == currentPosition + 1 || position == currentPosition - 1) {
            recyclerView.smoothScrollToPosition(position);
        } else {
            recyclerView.scrollToPosition(position);
        }
    }

    @Override
    public void goToPage(int pageNum) {
        int position = pageNum - 1;
        if (position == currentPosition || position < 0 || position > maxPosition) return;
        seekToPosition(position);
    }

    private void onLeftTap() {
        if (PrefsMockup.Constant.PREF_VIEWER_DIRECTION_LTR == PrefsMockup.getViewerDirection())
            previousPage();
        else
            nextPage();
    }

    private void onRightTap() {
        if (PrefsMockup.Constant.PREF_VIEWER_DIRECTION_LTR == PrefsMockup.getViewerDirection())
            nextPage();
        else
            previousPage();
    }

    private void onMiddleTap() {
        // TODO AlphaAnimation to make it appear progressively
        if (View.VISIBLE == controlsOverlay.getVisibility()) {
            controlsOverlay.setVisibility(View.INVISIBLE);
            setSystemBarsVisible(false);
        } else {
            controlsOverlay.setVisibility(View.VISIBLE);
            setSystemBarsVisible(true);
        }
    }

    private void setSystemBarsVisible(boolean visible) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Window window = requireActivity().getWindow();
            if (!visible) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } else {
            int uiOptions;
            if (visible) {
                uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            } else {
                uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            Objects.requireNonNull(getView()).setSystemUiVisibility(uiOptions);
        }
    }
}
