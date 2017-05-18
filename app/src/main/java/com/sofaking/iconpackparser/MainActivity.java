package com.sofaking.iconpackparser;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sofaking.iconpack.IconPack;
import com.sofaking.iconpack.IconPackManager;

public class MainActivity extends AppCompatActivity {

    private IconPackManager mIconPackManager;
    private IconPack mIconPack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIconPackManager = ((App) getApplicationContext()).getIconPackManager();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mIconPackManager.loadInstalledIconPacksAsync(getApplicationContext(), new IconPackManager.Listener() {
            @Override
            public void onIconPacksLoaded() {

                mIconPack = mIconPackManager.getInstalledIconPack("com.example.iconpack");

                mIconPack.initAppFilterAsync(true, new IconPack.AppFilterListener() {
                    @Override
                    public void onAppFilterLoaded() {

                        ComponentName component = new ComponentName("com.app.example", ".ExampleActivity");

                        Drawable icon = mIconPack.getDefaultIconForPackage(MainActivity.this, component, true);


                    }

                    @Override
                    public void onLoadingFailed(Exception e) {

                    }
                });


            }
        });

    }
}
