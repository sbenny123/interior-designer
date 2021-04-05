package com.example.horizoninteriordesigner.activities.ArCamera;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.horizoninteriordesigner.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.ref.WeakReference;


public class ArCameraActivity extends AppCompatActivity implements BaseArFragment.OnTapArPlaneListener {
    private static final String TAG = ArCameraActivity.class.getSimpleName();
   // final public static String ITEM_KEY = "item_key";
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Renderable renderable;
    private Texture texture;

   /* private static class AnimationInstance {
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
    }*/

   /* private final Set<AnimationInstance> animators = new ArraySet<>();

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
    private int nextColor = 0;*/

    @Override
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_ar_camera);
        getSupportFragmentManager().addFragmentOnAttachListener((fragmentManager, fragment) -> {
            if (fragment.getId() == R.id.arFragment) {
                arFragment = (ArFragment) fragment;
                arFragment.setOnTapArPlaneListener(ArCameraActivity.this);
            }
        });

        if (savedInstanceState == null) {
            if (Sceneform.isSupported(this)) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.arFragment, ArFragment.class, null)
                        .commit();
            }
        }

        loadModel();
        //loadTexture();
    }

    public void loadModel() {
        WeakReference<ArCameraActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/sofa.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    ArCameraActivity activity = weakActivity.get();
                    if (activity != null) {
                        activity.renderable = renderable;
                    }
                })
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG).show();
                            return null;
                        });
    }

    public void loadTexture() {
        WeakReference<ArCameraActivity> weakActivity = new WeakReference<>(this);
        Texture.builder()
                .setSampler(Texture.Sampler.builder()
                        .setMinFilter(Texture.Sampler.MinFilter.LINEAR_MIPMAP_LINEAR)
                        .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
                        .setWrapMode(Texture.Sampler.WrapMode.REPEAT)
                        .build())
                .setSource(this, Uri.parse("textures/parquet.jpg"))
                .setUsage(Texture.Usage.COLOR)
                .build()
                .thenAccept(
                        texture -> {
                            ArCameraActivity activity = weakActivity.get();
                            if (activity != null) {
                                activity.texture = texture;
                            }
                        })
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load texture", Toast.LENGTH_LONG).show();
                            return null;
                        });
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (renderable == null) {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
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

        //RenderableInstance renderableInstance = model.getRenderableInstance();
        //renderableInstance.getMaterial().setInt("baseColorIndex", 0);
        //renderableInstance.getMaterial().setTexture("baseColorMap", texture);
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

