package com.example.horizoninteriordesigner.activities.ArCamera;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.horizoninteriordesigner.R;
import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;


public class ArCameraActivity extends AppCompatActivity {
    private static final String TAG = ArCameraActivity.class.getSimpleName();
   // final public static String ITEM_KEY = "item_key";
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Renderable renderable;

    private static class AnimationInstance {
        Animator animator;
        Long startTime;
        float duration;
        int index;

        AnimationInstance(Animator animator, int index, Long startTime) {
            this.animator = animator;
            this.startTime = startTime;
            this.duration = animator.getAnimationDuration(index);
            this.index = index;
        }
    }

    private final Set<AnimationInstance> animators = new ArraySet<>();

    private final List<Color> colors =
            Arrays.asList(
                    new Color(0, 0, 0, 1),
                    new Color(1, 0, 0, 1),
                    new Color(0, 1, 0, 1),
                    new Color(0, 0, 1, 1),
                    new Color(1, 1, 0, 1),
                    new Color(0, 1, 1, 1),
                    new Color(1, 0, 1, 1),
                    new Color(1, 1, 1, 1));
    private int nextColor = 0;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    /*if (!checkIsSupportedDeviceOrFinish(this)) {
      return;
    }*/

        setContentView(R.layout.activity_ar_camera);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        WeakReference<ArCameraActivity> weakActivity = new WeakReference<>(this);

        ModelRenderable.builder()
                .setSource(
                        this,
                        Uri.parse(
                                "https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(
                        modelRenderable -> {
                            Toast toast =
                                    Toast.makeText(this, "Model rendered", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                            ArCameraActivity activity = weakActivity.get();
                            if (activity != null) {
                                activity.renderable = modelRenderable;
                            }
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast1 =
                                    Toast.makeText(this, "Unable to load Tiger renderable", Toast.LENGTH_LONG);
                            toast1.setGravity(Gravity.CENTER, 0, 0);
                            toast1.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (renderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable model and add it to the anchor.
                    TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
                    model.setParent(anchorNode);
                    model.setRenderable(renderable);
                    model.select();

                    FilamentAsset filamentAsset = model.getRenderableInstance().getFilamentAsset();
                    if (filamentAsset.getAnimator().getAnimationCount() > 0) {
                        animators.add(new AnimationInstance(filamentAsset.getAnimator(), 0, System.nanoTime()));
                    }

                    Color color = colors.get(nextColor);
                    nextColor++;
                    for (int i = 0; i < renderable.getSubmeshCount(); ++i) {
                        Material material = renderable.getMaterial(i);
                        material.setFloat4("baseColorFactor", color);
                    }

                    Node tigerTitleNode = new Node();
                    tigerTitleNode.setParent(model);
                    tigerTitleNode.setEnabled(false);
                    tigerTitleNode.setLocalPosition(new Vector3(0.0f, 1.0f, 0.0f));
                    ViewRenderable.builder()
                            .setView(this, R.layout.tiger_card_view)
                            .build()
                            .thenAccept(
                                    (renderable) -> {
                                        tigerTitleNode.setRenderable(renderable);
                                        tigerTitleNode.setEnabled(true);
                                    })
                            .exceptionally(
                                    (throwable) -> {
                                        throw new AssertionError("Could not load card view.", throwable);
                                    }
                            );
                });

        arFragment
                .getArSceneView()
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            Long time = System.nanoTime();
                            for (AnimationInstance animator : animators) {
                                animator.animator.applyAnimation(
                                        animator.index,
                                        (float) ((time - animator.startTime) / (double) SECONDS.toNanos(1))
                                                % animator.duration);
                                animator.animator.updateBoneMatrices();
                            }
                        });
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    /*private static final String TAG = ArCameraActivity.class.getSimpleName();
    final public static String ITEM_KEY = "item_key";

    private ArFragment arFragment;
    private Renderable renderable;*/

   // private ViroView viroView; // Used to render AR scenes using ARCore API.
   // private ARScene arScene; // Allows real and virtual world to be rendered in front of camera's live feed.

    //private List<ItemModel> itemModelsList;
    //private Item selectedItem; // Selected item's details from collection including its Uri

  //  @Override
   // @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    /**
     *
     */
   /* protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);*/

        //ItemDbApplication itemDbApplication = (ItemDbApplication)this.getApplication();
        //itemModelsList = itemDbApplication.getItemModelDB();

        /*viroView = new ViroViewARCore(this, new ViroViewARCore.StartupListener() {
            /**
             * Actions to take when ARCore has been installed and initialised,
             * and the rendering surface has been created.
             */
            /*@Override
            public void onSuccess() {
                displayARScene();
            }*/

            /**
             * Actions to take when view has failed to initialise.
             */
            /*@Override
            public void onFailure(ViroViewARCore.StartupError startupError, String errorMsg) {
                Log.e(TAG, "Failed to display AR scene: " + errorMsg);
            }
        });

        setContentView(viroView); // Set's view as activity's content.

        // Show main AR camera page
        View.inflate(this, R.layout.activity_ar_camera, ((ViewGroup) viroView)); */

      /*  setContentView(R.layout.activity_ar_camera);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        WeakReference<ArCameraActivity> weakActivity = new WeakReference<>(this);

        ModelRenderable.builder()
                .setSource(
                        this,
                        Uri.parse(
                                "file:///android_asset/CHAHIN_WOODEN_CHAIR.gltf"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(
                        modelRenderable -> {
                            ArCameraActivity activity = weakActivity.get();
                            if (activity != null) {
                                activity.renderable = modelRenderable;
                            }
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load chair renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (renderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable model and add it to the anchor.
                    TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
                    model.setParent(anchorNode);
                    model.setRenderable(renderable);
                    model.select();
                });
    }*/

    /**
     *
     */
   /* private void displayARScene() {
        arScene = new ARScene();

        AmbientLight ambientLight = new AmbientLight(); // Light to illuminate node containing item

        // Add lighting to scene to make 3D object appear
        ambientLight.setColor(Color.WHITE);
        ambientLight.setIntensity(400); // Measure of brightness, 1000 is default
        ambientLight.setInfluenceBitMask(3); // Used to make light apply to a specific node
        arScene.getRootNode().addLight(ambientLight);

        addNewModel(arScene);
        initialiseButtons();

        viroView.setScene(arScene);
    }*/


    /**
     *
     */
    /*private void addNewModel(ARScene arScene) {
        Intent intent = getIntent();
        String itemId = intent.getStringExtra(ITEM_KEY);

        if (itemId != null && !itemId.isEmpty()) {
            ItemDbApplication itemDbApplication = (ItemDbApplication)this.getApplication();
            ItemDB itemDB = itemDbApplication.getItemDB();

            selectedItem = itemDB.getItemById(itemId);

            ItemModel itemModel = new ItemModel(selectedItem.getUri());
            itemModel.add3DModel(viroView, arScene);
            itemModelsList.add(itemModel);
        }
    }*/


    /**
     *
     */
    /*private void initialiseButtons() {
        FloatingActionButton selectItemsBtn = findViewById(R.id.btn_select_items);

        selectItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchItemSelectActivity();
            }
        });
    }*/

    /**
     *
     */
    /*private void launchItemSelectActivity() {
        Intent intent = new Intent(this, ItemSelectionActivity.class);
        startActivity(intent);
    }*/

    /**
     *
     */
    /*private void add3DModel(ARScene arScene) {
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
    }*/
}

