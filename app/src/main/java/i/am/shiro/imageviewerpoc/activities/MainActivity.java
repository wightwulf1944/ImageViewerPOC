package i.am.shiro.imageviewerpoc.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import i.am.shiro.imageviewerpoc.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new MainFragment())
                    .commit();
        }
    }
}
