package i.am.shiro.imageviewerpoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toast lastToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageRecyclerAdapter adapter = new ImageRecyclerAdapter(new ArrayList<>());

        RecyclerView imageRecycler = findViewById(R.id.recycler_image);
        imageRecycler.setHasFixedSize(true);
        imageRecycler.setAdapter(adapter);

        RecyclerViewPager recyclerViewPager = new RecyclerViewPager(imageRecycler);
        recyclerViewPager.enablePaging();

        View snapFab = findViewById(R.id.fab_snap);
        snapFab.setOnClickListener(v -> recyclerViewPager.togglePaging());

        View startZone = findViewById(R.id.view_zone_start);
        startZone.setOnClickListener(v -> invokePlaceholder("previous page"));

        View endZone = findViewById(R.id.view_zone_end);
        endZone.setOnClickListener(v -> invokePlaceholder("next page"));
    }

    private void invokePlaceholder(String name) {
        if (lastToast != null) lastToast.cancel();
        lastToast = Toast.makeText(this, name + " invoked", Toast.LENGTH_SHORT);
        lastToast.show();
    }
}
