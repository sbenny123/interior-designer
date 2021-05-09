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
 * Fragment to handle the Sceneform actions:
 *   - Handles manipulation of models - enlarged, shrunk, moved, deleted etc.
 *   - Takes photos of the scene.
 */
public class ArViewFragment extends Fragment implements View.OnClickListener,
        BaseArFragment.OnTapArPlaneListener, BaseArFragment.OnSessionInitializationListener {

    private Boolean isAllFabsVisible; // True when the item option buttons are visible.
    private TransformableNode currentModel; // The current model that has been selected/manipulated.
    private ItemViewModel itemViewModel; // View model containing the shared data amongst the fragments.
    private SceneformFragment sceneformFragment; // Inner fragment which utilises Sceneform to
                                                 // manipulate 3D models and scenes.

    // View types including buttons and text.
    private Button selectItemsBtn, takePhotoBtn;
    private FloatingActionButton showItemOptionsFab, changeDesignFab, removeItemFab;
    private TextView changeDesignText, removeItemText;


    public ArViewFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of ArViewFragment.
     * @return a new instance of fragment ArViewFragment.
     */
    public static ArViewFragment newInstance() {
        return new ArViewFragment();
    }


    /**
     * Initialises sceneform which is used to manipulate models and scenes in AR.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up sceneform fragment.
        sceneformFragment = new SceneformFragment();
        sceneformFragment.setOnTapArPlaneListener(this);
        sceneformFragment.setOnSessionInitializationListener(this);

        // Create transaction to add the fragment to ArViewFragment.
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_ar, sceneformFragment).commit();
    }


    /**
     * Inflates the fragment's layout.
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

        // Adds on click listeners to the buttons and manages which ones are visible.
        initialiseButtons(view);

        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
    }


    /**
     * Specifies actions to take when a button has been pressed.
     * All the actions for all the buttons are grouped into this method.
     */
    @Override
    public void onClick(View view) {

        int id = view.getId();


        // Note: Resource IDs will be non-final in gradle version 7.0 so chained if used instead of
        // switch case statements.

        // Open item selection fragment.
        if (id == R.id.btn_select_items) {
            currentModel = null;
            showItemSelectionFragment();

        // Take a photo of the camera view.
        } else if (id == R.id.btn_take_photo) {
            takePhoto();

        // Show options for a selected model.
        } else if (id == R.id.fab_show_item_options) {
            toggleItemOptionVisibility();

        // Show materials to change for item.
        } else if (id == R.id.fab_change_item_design) {
            itemViewModel.setSelectedModelNode(currentModel);
            hideMainButtons();
            showMaterialSelectionFragment();

        // Delete an item.
        } else if (id == R.id.fab_remove_item) {
            AnchorNode currentAnchorNode = getParentAnchorNode(currentModel);
            removeAnchorNode(currentAnchorNode);
            currentModel = null;
        }
    }


    /**
     * Triggered when a plane is tapped in the scene.
     * @param hitResult Where it was tapped.
     * @param plane Details of the plane.
     * @param motionEvent Type of movement event - mouse, pen, finger etc.
     */
    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

        String currentItemId = itemViewModel.getItemId(); // The item id used by firestore.
        AtomicReference<Renderable> currentRenderable = new AtomicReference<>(); // The renderable to add
                                                                                 // to the scene.

        Anchor anchor = hitResult.createAnchor(); // Details of the position to place the model in.


        // Get the renderable set in the viewModel.
        // Assumed the viewModel will have the latest one to use.
        itemViewModel.getRenderableToAdd().observe(getViewLifecycleOwner(), renderable ->
                currentRenderable.set(renderable));


        // Warning in the event that an item wasn't selected but the view managed to be shown.
        if (currentRenderable.get() == null) {
            Toast.makeText(getActivity(), "Select a model", Toast.LENGTH_SHORT).show();
            return;
        }


        // Update the anchor if model already exists
        if (currentModel != null) {
            AnchorNode currentAnchorNode = getParentAnchorNode(currentModel);
            currentAnchorNode.setAnchor(anchor);

        // Create a new anchorNode and create the renderable at the position the user touched on.
        } else {
            addModelToScene(anchor, currentRenderable.get(), currentItemId);
        }
    }


    /**
     * Includes a scene touch listener which updates what the current selected model is each time
     * there is an update to the scene.
     */
    @Override
    public void onSessionInitialization(Session session) {
        Scene scene = sceneformFragment.getArSceneView().getScene();

        scene.addOnPeekTouchListener((hitTestResult, motionEvent) ->
                performModelSelectedActions((TransformableNode) hitTestResult.getNode()));
    }


    //********************************************************************************************//
    //                                  Model rendering methods                                   //
    //********************************************************************************************//

    /**
     * Adds a new model to the scene.
     * @param anchor: The position to add the model to.
     * @param modelToRender: The model to render.
     * @param itemId: The model's itemId; for use with firestore.
     */
    private void addModelToScene(Anchor anchor, Renderable modelToRender, String itemId) {

        // Create an anchor node which the renderable will be added to.
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(sceneformFragment.getArSceneView().getScene());


        // Create the transformable model and add it to the anchor.
        TransformableNode renderedModel = new TransformableNode(sceneformFragment.getTransformationSystem());

        renderedModel.setName(itemId);
        renderedModel.setParent(anchorNode);
        renderedModel.setRenderable(modelToRender);
        renderedModel.select();


        // Show item options button.
        performModelSelectedActions(renderedModel);
    }


    /**
     * Actions to perform once a model may/has been selected.
     *   Toggles visibility of item options button.
     *   Sets the selected model as the currentModel - used for item options.
     * @param selectedModel: The current selected model. May be null which means no model has been selected.
     */
    private void performModelSelectedActions(@Nullable TransformableNode selectedModel) {

        // If model selected, set is as the current selected one and show item options
        // Else hide the item options - model has not been selected.
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


    //********************************************************************************************//
    //                                           Button methods                                   //
    //********************************************************************************************//

    /**
     * Adds on click listeners to each of the buttons available.
     */
    private void initialiseButtons(View view) {

        // Main buttons.
        takePhotoBtn = view.findViewById(R.id.btn_take_photo);
        selectItemsBtn = view.findViewById(R.id.btn_select_items);

        // Item option buttons.
        showItemOptionsFab = view.findViewById(R.id.fab_show_item_options);
        changeDesignFab = view.findViewById(R.id.fab_change_item_design);
        removeItemFab = view.findViewById(R.id.fab_remove_item);

        // Labels for item options.
        changeDesignText = view.findViewById(R.id.text_change_item_design);
        removeItemText = view.findViewById(R.id.text_remove_item);

        // All the buttons use the same onClick method.
        takePhotoBtn.setOnClickListener(this);
        selectItemsBtn.setOnClickListener(this);
        showItemOptionsFab.setOnClickListener(this);
        changeDesignFab.setOnClickListener(this);
        removeItemFab.setOnClickListener(this);

        // Hide item options and main button.
        hideItemOptionButtons();
    }


    /**
     * Toggles visibility of the item option buttons.
     */
    private void toggleItemOptionVisibility() {

        // If options visible, hide the options.
        if (isAllFabsVisible) {
            removeItemFab.hide();
            changeDesignFab.hide();

            removeItemText.setVisibility(View.GONE);
            changeDesignText.setVisibility(View.GONE);

            isAllFabsVisible = false;

        // Assume options not visible then and show the options.
        } else {
            removeItemFab.show();
            changeDesignFab.show();

            removeItemText.setVisibility(View.VISIBLE);
            changeDesignText.setVisibility(View.VISIBLE);

            isAllFabsVisible = true;
        }
    }


    /**
     * Hides the main buttons - add item, save photo and edit item buttons.
     */
    private void hideMainButtons() {
        selectItemsBtn.setVisibility(View.GONE);
        takePhotoBtn.setVisibility(View.GONE);

        hideItemOptionButtons();
    }


    /**
     * Hides the item options buttons.
     */
    private void hideItemOptionButtons() {
        showItemOptionsFab.setVisibility(View.GONE);
        changeDesignFab.setVisibility(View.GONE);
        removeItemFab.setVisibility(View.GONE);
        changeDesignText.setVisibility(View.GONE);
        removeItemText.setVisibility(View.GONE);

        isAllFabsVisible = false;
    }


    /**
     * Shows the main buttons.
     */
    private void showMainButtons() {
        selectItemsBtn.setVisibility(View.VISIBLE);
        takePhotoBtn.setVisibility(View.VISIBLE);
    }


    //********************************************************************************************//
    //                             Other Fragment Visibility methods                              //
    //********************************************************************************************//

    /**
     * Show a sliding menu with the available designs the item can be changed to
     */
    private void showMaterialSelectionFragment() {
        String selectedItemId = currentModel.getName();
        Fragment materialSelectionFragment = MaterialSelectionFragment.newInstance(selectedItemId);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_materials, materialSelectionFragment).commitNow();
    }


    /**
     * Opens the item selection page.
     */
    private void showItemSelectionFragment() {
        ((MainActivity) getActivity()).manageFragmentTransaction(ITEM_SELECT_TAG);
    }


    //********************************************************************************************//
    //                                              Other methods                                 //
    //********************************************************************************************//

    /**
     * Removes a renderable and its nodes from the scene.
     * @param nodeToRemove: AnchorNode which contains the renderable to remove.
     */
    private void removeAnchorNode(AnchorNode nodeToRemove) {
        if (nodeToRemove != null) {
            sceneformFragment.getArSceneView().getScene().removeChild(nodeToRemove);
            nodeToRemove.getAnchor().detach();
            nodeToRemove.setParent(null);
        }
    }


    /**
     * Uses the PixelCopy API to take a screenshot of the ArSceneView - scene with camera view and models.
     */
    private void takePhoto() {
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
}