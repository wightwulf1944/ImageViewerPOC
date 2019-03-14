package i.am.shiro.imageviewerpoc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import static android.support.v4.view.ViewCompat.requireViewById;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * TODO
 * <p>
 * Even with smaller tap zones, they still accidentally consume swipe events. Consider manually
 * implementing gesture detection and have the whole screen be 1 big tap zone to fix this.
 */
public class ImagePagerFragment extends Fragment {

    private final List<String> data = Arrays.asList("a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a");

    private final PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();

    private RecyclerView recyclerView;

    private View startZone;

    private View endZone;

    private boolean pagingEnabled;

    private int currentPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(data);

        View snapFab = requireViewById(view, R.id.fab_snap);
        snapFab.setOnClickListener(v -> togglePaging());

        recyclerView = requireViewById(view, R.id.recycler_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new CurrrentPositionListener());

        startZone = requireViewById(view, R.id.view_zone_start);
        startZone.setOnClickListener(v -> previousPage());

        endZone = requireViewById(view, R.id.view_zone_end);
        endZone.setOnClickListener(v -> nextPage());

        pagerSnapHelper.attachToRecyclerView(recyclerView);

        pagingEnabled = true;

        return view;
    }

    private void enablePaging() {
        startZone.setVisibility(View.VISIBLE);
        endZone.setVisibility(View.VISIBLE);
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        pagingEnabled = true;
    }

    private void disablePaging() {
        startZone.setVisibility(View.GONE);
        endZone.setVisibility(View.GONE);
        pagerSnapHelper.attachToRecyclerView(null);
        pagingEnabled = false;
    }

    private void togglePaging() {
        if (pagingEnabled) {
            disablePaging();
        } else {
            enablePaging();
        }
    }

    private void nextPage() {
        if (!pagingEnabled) return;
        if (currentPosition == data.size() - 1) return;
        recyclerView.smoothScrollToPosition(currentPosition + 1);
    }

    private void previousPage() {
        if (!pagingEnabled) return;
        if (currentPosition == 0) return;
        recyclerView.smoothScrollToPosition(currentPosition - 1);
    }

    private class CurrrentPositionListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState != SCROLL_STATE_IDLE) return;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }
}
