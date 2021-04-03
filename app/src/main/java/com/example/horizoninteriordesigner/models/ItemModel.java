package com.example.horizoninteriordesigner.models;

import android.net.Uri;


/**
 * 3D model using ViroCore including dragging, zoom etc.
 */
public class ItemModel {
    private final Uri modelUri;
    //private ViroView viroView;
   // private ARScene arScene;
    //private Node itemModelNode = null; // Group node container for 3D object item, its lighting, shadow etc.
    private float scaleStart;

    //public ItemModel(Uri modelUri, ViroView viroView, ARScene arScene) {
    public ItemModel(Uri modelUri) {
        this.modelUri = modelUri;
        //this.viroView = viroView;
        //this.arScene = arScene;
    }

  /*  public void add3DModel(ViroView viroView, ARScene arScene) {
        itemModelNode = new Node();
        arScene.getRootNode().addChildNode(itemModelNode);

        final Object3D itemModel = new Object3D();
        itemModelNode.addChildNode(itemModel);
        itemModelNode.setPosition(new Vector(0, -1, -1.5));
        itemModelNode.setScale(new Vector(1, 1, 1));


        itemModel.setGesturePinchListener(new GesturePinchListener() {
            @Override
            public void onPinch(int i, Node node, float scale, PinchState pinchState) {
                if(pinchState == PinchState.PINCH_START) {
                    scaleStart = itemModel.getScaleRealtime().x;
                } else {
                    itemModel.setScale(new Vector(scaleStart * scale, scaleStart * scale, scaleStart * scale));
                }
            }
        });

        itemModel.setDragListener(new DragListener() {
            @Override
            public void onDrag(int source, Node node, Vector worldLocation, Vector localLocation) {
                // No-op
            }
        });

        // Load the Android model asynchronously.
        itemModel.loadModel(viroView.getViroContext(), modelUri, Object3D.Type.GLTF, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {
                Log.i("Viro", "Model successfully loaded");
            }

            @Override
            public void onObject3DFailed(String error) {
                Log.e("Viro", "Failed to load model: " + error);
            }
        });

        // Make the item draggable
        itemModel.setDragType(Node.DragType.FIXED_TO_WORLD);

        itemModelNode.addChildNode(itemModel);
    }*/
}
