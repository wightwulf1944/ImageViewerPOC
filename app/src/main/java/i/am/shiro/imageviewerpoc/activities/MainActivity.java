package i.am.shiro.imageviewerpoc.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import i.am.shiro.imageviewerpoc.fragments.ImagePagerFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new ImagePagerFragment())
                    .commit();
        }
    }
}
