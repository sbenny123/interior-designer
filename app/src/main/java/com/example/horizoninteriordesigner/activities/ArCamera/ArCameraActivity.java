package com.example.horizoninteriordesigner.activities.ArCamera;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.horizoninteriordesigner.activities.ItemSelection.ItemSelectionActivity;
import com.example.horizoninteriordesigner.models.Item;
import com.example.horizoninteriordesigner.*;
import com.example.horizoninteriordesigner.models.ItemDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.viro.core.ARScene;
import com.viro.core.AmbientLight;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.DragListener;
import com.viro.core.GestureRotateListener;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.RotateState;
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;


public class ArCameraActivity extends AppCompatActivity {

    private static final String TAG = ArCameraActivity.class.getSimpleName();
    final public static String ITEM_KEY = "item_key";

    private ViroView viroView; // Used to render AR scenes using ARCore API.
    private ARScene arScene; // Allows real and virtual world to be rendered in front of camera's live feed.

    private static final float MIN_DISTANCE = 0.2f;
    private static final float MAX_DISTANCE = 10f;

    private Item selectedItem; // Selected item's details from collection including its Uri
    private Node itemModelNode = null; // Group node container for 3D object item, its lighting, shadow etc.


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

        Intent intent = getIntent();
        String itemId = intent.getStringExtra(ITEM_KEY);

        if (itemId != null && !itemId.isEmpty()) {
            ItemDbApplication itemDbApplication = (ItemDbApplication)this.getApplication();
            ItemDB itemDB = itemDbApplication.getItemDB();

            selectedItem = itemDB.getItemById(itemId);

            Toast.makeText(this, "Selected item is " + selectedItem.getName(), Toast.LENGTH_SHORT).show();
        }

        // Show main AR camera page
        View.inflate(this, R.layout.activity_ar_camera, ((ViewGroup) viroView));
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

        if (selectedItem != null) {
            add3DModel(arScene);
        }

        initialiseButtons();


        viroView.setScene(arScene);
    }


    /**
     *
     */
    private void initialiseButtons() {
        FloatingActionButton selectItemsBtn = findViewById(R.id.btn_select_items);

        selectItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchItemSelectActivity();
            }
        });
    }

    /**
     *
     */
    private void launchItemSelectActivity() {
        Intent intent = new Intent(this, ItemSelectionActivity.class);
        startActivity(intent);
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
        itemModelNode.setScale(new Vector(0.1, 0.1, 0.1));


        itemModel.setDragListener(new DragListener() {
            @Override
            public void onDrag(int source, Node node, Vector worldLocation, Vector localLocation) {
                // No-op
            }
        });

        // Load the Android model asynchronously.
        itemModel.loadModel(viroView.getViroContext(), selectedItem.getUri(), Object3D.Type.OBJ, new AsyncObject3DListener() {
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
    }
}

