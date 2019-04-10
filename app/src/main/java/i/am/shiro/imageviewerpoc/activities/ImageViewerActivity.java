package i.am.shiro.imageviewerpoc.activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import i.am.shiro.imageviewerpoc.fragments.ImagePagerFragment;
import i.am.shiro.imageviewerpoc.util.BundleManager;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            BundleManager manager = new BundleManager(intent.getExtras());
            List<String> uris = manager.getUrisStr();

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

        if (null == savedInstanceState) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new ImagePagerFragment())
                    .commit();
        }
    }
}
