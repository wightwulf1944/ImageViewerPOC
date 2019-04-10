package i.am.shiro.imageviewerpoc.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.util.BundleManager;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    // ASSETS ARE TO BE PUT INTO THE DOWNLOADS FOLDER
    // SubsamplingScaleImageView does _not_ support animated GIFs
    private final List<String> images = Arrays.asList("01.jpg", "02.png", "03.jpg", "01.jpg", "02.png", "03.jpg", "01.jpg", "02.png", "03.jpg", "01.jpg", "02.png", "03.jpg", "01.jpg", "02.png", "03.jpg", "01.jpg", "02.png", "03.jpg" );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());

        setContentView(R.layout.activity_main);

        View fab = findViewById(R.id.fab_launch);
        fab.setOnClickListener(v -> launchViewer());

        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        for (int i = 0; i < images.size(); i++) {
            images.set(i, downloadsFolder.getAbsolutePath() + "/" + images.get(i));
        }
    }

    private void launchViewer() {
        BundleManager manager = new BundleManager();
        manager.setUrisStr(images); // TODO - consider using java.net.Uri instead

        Intent viewer = new Intent(this, ImageViewerActivity.class);
        viewer.putExtras(manager.getBundle());

        startActivity(viewer);
    }
}
