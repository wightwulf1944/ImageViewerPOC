package i.am.shiro.imageviewerpoc.util;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BundleManager {

    private static final String KEY_URIS = "uris";
    private static final String KEY_URIS_STR = "urisStr";

    private final Bundle bundle;

    public BundleManager(Bundle bundle) {
        this.bundle = bundle;
    }

    public BundleManager() {
        this.bundle = new Bundle();
    }


    public Bundle getBundle() {
        return bundle;
    }

    public void setUris(Uri... uris) {
        ArrayList<String> uriList = new ArrayList<>();
        for (Uri uri : uris) uriList.add(uri.toString());

        bundle.putStringArrayList(KEY_URIS, uriList);
    }

    public List<Uri> getUris() {
        List<Uri> result = new ArrayList<>();

        List<String> uriList = bundle.getStringArrayList(KEY_URIS);
        if (null != uriList && !uriList.isEmpty())
            for (String s : uriList) result.add(Uri.parse(s));

        return result;
    }

    public void setUrisStr(List<String> uris) {
        ArrayList<String> uriList = new ArrayList<>(uris);
        bundle.putStringArrayList(KEY_URIS_STR, uriList);
    }

    @Nullable
    public List<String> getUrisStr() {
        return bundle.getStringArrayList(KEY_URIS_STR);
    }

}
