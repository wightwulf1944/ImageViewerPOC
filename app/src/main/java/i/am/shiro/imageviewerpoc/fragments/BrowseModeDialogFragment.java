package i.am.shiro.imageviewerpoc.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;

import static android.support.v4.view.ViewCompat.requireViewById;

public class BrowseModeDialogFragment extends DialogFragment {

    public static void invoke(Fragment parent) {
        BrowseModeDialogFragment fragment = new BrowseModeDialogFragment();
        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ViewerBrowseModeDialog);
        fragment.setCancelable(false);
        fragment.show(parent.getChildFragmentManager(), null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        return inflater.inflate(R.layout.dialog_viewer_browse_mode_chooser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View ltrButton = requireViewById(view, R.id.chooseHorizontalLtr);
        ltrButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.Constant.PREF_VIEWER_BROWSE_LTR));

        View rtlButton = requireViewById(view, R.id.chooseHorizontalRtl);
        rtlButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.Constant.PREF_VIEWER_BROWSE_RTL));

        View verticalButton = requireViewById(view, R.id.chooseVertical);
        verticalButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.Constant.PREF_VIEWER_BROWSE_TTB));
    }

    private void chooseBrowseMode(int browseMode) {
        PrefsMockup.setViewerBrowseMode(browseMode);
        getParent().onBrowseModeChange();
        dismiss();
    }

    private Parent getParent() {
        return (Parent) getParentFragment();
    }

    public interface Parent {
        void onBrowseModeChange();
    }
}
