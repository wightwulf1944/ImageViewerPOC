package i.am.shiro.imageviewerpoc.fragments;

import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.activities.ImageViewerActivity;
import i.am.shiro.imageviewerpoc.activities.ImageViewerPrefsActivity;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;
import i.am.shiro.imageviewerpoc.widget.OnZoneTapListener;
import i.am.shiro.imageviewerpoc.widget.PageSnapWidget;
import i.am.shiro.imageviewerpoc.widget.PrefetchLinearLayoutManager;
import i.am.shiro.imageviewerpoc.widget.ScrollPositionListener;

import static android.support.v4.view.ViewCompat.requireViewById;
import static java.lang.String.format;

public class ImagePagerFragment extends Fragment implements GoToPageDialogFragment.Parent {

    private View controlsOverlay;
    private PrefetchLinearLayoutManager llm;
    private ImageRecyclerAdapter adapter;
    private SeekBar seekBar;
    private TextView pageNumber;
    private RecyclerView recyclerView;
    private PageSnapWidget pageSnapWidget;

    private int openPageIndex;
    private int currentPosition;
    private int maxPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_viewer, container, false);

        openPageIndex = -1;
        Bundle bundle = getArguments();
        if (bundle != null) {
            openPageIndex = getArguments().getInt("openPageIndex", -1);
        }

        initPager(view);
        initControlsOverlay(view);

        ViewModelProviders.of(requireActivity())
                .get(ImageViewerViewModel.class)
                .getImages()
                .observe(this, this::onImagesChanged);

        setStatusBarButtonsVisibility(requireActivity(), false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int FRAGMENT_BROWSE_MODE = 1;
        if (PrefsMockup.Constant.PREF_VIEWER_BROWSE_NONE == PrefsMockup.getViewerBrowseMode())
            BrowseModeDialogFragment.invoke(requireActivity().getSupportFragmentManager(), this, FRAGMENT_BROWSE_MODE);
    }

    private void initPager(View rootView) {
        adapter = new ImageRecyclerAdapter();

        recyclerView = requireViewById(rootView, R.id.image_viewer_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new ScrollPositionListener(this::onCurrentPositionChange));

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
        if (openPageIndex > -1) {
            goToPage(openPageIndex + 1);
            openPageIndex = -1; // It only has to happen once
        } else {
            updatePageDisplay();
        }
    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
            setStatusBarButtonsVisibility(requireActivity(), false);
        } else {
            controlsOverlay.setVisibility(View.VISIBLE);
            setStatusBarButtonsVisibility(requireActivity(), true);
        }
    }

    private void setStatusBarButtonsVisibility(Activity activity, boolean visible) {
        // SYSTEM_UI_FLAG_IMMERSIVE is only available since Kitkat. Hiding the system UI
        // without this flag is pointless, as any screentap would show the system UI again...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions;
            if (visible) {
                uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            } else {
                uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
