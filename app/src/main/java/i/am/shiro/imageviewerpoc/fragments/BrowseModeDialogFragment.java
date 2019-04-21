package i.am.shiro.imageviewerpoc.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;

import static android.support.v4.view.ViewCompat.requireViewById;

public class BrowseModeDialogFragment extends DialogFragment {

    public static void invoke(FragmentManager fragmentManager, Fragment caller, int requestCode) {
        BrowseModeDialogFragment fragment = new BrowseModeDialogFragment();

        fragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ViewerBrowseModeDialog);
        fragment.setCancelable(false);
        fragment.setTargetFragment(caller, requestCode);
        fragment.show(fragmentManager, null);
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
        ltrButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_LTR, PrefsMockup.ORIENTATION_HORIZONTAL));

        View rtlButton = requireViewById(view, R.id.chooseHorizontalRtl);
        rtlButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_RTL, PrefsMockup.ORIENTATION_HORIZONTAL));

        View verticalButton = requireViewById(view, R.id.chooseVertical);
        verticalButton.setOnClickListener(v -> chooseBrowseMode(PrefsMockup.DIRECTION_LTR, PrefsMockup.ORIENTATION_VERTICAL));
    }

    private void chooseBrowseMode(int readingDirection, int orientation) {
        PrefsMockup.readingDirection = readingDirection;
        PrefsMockup.orientation = orientation;

        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());

        dismiss();
    }
}
