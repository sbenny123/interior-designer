package com.example.horizoninteriordesigner.main;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.horizoninteriordesigner.model.Item;
import com.viro.core.ARAnchor;
import com.viro.core.ARHitTestListener;
import com.viro.core.ARHitTestResult;
import com.viro.core.ARNode;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.AnimationTimingFunction;
import com.viro.core.AnimationTransaction;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.DirectionalLight;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Quad;
import com.viro.core.Spotlight;
import com.viro.core.Surface;
import com.viro.core.Text;
import com.viro.core.Vector;
import com.viro.core.ViroContext;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import java.util.Arrays;


public class ItemARActivity extends Activity {

    private static final String TAG = ItemARActivity.class.getSimpleName();
    protected ViroView viroView; // Used to render AR scenes using ARCore API.
    private ARScene arScene; // Allows real and virtual world to be rendered in front of camera's live feed.

    @Override
    /**
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viroView = new ViroViewARCore(this, new ViroViewARCore.StartupListener() {
            /**
             * Actions to take when ARCore has been installed and initialised,
             * and the rendering surface has been created.
             *
             */
            @Override
            public void onSuccess() {
                arScene = new ARScene();

                ARSceneListener arSceneListener = new ARSceneListener(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                arScene.setListener(arSceneListener);

                viroView.setScene(arScene);
            }

            /**
             * Actions to take when view has failed to initialise.
             * @param startupError
             * @param s
             */
            @Override
            public void onFailure(ViroViewARCore.StartupError startupError, String s) {

            }
        });

        setContentView(viroView); // Set's view as activity's content.
    }


    /**
     * Responds to AR events like detection of anchors.
     */
    private class ARSceneListener implements ARScene.Listener {
        private Runnable mOnTrackingInitializedRunnable;
        private boolean mInitialized;

        public ARSceneListener(Runnable onTrackingInitializedRunnable) {
            mOnTrackingInitializedRunnable = onTrackingInitializedRunnable;
            mInitialized = false;
        }

        @Override
        public void onAmbientLightUpdate(float intensity, Vector color) {
            // no-op
        }

        @Override
        public void onAnchorFound(ARAnchor anchor, ARNode arNode) {
            // no-op
        }

        @Override
        public void onAnchorRemoved(ARAnchor anchor, ARNode arNode) {
            // no-op
        }

        @Override
        public void onAnchorUpdated(ARAnchor anchor, ARNode arNode) {
            // no-op
        }

        @Override
        public void onTrackingInitialized() {
            // this method is deprecated.
        }

        @Override
        public void onTrackingUpdated(ARScene.TrackingState state, ARScene.TrackingStateReason reason) {
            if (state == ARScene.TrackingState.NORMAL && !mInitialized) {
                mInitialized = true;
                if (mOnTrackingInitializedRunnable != null) {
                    mOnTrackingInitializedRunnable.run();
                }
            }
        }

    }




    private Object3D load3DModel() {
        final Object3D itemModel = new Object3D();

        // Load 3D model using URI
       itemModel.loadModel(viroView.getViroContext(), Uri.parse("file:///android_asset/object_lamp.vrx"), Object3D.Type.FBX, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(Object3D object3D, Object3D.Type type) {
                Log.i("Viro", "Model success");
            }

            @Override
            public void onObject3DFailed(String error) {
                Log.e("Viro","Model load failed : " + error);
            }
        });

       return itemModel;
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

