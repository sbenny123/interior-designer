package com.example.horizoninteriordesigner.activities;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.horizoninteriordesigner.models.Item;
import com.viro.core.ARAnchor;
import com.viro.core.ARNode;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Vector;
import com.viro.core.ViroViewARCore;


public class ItemArActivity extends Activity {

    private static final String TAG = ItemArActivity.class.getSimpleName();
    private ViroViewARCore viroView; // Used to render AR scenes using ARCore API.
    private ARScene arScene; // Allows real and virtual world to be rendered in front of camera's live feed.

    private Item item = null; // Selected item's details from collection including its Uri
    private Node itemModelNode = null; // Group node container for 3D object item, its lighting, shadow etc.
    private Node crosshairModel = null;


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
             */
            @Override
            public void onSuccess() {
                displayARScene();
            }

            /**
             * Actions to take when view has failed to initialise.
             */
            @Override
            public void onFailure(ViroViewARCore.StartupError startupError, String errorMsg) {
                Log.e(TAG, "Failed to display AR scene: " + errorMsg);
            }
        });

        setContentView(viroView); // Set's view as activity's content.
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


    /**
     * Responds to AR events like detection of anchors.
     */
    private class ARSceneListener implements ARScene.Listener {
        private boolean isInitialised;

        public ARSceneListener(Activity activity, View view) {
            isInitialised = false;
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
            if (state == ARScene.TrackingState.NORMAL && !isInitialised) {
                isInitialised = true;
            }
        }
    }


    /**
     *
     */
    private void displayARScene() {
        arScene = new ARScene();
        AmbientLight ambientLight = new AmbientLight(); // Light to illuminate node containing item

        // Add lighting to scene to make 3D object appear
        ambientLight.setColor(Color.WHITE);
        ambientLight.setIntensity(400); // Measure of brightness, 1000 is default
        ambientLight.setInfluenceBitMask(3); // Used to make light apply to a specific node
        arScene.getRootNode().addLight(ambientLight);

        // Loads the item into the scene
        load3DModel(arScene);

        viroView.setScene(arScene);
    }


    /**
     *
     * @param arScene
     */
    private void load3DModel(ARScene arScene) {

        itemModelNode = new Node();
        arScene.getRootNode().addChildNode(itemModelNode);

        // Create 3D object of item and attach to item's group node
        final Object3D itemModel = new Object3D();
        itemModelNode.addChildNode(itemModel);
        itemModelNode.setPosition(new Vector(0,-1,-1.5));
        itemModelNode.setScale(new Vector(0.9, 0.9, 0.9));


        // Load 3D model using URI
        itemModel.loadModel(viroView.getViroContext(), Uri.parse("file:///android_asset/object_lamp.vrx"), Object3D.Type.FBX, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(Object3D object3D, Object3D.Type type) {
                Log.i("Viro", "Model successfully loaded");
            }

            @Override
            public void onObject3DFailed(String error) {
                Log.e("Viro","Failed to load model: " + error);
            }
        });

        itemModelNode.addChildNode(itemModel);
    }
}

