package i.am.shiro.imageviewerpoc.activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.List;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.fragments.ImagePagerFragment;
import i.am.shiro.imageviewerpoc.util.BundleManager;
import i.am.shiro.imageviewerpoc.util.Helper;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;

public class ImageViewerActivity extends AppCompatActivity {

    ImagePagerFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PrefsMockup.isViewerKeepScreenOn())
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        int openPageIndex = -1;
        if (intent != null) {
            BundleManager manager = new BundleManager(intent.getExtras());
            List<String> uris = manager.getUrisStr();
            if (PrefsMockup.isViewerResumeLastLeft()) openPageIndex = manager.getOpenPageIndex();

            if (null == uris) {
                throw new RuntimeException("Initialization failed");
            }

            ViewModelProviders.of(this)
                    .get(ImageViewerViewModel.class)
                    .setImages(uris);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            this.requestPermissions(permissions, 165498);
        }

        // Allows an full recolor of the status bar with the custom color defined in the activity's theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (null == savedInstanceState) {
            Bundle bundle = new Bundle();
            if (openPageIndex > -1) bundle.putInt("openPageIndex", openPageIndex);

            fragment = new ImagePagerFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide status bar at activity restart
        Helper.setStatusBarButtonsVisibility(this, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                fragment.previousPage();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                fragment.nextPage();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
