package i.am.shiro.imageviewerpoc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Switch;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;

import static android.support.v4.view.ViewCompat.requireViewById;

public class ViewerPrefsDialogFragment extends DialogFragment {

    public static void invoke(Fragment parent) {
        ViewerPrefsDialogFragment fragment = new ViewerPrefsDialogFragment();
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ViewerBrowseModeDialog);
        fragment.show(parent.getChildFragmentManager(), null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        return inflater.inflate(R.layout.dialog_viewer_prefs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Switch theSwitch = requireViewById(view, R.id.viewer_prefs_resume_reading_action);
        theSwitch.setChecked(PrefsMockup.isViewerResumeLastLeft());
        theSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> PrefsMockup.setViewerResumeLastLeft(isChecked));

        theSwitch = requireViewById(view, R.id.viewer_prefs_keep_screen_action);
        theSwitch.setChecked(PrefsMockup.isViewerKeepScreenOn());
        theSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> PrefsMockup.setViewerKeepScreenOn(isChecked));

        RadioGroup theRadio = requireViewById(view, R.id.viewer_prefs_display_mode_group);
        switch (PrefsMockup.getViewerResizeMode()) {
            case (PrefsMockup.Constant.PREF_VIEWER_DISPLAY_FIT):
                theRadio.check(R.id.viewer_prefs_display_mode_action_fit);
                break;
            case (PrefsMockup.Constant.PREF_VIEWER_DISPLAY_FILL):
                theRadio.check(R.id.viewer_prefs_display_mode_action_fill);
                break;
        }
        theRadio.setOnCheckedChangeListener(this::onChangeDisplayMode);

        theRadio = requireViewById(view, R.id.viewer_prefs_browse_mode_group);
        switch (PrefsMockup.getViewerBrowseMode()) {
            case (PrefsMockup.Constant.PREF_VIEWER_BROWSE_LTR):
                theRadio.check(R.id.viewer_prefs_browse_mode_action_ltr);
                break;
            case (PrefsMockup.Constant.PREF_VIEWER_BROWSE_RTL):
                theRadio.check(R.id.viewer_prefs_browse_mode_action_rtl);
                break;
            case (PrefsMockup.Constant.PREF_VIEWER_BROWSE_TTB):
                theRadio.check(R.id.viewer_prefs_browse_mode_action_ttb);
                break;
        }
        theRadio.setOnCheckedChangeListener(this::onChangeBrowseMode);
    }

    private void onChangeDisplayMode(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case (R.id.viewer_prefs_display_mode_action_fit):
                PrefsMockup.setViewerResizeMode(PrefsMockup.Constant.PREF_VIEWER_DISPLAY_FIT);
                break;
            case (R.id.viewer_prefs_display_mode_action_fill):
                PrefsMockup.setViewerResizeMode(PrefsMockup.Constant.PREF_VIEWER_DISPLAY_FILL);
                break;
        }
    }

    private void onChangeBrowseMode(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case (R.id.viewer_prefs_browse_mode_action_ltr):
                PrefsMockup.setViewerBrowseMode(PrefsMockup.Constant.PREF_VIEWER_BROWSE_LTR);
                break;
            case (R.id.viewer_prefs_browse_mode_action_rtl):
                PrefsMockup.setViewerBrowseMode(PrefsMockup.Constant.PREF_VIEWER_BROWSE_RTL);
                break;
            case (R.id.viewer_prefs_browse_mode_action_ttb):
                PrefsMockup.setViewerBrowseMode(PrefsMockup.Constant.PREF_VIEWER_BROWSE_TTB);
                break;
        }
    }
}
