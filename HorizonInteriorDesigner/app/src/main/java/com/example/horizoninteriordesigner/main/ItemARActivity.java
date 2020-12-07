package com.example.horizoninteriordesigner.main;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.viro.core.ARAnchor;
import com.viro.core.ARNode;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Object3D;
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import java.lang.ref.WeakReference;


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

                load3DModel();

                ARSceneListener arSceneListener = new ARSceneListener(ItemARActivity.this, viroView);
                arScene.setListener(arSceneListener);

                // Add a light to the scene so our models show up
                arScene.getRootNode().addLight(new AmbientLight(Color.WHITE, 1000f));

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
        private WeakReference<Activity> mCurrentActivityWeak;
        private boolean mInitialized;

        public ARSceneListener(Activity activity, View view) {
            mCurrentActivityWeak = new WeakReference<Activity>(activity);
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
                Activity activity = mCurrentActivityWeak.get();
                if (activity == null) {
                    return;
                }

                mInitialized = true;
            }
        }

    }




    private void load3DModel() {
        Object3D itemModel = new Object3D();

        // Load 3D model using URI
       itemModel.loadModel(viroView.getViroContext(), Uri.parse("file:///android_asset/Couch.obj"), Object3D.Type.OBJ, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(Object3D object3D, Object3D.Type type) {
                Log.i("Viro", "Model successfully loaded");
            }

            @Override
            public void onObject3DFailed(String error) {
                Log.e("Viro","Failed to load model: " + error);
            }
        });

       arScene.getRootNode().addChildNode(itemModel);
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

