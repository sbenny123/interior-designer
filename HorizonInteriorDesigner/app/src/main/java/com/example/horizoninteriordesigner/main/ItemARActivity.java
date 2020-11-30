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

                arScene.setListener(new ARScene.Listener() {
                                        @Override
                                        public void onTrackingInitialized() {

                                        }

                                        @Override
                                        public void onTrackingUpdated(ARScene.TrackingState trackingState, ARScene.TrackingStateReason trackingStateReason) {

                                        }

                                        @Override
                                        public void onAmbientLightUpdate(float v, Vector vector) {

                                        }

                                        @Override
                                        public void onAnchorFound(ARAnchor arAnchor, ARNode arNode) {
                                            if (arAnchor.getType() == ARAnchor.Type.PLANE) {
                                                DirectionalLight light = new DirectionalLight();
                                                light.setColor(Color.WHITE);
                                                light.setDirection(new Vector(0, -1, 0));
                                                light.setShadowOrthographicPosition(new Vector(0, 4, 0));
                                                light.setShadowOrthographicSize(10);
                                                light.setShadowNearZ(1);
                                                light.setShadowFarZ(4);
                                                light.setCastsShadow(true);

                                                Object3D itemModel = load3DModel();

                                                Material material = new Material();
                                                material.setLightingModel(Material.LightingModel.LAMBERT);
                                                material.setShadowMode(Material.ShadowMode.TRANSPARENT);

                                                Quad quad = new Quad(2, 2);
                                                quad.setMaterials(Arrays.asList(material));

                                                Node quadNode = new Node();
                                                quadNode.setPosition(new Vector(0, -0.1, 0));
                                                quadNode.setRotation(new Vector((float) -Math.PI / 2.0f, 0, 0));
                                                quadNode.setGeometry(quad);

                                                arNode.addLight(light);
                                                arNode.addChildNode(itemModel);
                                            }


                                        }

                                        @Override
                                        public void onAnchorUpdated(ARAnchor arAnchor, ARNode arNode) {

                                        }

                                        @Override
                                        public void onAnchorRemoved(ARAnchor arAnchor, ARNode arNode) {

                                        }
                                    });
              /*  SampleARSceneListener arSceneListener = new SampleARSceneListener(new Runnable() {
                    @Override
                    public void run() {
                    }
                });*/
              //  arScene.setListener(arSceneListener);

                //AmbientLight mMainLight = new AmbientLight(Color.WHITE, 1000.0f);
                //arScene.getRootNode().addLight(mMainLight);



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

