package com.example.horizoninteriordesigner.main;

import android.app.Activity;
import android.os.Bundle;

import com.viro.core.ARScene;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected ViroView viroView; // Used to render 3D content
    private ARScene arScene; // Allows real and virtual world to be rendered on the camera

    @Override
    /**
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viroView = new ViroViewARCore(this, new ViroViewARCore.StartupListener() {
            @Override
            public void onSuccess() {
                arScene = new ARScene();
            }

            @Override
            public void onFailure(ViroViewARCore.StartupError startupError, String s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viroView.onActivityStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viroView.onActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viroView.onActivityPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        viroView.onActivityStopped(this);
    }
}

