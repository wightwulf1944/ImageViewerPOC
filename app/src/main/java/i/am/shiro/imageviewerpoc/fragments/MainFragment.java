package i.am.shiro.imageviewerpoc.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.activities.ImageViewerActivity;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.util.BundleManager;
import i.am.shiro.imageviewerpoc.util.Debouncer;

import static android.support.v4.view.ViewCompat.requireViewById;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * TODO
 * <p>
 * Even with smaller tap zones, they still accidentally consume swipe events. Consider manually
 * implementing gesture detection and have the whole screen be 1 big tap zone to fix this.
 */
public class MainFragment extends Fragment {

    private final List<String> images = Arrays.asList("a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        View fab = requireViewById(view, R.id.fab_launch);
        fab.setOnClickListener(v -> launchViewer());

        return view;
    }

    private void launchViewer()
    {
        Intent viewer = new Intent(this.getContext(), ImageViewerActivity.class);

        BundleManager manager = new BundleManager();
        manager.setUrisStr(images); // TODO - consider using java.net.Uri instead
        viewer.putExtras(manager.getBundle());

        startActivity(viewer);
    }

}
