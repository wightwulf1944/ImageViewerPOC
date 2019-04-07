package i.am.shiro.imageviewerpoc.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class ImageViewerViewModel extends AndroidViewModel {

    private final MutableLiveData<List<String>> images = new MutableLiveData<>();


    // === INIT METHODS

    public ImageViewerViewModel(@NonNull Application application) {
        super(application);
        images.setValue(new ArrayList<>());
    }

    @NonNull
    public LiveData<List<String>> getImages() {
        return images;
    }


    // === VERB METHODS

    public void setImages(List<String> imgs) {
        images.setValue(imgs);
    }

    @Override
    protected void onCleared() {
        // Put Rx disposable code here
        super.onCleared();
    }
}
