package com.project.horizoninteriordesigner.activities.main.fragments.arView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.MainActivity;
import com.project.horizoninteriordesigner.activities.main.fragments.materialSelection.MaterialSelectionFragment;
import com.project.horizoninteriordesigner.activities.main.viewModels.ItemViewModel;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.project.horizoninteriordesigner.activities.main.MainActivity.ITEM_SELECT_TAG;
import static com.project.horizoninteriordesigner.utils.CameraUtils.generatePhotoFileName;
import static com.project.horizoninteriordesigner.utils.CameraUtils.saveBitmapToDisk;
import static com.project.horizoninteriordesigner.utils.SceneformUtils.getParentAnchorNode;


/**
 * Ar view Fragment
 *   Where models can be manipulated - enlarged, shrunk, moved, deleted etc.
 */
public class ArViewFragment extends Fragment implements View.OnClickListener,
        BaseArFragment.OnTapArPlaneListener, BaseArFragment.OnSessionInitializationListener {

    private SceneformFragment sceneformFragment; // Inner fragment which utilises Sceneform to manipulate 3D models and scenes
    private TransformableNode currentModel;
    private ItemViewModel itemViewModel;

    private static Button selectItemsBtn, takePhotoBtn;
    private FloatingActionButton showItemOptionsFab, changeDesignFab, removeItemFab;
    private TextView changeDesignText, removeItemText;
    private Boolean isAllFabsVisible;


    public ArViewFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of ArViewFragment.
     * @return a new instance of fragment ArViewFragment.
     */
    public static ArViewFragment newInstance() {
        ArViewFragment fragment = new ArViewFragment();

        return fragment;
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
     * Inflates the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ar_view, container, false);
    }


    /**
     * Initialises associated UI like the buttons and viewModel.
     * Sets viewModel to be used for shared data between the fragments.
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

        takePhotoBtn = view.findViewById(R.id.btn_take_photo);
        selectItemsBtn = view.findViewById(R.id.btn_select_items);

        showItemOptionsFab = view.findViewById(R.id.fab_show_item_options);
        changeDesignFab = view.findViewById(R.id.fab_change_item_design);
        removeItemFab = view.findViewById(R.id.fab_remove_item);

        changeDesignText = view.findViewById(R.id.text_change_item_design);
        removeItemText = view.findViewById(R.id.text_remove_item);

        isAllFabsVisible = false;

        takePhotoBtn.setOnClickListener(this);
        selectItemsBtn.setOnClickListener(this);
        showItemOptionsFab.setOnClickListener(this);
        changeDesignFab.setOnClickListener(this);
        removeItemFab.setOnClickListener(this);

        hideItemOptionButtons();
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

            // To take a photo of the camera view
            case R.id.btn_take_photo:
                takePhoto(view);
                break;

            // To show options for a selected model
            case R.id.fab_show_item_options:
                showItemOptions();
                break;

            // To change material of an item
            case R.id.fab_change_item_design:
                itemViewModel.setSelectedModelNode(currentModel);
                hideMainButtons();
                showMaterialSelectionFragment();
                break;

            // To delete an item
            case R.id.fab_remove_item:
                AnchorNode currentAnchorNode = getParentAnchorNode(currentModel);
                removeAnchorNode(currentAnchorNode);
                currentModel = null;
                break;
        }
    }


    /**
     * Opens the item selection page
     */
    private void showItemSelectionFragment() {
        ((MainActivity) getActivity()).manageFragmentTransaction(ITEM_SELECT_TAG);
    }


    private void showItemOptions() {
        if (!isAllFabsVisible) {
            removeItemFab.show();
            changeDesignFab.show();

            removeItemText.setVisibility(View.VISIBLE);
            changeDesignText.setVisibility(View.VISIBLE);

            isAllFabsVisible = true;

        } else {
            removeItemFab.hide();
            changeDesignFab.hide();

            removeItemText.setVisibility(View.GONE);
            changeDesignText.setVisibility(View.GONE);

            isAllFabsVisible = false;
        }
    }

    /**
     * Show a sliding menu with the available designs the item can be changed to
     */
    private void showMaterialSelectionFragment() {
        Fragment materialSelectionFragment = new MaterialSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_materials, materialSelectionFragment).commitNow();
    }

    /**
     * Hides the main buttons - add item, save photo and edit item buttons
     */
    private void hideMainButtons() {
        selectItemsBtn.setVisibility(View.GONE);
        takePhotoBtn.setVisibility(View.GONE);

        hideItemOptionButtons();

    }


    public static void showMainButtons() {
        selectItemsBtn.setVisibility(View.VISIBLE);
        takePhotoBtn.setVisibility(View.VISIBLE);
    }


    private void hideItemOptionButtons() {
        showItemOptionsFab.setVisibility(View.GONE);
        changeDesignFab.setVisibility(View.GONE);
        removeItemFab.setVisibility(View.GONE);
        changeDesignText.setVisibility(View.GONE);
        removeItemText.setVisibility(View.GONE);

        isAllFabsVisible = false;
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
                   e.printStackTrace();
                   return;
               }

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
            AnchorNode currentAnchorNode = getParentAnchorNode(currentModel);
            currentAnchorNode.setAnchor(anchor);

        } else {
            addModelToScene(anchor, currentRenderable.get(), currentItemId);
        }
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
            }
        });

        performModelSelectedActions(renderedModel);
    }


    @Override
    public void onSessionInitialization(Session session) {
        Scene scene = sceneformFragment.getArSceneView().getScene();

        scene.addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                TransformableNode node = (TransformableNode) hitTestResult.getNode();
                String name = node != null ? node.getName() : null;

                performModelSelectedActions((TransformableNode) hitTestResult.getNode());
            }
        });
    }

    /**
     * Actions to perform once a model may/has been selected
     *   Toggles visibility of item options button
     *   Sets the selected model as the currentModel - used for item options
     * @param selectedModel
     */
    private void performModelSelectedActions(@Nullable TransformableNode selectedModel) {

        if (selectedModel != null) {

            setCurrentModel(selectedModel);
            showItemOptionsFab.setVisibility(View.VISIBLE);

        } else {
            hideItemOptionButtons();
        }
    }

    private void setCurrentModel(TransformableNode model) {
        currentModel = model;
    }
}