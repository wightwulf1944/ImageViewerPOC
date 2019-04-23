package i.am.shiro.imageviewerpoc.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.Collections;
import java.util.List;

import i.am.shiro.imageviewerpoc.PrefsMockup;
import i.am.shiro.imageviewerpoc.R;
import i.am.shiro.imageviewerpoc.adapters.ImageRecyclerAdapter.ViewHolder;

public final class ImageRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    // TODO : SubsamplingScaleImageView does _not_ support animated GIFs -> use pl.droidsonroids.gif:android-gif-drawable when serving a GIF ?

    private View.OnTouchListener itemTouchListener;

    private List<String> imageUris;

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void setImageUris(List<String> imageUris) {
        this.imageUris = Collections.unmodifiableList(imageUris);
    }

    public void setItemTouchListener(View.OnTouchListener itemTouchListener) {
        this.itemTouchListener = itemTouchListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_viewer_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int pos) {
        viewHolder.setImageUri(imageUris.get(pos));
    }

    final class ViewHolder extends RecyclerView.ViewHolder {

        private final SubsamplingScaleImageView imgView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = (SubsamplingScaleImageView) itemView;
            imgView.setOnTouchListener(itemTouchListener);
        }

        void setImageUri(String uri) {
            imgView.recycle();
            imgView.setMinimumScaleType(getScaleType());
            imgView.setImage(ImageSource.uri(uri));
        }

        private int getScaleType() {
            if (PrefsMockup.Constant.PREF_VIEWER_DISPLAY_FILL == PrefsMockup.getViewerResizeMode()) {
                return SubsamplingScaleImageView.SCALE_TYPE_START;
            } else {
                return SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE;
            }
        }
    }
}
