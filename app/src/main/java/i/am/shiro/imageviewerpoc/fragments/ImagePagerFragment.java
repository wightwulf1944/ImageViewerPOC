package i.am.shiro.imageviewerpoc.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import i.am.shiro.imageviewerpoc.PagerController;
import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter;
import i.am.shiro.imageviewerpoc.viewmodels.ImageViewerViewModel;

import static android.support.v4.view.ViewCompat.requireViewById;

/**
 * TODO
 * <p>
 */
public class ImagePagerFragment extends Fragment {

    private PagerController pagerController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_image_viewer, container, false);

        ImageRecyclerAdapter adapter = new ImageRecyclerAdapter();

        RecyclerView recyclerView = requireViewById(view, R.id.recycler_image);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        pagerController = new PagerController(recyclerView);

        ViewModelProviders.of(requireActivity())
                .get(ImageViewerViewModel.class)
                .getImages()
                .observe(this, adapter::setImageUris);

        return view;
    }
}
