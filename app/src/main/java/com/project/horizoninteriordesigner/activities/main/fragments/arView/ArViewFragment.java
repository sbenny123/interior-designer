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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.MainActivity;
import com.project.horizoninteriordesigner.activities.main.fragments.materialSelection.MaterialSelectionFragment;
import com.project.horizoninteriordesigner.activities.main.viewModels.ItemViewModel;
import com.project.horizoninteriordesigner.dialogs.ErrorDialog;

import java.io.IOException;
import java.util.Collection;
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

    private TransformableNode currentModel; // The current model that has been selected/manipulated.
    private Boolean canShowItemFabs; // True when the item option buttons should be visible.
    private ErrorDialog errorDialog; // For showing errors if any.
    private static Boolean isShowingMaterials; // True when the material fragment is visible.
    private ItemViewModel itemViewModel; // View model containing the shared data amongst the fragments.
    private SceneformFragment sceneformFragment; // Inner fragment which utilises Sceneform to
                                                 // manipulate 3D models and scenes.

    // View types including buttons and text.
    private FloatingActionButton selectItemsBtn, takePhotoBtn;
    private FloatingActionButton showItemOptionsFab, changeDesignFab, removeItemFab;
    private TextView changeDesignText, removeItemText, selectItemsText, showItemOptionsText, instructionText;


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
        View view = inflater.inflate(R.layout.fragment_ar_view, container, false);

        return view;
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

        // Set boolean indicator intial values
        isShowingMaterials = false;

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
            setCurrentModel(null);
            performModelSelectedActions(null);
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
            showMaterialSelectionFragment();

        // Delete an item.
        } else if (id == R.id.fab_remove_item) {
            AnchorNode currentAnchorNode = getParentAnchorNode(currentModel);
            removeAnchorNode(currentAnchorNode);
            currentModel = null;
            hideAllItemOptionBtns();
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
        session.getConfig().setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);

        Scene scene = sceneformFragment.getArSceneView().getScene();

        scene.addOnPeekTouchListener((hitTestResult, motionEvent) ->
                performModelSelectedActions((TransformableNode) hitTestResult.getNode()));

        scene.addOnUpdateListener(this::onUpdate);
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

        modelToRender.setShadowCaster(false);
        modelToRender.setShadowReceiver(false);

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

        // If model selected, set as the current selected one
        // Show item options only if the materials fragment isn't showing
        // For all other cases, hide the item options - assumes model has not been selected.
        if (selectedModel != null && !isShowingMaterials) {
            setCurrentModel(selectedModel);
            showItemOptionsBtn();

        } else if (selectedModel != null) {
            setCurrentModel(selectedModel);

        } else {
            canShowItemFabs = false;
            hideAllItemOptionBtns();
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

        // Labels for main buttons.
        selectItemsText = view.findViewById(R.id.text_select_items);
        showItemOptionsText = view.findViewById(R.id.text_show_item_options);

        // Item option buttons.
        showItemOptionsFab = view.findViewById(R.id.fab_show_item_options);
        changeDesignFab = view.findViewById(R.id.fab_change_item_design);
        removeItemFab = view.findViewById(R.id.fab_remove_item);

        // Labels for item options.
        changeDesignText = view.findViewById(R.id.text_change_item_design);
        removeItemText = view.findViewById(R.id.text_remove_item);

        // Instruction seen at the start when detecting planes.
        instructionText = view.findViewById(R.id.text_instruction);

        // All the buttons use the same onClick method.
        takePhotoBtn.setOnClickListener(this);
        selectItemsBtn.setOnClickListener(this);
        showItemOptionsFab.setOnClickListener(this);
        changeDesignFab.setOnClickListener(this);
        removeItemFab.setOnClickListener(this);

        // Set to false so that the items can be hidden
        canShowItemFabs = false;

        // Hide item options and main button.
        hideAllItemOptionBtns();
    }


    /**
     * Toggles visibility of the item option buttons.
     */
    private void toggleItemOptionVisibility() {

        // Show item options
        if (canShowItemFabs) {
            removeItemFab.show();
            changeDesignFab.show();

            removeItemText.setVisibility(View.VISIBLE);
            changeDesignText.setVisibility(View.VISIBLE);

            canShowItemFabs = false; // Already showing so should hide them if method called again.

        // Assume options can't be visible and hide them.
        } else {
            removeItemFab.hide();
            changeDesignFab.hide();

            removeItemText.setVisibility(View.GONE);
            changeDesignText.setVisibility(View.GONE);

            canShowItemFabs = true; // Not shown so should show them if method called again.
        }
    }


    /**
     * Hides the main buttons - add item, save photo and edit item buttons.
     */
    private void hideMainButtons() {
        selectItemsBtn.setVisibility(View.GONE);
        takePhotoBtn.setVisibility(View.GONE);

        selectItemsText.setVisibility(View.GONE);

        hideAllItemOptionBtns();
    }


    /**
     * Hides the item options buttons.
     */
    private void hideAllItemOptionBtns() {
        showItemOptionsFab.setVisibility(View.GONE);
        showItemOptionsText.setVisibility(View.GONE);

        toggleItemOptionVisibility();
    }


    /**
     *
     * Shows the item options button only - not its options.
     */
    private void showItemOptionsBtn() {
        showItemOptionsFab.setVisibility(View.VISIBLE);
        showItemOptionsText.setVisibility(View.VISIBLE);
    }


    //********************************************************************************************//
    //                             Other Fragment Visibility methods                              //
    //********************************************************************************************//

    public static void setIsShowingMaterials(Boolean isShowing) {
        isShowingMaterials = isShowing;
    }


    /**
     * Show a sliding menu with the available designs the item can be changed to
     */
    private void showMaterialSelectionFragment() {
        String selectedItemId = currentModel.getName();
        Boolean isOnline = ((MainActivity) getActivity()).isOnline();

        // Get the materials only if the device is connected to the internet.
        // Else, show the error dialog.
        if (isOnline) {
            hideMainButtons();

            Fragment materialSelectionFragment = MaterialSelectionFragment.newInstance(selectedItemId);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_materials, materialSelectionFragment).commitNow();
            isShowingMaterials = true;

        } else {
            setUpInternetErrorDialog();
        }
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
     * Actions to take at each frame update.
     * Hides the instruction text when a plane is found.
     * @param frameTime
     */
    private void onUpdate(FrameTime frameTime) {
        Frame frame = sceneformFragment.getArSceneView().getArFrame();

        if (frame == null) {
            return;
        }

        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);

        for (Plane plane : planes) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                instructionText.setVisibility(View.GONE);
                break;
            }
        }
    }


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
     * Sets up an error dialog when there is no internet connected.
     * The internet is needed to load the materials from Firebase storage.
     */
    private void setUpInternetErrorDialog() {
        errorDialog = new ErrorDialog(getContext());
        errorDialog.createDialog(R.drawable.ic_no_wifi, "No internet connection",
                "Please turn on your internet connection and try again.");
        errorDialog.showDialog();
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