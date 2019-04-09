package i.am.shiro.imageviewerpoc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.util.BundleManager;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private final List<String> images = Arrays.asList("a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());

        setContentView(R.layout.activity_main);

        View fab = findViewById(R.id.fab_launch);
        fab.setOnClickListener(v -> launchViewer());
    }

    private void launchViewer() {
        BundleManager manager = new BundleManager();
        manager.setUrisStr(images); // TODO - consider using java.net.Uri instead

        Intent viewer = new Intent(this, ImageViewerActivity.class);
        viewer.putExtras(manager.getBundle());

        startActivity(viewer);
    }
}
