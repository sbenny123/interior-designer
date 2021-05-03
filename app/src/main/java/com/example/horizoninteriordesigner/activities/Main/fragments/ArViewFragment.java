package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.activities.Main.viewModels.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.ITEM_SELECT_TAG;
import static com.example.horizoninteriordesigner.utils.CameraUtils.*;


/**
 * Ar view Fragment
 *   Where models can be manipulated - enlarged, shrunk, moved, deleted etc.
 */
public class ArViewFragment extends Fragment implements View.OnClickListener,
        BaseArFragment.OnTapArPlaneListener, BaseArFragment.OnSessionInitializationListener {

    private SceneformFragment sceneformFragment; // Inner fragment which utilises Sceneform to manipulate 3D models and scenes
    private TransformableNode currentModel;
    private ItemViewModel itemViewModel;
    //private Renderable renderable;


    public ArViewFragment() {
        // Required empty public constructor
    }


    /**
     * Initialises sceneform which is used to manipulate models and scenes in AR
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up sceneform fragment
        sceneformFragment = new SceneformFragment();
        sceneformFragment.setOnTapArPlaneListener(this::onTapPlane);
        sceneformFragment.setOnSessionInitializationListener(this::onSessionInitialization);

        // Create transaction to add the fragment to ArViewFragment
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_ar, sceneformFragment).commit();
    }


    /**
     * Inflates view for fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ar_view, container, false);
    }


    /**
     * Initialises associated UI like the buttons and viewModel
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Adds on click listeners to the buttons
        initialiseButtons(view);

        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
    }


    /**
     * Adds on click listeners to each of the buttons avaiable
     */
    private void initialiseButtons(View view) {

        FloatingActionButton takePhotoBtn = view.findViewById(R.id.btn_take_photo);
        FloatingActionButton selectItemsBtn = view.findViewById(R.id.btn_select_items);
        FloatingActionButton showItemOptionsBtn = view.findViewById(R.id.btn_show_item_options);

        takePhotoBtn.setOnClickListener(this);
        selectItemsBtn.setOnClickListener(this);
        showItemOptionsBtn.setOnClickListener(this);
    }


    /**
     * Specifies actions to take when a button has been pressed
     * All the actions for all the buttons are grouped into this method.
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            // To open item selection fragment
            case R.id.btn_select_items:
                currentModel = null;
                showItemSelectionFragment();
                break;

             // To show options for a selected model
            case R.id.btn_show_item_options:
                showItemOptionsPopup(view);
                break;

            // To take a photo of the camera view
            case R.id.btn_take_photo:
                takePhoto(view);
                break;
        }
    }


    /**
     * Opens the item selection page
     */
    private void showItemSelectionFragment() {
        ((MainActivity) getActivity()).manageFragmentTransaction(ITEM_SELECT_TAG);
    }


    /**
     * Creates and shows a pop-up menu with all the available options for the model
     * Includes:
     *   Changing model's materials
     *   Model deletion
     */
    private void showItemOptionsPopup(View view) {

        // Create pop-up menu
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_model_options, popupMenu.getMenu());

        // onClick listener for when an option has been selected
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    // Change model's design
                    case R.id.item_change_design:
                        showMaterialSelectionFragment();
                        break;

                    // Remove model from scene
                    case R.id.item_remove_item:
                        AnchorNode currentAnchorNode = setAnchorNode(currentModel);
                        removeAnchorNode(currentAnchorNode);
                        currentModel = null;
                        break;

                    default:
                        return false;
                }

                return true;
            }
        });

        popupMenu.show();
    }


    /**
     * Show a sliding menu with the available designs the item can be changed to
     */
    private void showMaterialSelectionFragment() {
        itemViewModel.setModelNode(currentModel);

        Fragment materialSelectionFragment = new MaterialSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_materials, materialSelectionFragment).commit();
    }


    /**
     *
     * @param nodeToRemove
     */
    private void removeAnchorNode(AnchorNode nodeToRemove) {
        if (nodeToRemove != null) {
            sceneformFragment.getArSceneView().getScene().removeChild(nodeToRemove);
            nodeToRemove.getAnchor().detach();
            nodeToRemove.setParent(null);
        }
    }


    /**
     * Uses PixelCopy API to take a screenshot of the ArSceneView - scene with camera view and models.
     *
     */
    private void takePhoto(View v) {
        final String filename = generatePhotoFileName();
        ArSceneView view = sceneformFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);

                } catch (IOException e) {
                    Toast toast = Toast.makeText(getContext(), e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                Snackbar snackbar = Snackbar.make(v.findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("Open in Photos", view1 -> {
                    File photoFile = new File(filename);

                    Uri photoURI = FileProvider.getUriForFile(getContext(),
                            getActivity().getPackageName() + ".ar.name.provider",
                            photoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                });
                snackbar.show();
            } else {
                Toast toast = Toast.makeText(getContext(),
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

        AtomicReference<Renderable> currentRenderable = new AtomicReference<>();
        String currentItemId = itemViewModel.getItemId();


        itemViewModel.getRenderableToAdd().observe(getViewLifecycleOwner(), renderable -> {
            currentRenderable.set(renderable);
        });


        if (currentRenderable.get() == null) {
            Toast.makeText(getActivity(), "Select a model", Toast.LENGTH_SHORT).show();
            return;
        }

        Anchor anchor = hitResult.createAnchor();

        if (currentModel != null) {
            AnchorNode currentAnchorNode = setAnchorNode(currentModel);
            currentAnchorNode.setAnchor(anchor);

        } else {
            addModelToScene(anchor, currentRenderable.get(), currentItemId);
        }
        /*else {
            AnchorNode newAnchorNode = new AnchorNode(anchor);

            newAnchorNode.setParent(sceneformFragment.getArSceneView().getScene());

            // Create the transformable model and add it to the anchor.
            TransformableNode newModel = new TransformableNode(sceneformFragment.getTransformationSystem());

            newModel.setParent(newAnchorNode);
            newModel.setRenderable(renderable);
            newModel.select();
            newModel.setOnTapListener(new Node.OnTapListener() {
                @Override
                public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                    TransformableNode selectedModel = (TransformableNode) hitTestResult.getNode();
                    AnchorNode selectedAnchorNode = (AnchorNode) selectedModel.getParent();
                    Renderable selectedRenderable = selectedModel.getRenderable();

                    currentModel = selectedModel;

                 Texture.builder()
                            .setSource(getActivity(), R.drawable.hexagon_wood)
                            .build()
                            .thenAccept(texture -> {
                                selectedModel.getRenderable().getMaterial().setTexture("baseColorMap", texture);
                            });


                   Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.parquet);

                    TextureSampler textureSampler = new TextureSampler();

                    Engine engine = Engine.create();
                    Texture texture = new Texture.Builder()
                            .width(bitmap.getWidth())
                            .height(bitmap.getHeight())
                            .sampler(Texture.Sampler.SAMPLER_3D)
                            .format(Texture.InternalFormat.RGBA8)
                            .build(engine);

                    for (MaterialInstance materialInstance : materialInstances) {
                        Material material = materialInstance.getMaterial();
                        materialInstance.setParameter("baseColor", texture, textureSampler);
                        //materialInstance.setParameter("baseColorFactor", 0.3f, 0.5f, 0.7f); // Values for Red, Green and Blue
                    }
                }
            });


            currentModel = newModel;
        }*/
    }


    private AnchorNode setAnchorNode (TransformableNode model) {
        AnchorNode anchorNode = (AnchorNode) model.getParent();

        return anchorNode;
    }


    private void addModelToScene(Anchor anchor, Renderable modelToRender, String itemId) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(sceneformFragment.getArSceneView().getScene());
        anchorNode.setName(itemId);

        // Create the transformable model and add it to the anchor.
        TransformableNode renderedModel = new TransformableNode(sceneformFragment.getTransformationSystem());

        renderedModel.setParent(anchorNode);
        renderedModel.setRenderable(modelToRender);
        renderedModel.select();
        renderedModel.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                TransformableNode selectedModel = (TransformableNode) hitTestResult.getNode();
                AnchorNode selectedAnchorNode = (AnchorNode) selectedModel.getParent();
                Renderable selectedRenderable = selectedModel.getRenderable();

                currentModel = selectedModel;
            }
        });

        currentModel = renderedModel;
    }


    @Override
    public void onSessionInitialization(Session session) {
        Scene scene = sceneformFragment.getArSceneView().getScene();
        scene.addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if (hitTestResult.getNode() != null) {
                    //currentAnchorNode = (AnchorNode) hitTestResult.getNode().getParent();
                }
            }
        });

    }
}