package i.am.shiro.imageviewerpoc.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;

public class ViewerPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.viewer_preferences, rootKey);

        findPreference(PrefsMockup.Key.PREF_VIEWER_KEEP_SCREEN_ON)
                .setOnPreferenceChangeListener((preference, newValue) -> onPrefRequiringRestartChanged());
    }

    private boolean onPrefRequiringRestartChanged() {
//        ToastUtil.toast(R.string.restart_needed); TODO activate when migrating to Hentoid
        Toast.makeText(requireContext(), "Viewer restart is needed to take changed into account", Toast.LENGTH_LONG).show();
        return true;
    }

}
