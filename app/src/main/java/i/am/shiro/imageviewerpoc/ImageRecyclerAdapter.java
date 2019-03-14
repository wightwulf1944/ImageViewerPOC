package i.am.shiro.imageviewerpoc;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import i.am.shiro.imageviewerpoc.ImageRecyclerAdapter.ViewHolder;

public final class ImageRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final List<String> imageUris;

    public ImageRecyclerAdapter(List<String> imageUris) {
        this.imageUris = Collections.unmodifiableList(imageUris);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_image_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.pageText.setText(String.valueOf(i));
    }

    final class ViewHolder extends RecyclerView.ViewHolder {

        private TextView pageText;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            pageText = itemView.findViewById(R.id.text_page);
        }
    }
}
