package i.am.shiro.imageviewerpoc.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import i.am.shiro.imageviewerpoc.fragments.ImagePagerFragment;
import i.am.shiro.imageviewerpoc.util.BundleManager;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;

public class ImageViewerActivity extends AppCompatActivity {

    // ViewModel of this activity
    private ImageViewerViewModel viewModel;

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

            viewModel = ViewModelProviders.of(this).get(ImageViewerViewModel.class);
            viewModel.setImages(uris);
        }

        if (null == savedInstanceState) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new ImagePagerFragment())
                    .commit();
        }
    }
}
