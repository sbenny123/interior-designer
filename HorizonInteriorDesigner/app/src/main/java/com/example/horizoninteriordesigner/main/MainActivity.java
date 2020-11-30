package com.example.horizoninteriordesigner.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.viro.core.ARAnchor;
import com.viro.core.ARNode;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.Vector;
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

                SampleARSceneListener arSceneListener = new SampleARSceneListener(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                arScene.setListener(arSceneListener);
                viroView.setScene(arScene);
            }

            @Override
            public void onFailure(ViroViewARCore.StartupError startupError, String s) {

            }
        });
    }

    // You can use the ARSceneListener to respond to AR events, including the detection of
    // anchors
    private class SampleARSceneListener implements ARScene.Listener {
        private Runnable mOnTrackingInitializedRunnable;
        private boolean mInitialized;
        public SampleARSceneListener(Runnable onTrackingInitializedRunnable) {
            mOnTrackingInitializedRunnable = onTrackingInitializedRunnable;
            mInitialized = false;
        }

        @Override
        public void onTrackingUpdated(ARScene.TrackingState trackingState,
                                      ARScene.TrackingStateReason trackingStateReason) {
            if (trackingState == ARScene.TrackingState.NORMAL && !mInitialized) {
                mInitialized = true;
                if (mOnTrackingInitializedRunnable != null) {
                    mOnTrackingInitializedRunnable.run();
                }
            }
        }

        @Override
        public void onTrackingInitialized() {
            // this method is deprecated.
        }

        @Override
        public void onAmbientLightUpdate(float lightIntensity, Vector color) {
            // no-op
        }

        @Override
        public void onAnchorFound(ARAnchor anchor, ARNode arNode) {
            // no-op
        }

        @Override
        public void onAnchorUpdated(ARAnchor anchor, ARNode arNode) {
            // no-op
        }

        @Override
        public void onAnchorRemoved(ARAnchor anchor, ARNode arNode) {
            // no-op
        }
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

