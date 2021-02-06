package com.example.horizoninteriordesigner.activities;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.horizoninteriordesigner.models.Item;
import com.example.horizoninteriordesigner.*;
import com.viro.core.ARAnchor;
import com.viro.core.ARNode;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.DragListener;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;


public class ArCameraActivity extends Activity {

    private static final String TAG = ArCameraActivity.class.getSimpleName();
    private ViroView viroView; // Used to render AR scenes using ARCore API.
    private ARScene arScene; // Allows real and virtual world to be rendered in front of camera's live feed.

    private static final float MIN_DISTANCE = 0.2f;
    private static final float MAX_DISTANCE = 10f;

    private Item item = null; // Selected item's details from collection including its Uri
    private Node itemModelNode = null; // Group node container for 3D object item, its lighting, shadow etc.
    private Node crosshairModel = null;

    //private ARHitListener arHitTestListener = null;


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
        View.inflate(this, R.layout.item_ar_activity, ((ViewGroup) viroView)); // Shows main AR camera page
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

        add3DModel(arScene);

        viroView.setScene(arScene);
    }


    /**
     *
     */
    private void add3DModel(ARScene arScene) {

        itemModelNode = new Node();
        arScene.getRootNode().addChildNode(itemModelNode);

        final Object3D itemModel = new Object3D();
        itemModelNode.addChildNode(itemModel);
        itemModelNode.setPosition(new Vector(0, -1, -1.5));
        itemModelNode.setScale(new Vector(0.9, 0.9, 0.9));


        itemModel.setDragListener(new DragListener() {
            @Override
            public void onDrag(int source, Node node, Vector worldLocation, Vector localLocation) {

            }
        });

        // Load the Android model asynchronously.
        itemModel.loadModel(viroView.getViroContext(), Uri.parse("file:///android_asset/object_lamp.vrx"), Object3D.Type.FBX, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {
                Log.i("Viro", "Model successfully loaded");
            }

            @Override
            public void onObject3DFailed(String error) {
                Log.e("Viro", "Failed to lo" +
                        "ad model: " + error);
            }
        });

        // Make the item draggable
        itemModel.setDragType(Node.DragType.FIXED_TO_WORLD);

        itemModelNode.addChildNode(itemModel);
    }
}

