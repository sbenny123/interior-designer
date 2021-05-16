package com.project.horizoninteriordesigner.activities.main.fragments.itemSelection;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.MainActivity;
import com.project.horizoninteriordesigner.activities.main.adapters.itemSelection.ItemSelectionAdapter;
import com.project.horizoninteriordesigner.activities.main.viewModels.ItemViewModel;
import com.project.horizoninteriordesigner.dialogs.ErrorDialog;
import com.project.horizoninteriordesigner.dialogs.LoadingDialog;
import com.project.horizoninteriordesigner.models.Item;

import java.util.ArrayList;

import static com.project.horizoninteriordesigner.activities.main.MainActivity.AR_VIEW_TAG;


/**
 * Item selection page actions.
 * All selectable items for a particular category are shown and once an item has been selected, its
 * Sceneform renderable is created ready to be used in the AR view.
 */
public class ItemSelectionFragment extends Fragment implements ItemSelectionAdapter.ItemClickListener {

    // Fragment initialisation parameters.
    private static final String CAT_KEY = "catKey";

    private ItemSelectionAdapter adapter; // Specifies how each item's card layout should look like.
    private String catKey; // Item category key; Used to retrieve the items with that category from firestore.
    private ErrorDialog errorDialog; // For showing errors if any.
    private ArrayList<Item> itemArrayList; // List of items available to select and render in AR view.
    private ItemViewModel itemViewModel; // Used to retrieve data shared amongst the fragments and activity.
    private LoadingDialog loadingDialog; // For showing loading screen whilst the model is built.
    private RecyclerView recyclerView; // Reuses the view to show new items in place of old ones.


    public ItemSelectionFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of ItemSelectionFragment.
     * @param catKey: item category key - to be used to retrieve items in category from firestore.
     * @return a new instance of fragment ItemSelectionFragment.
     */
    public static ItemSelectionFragment newInstance(String catKey) {
        ItemSelectionFragment fragment = new ItemSelectionFragment();

        Bundle args = new Bundle();
        args.putString(CAT_KEY, catKey);

        fragment.setArguments(args);

        return fragment;
    }


    /**
     * Initialises variables using the arguments passed in at instance creation.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            catKey = getArguments().getString(CAT_KEY);
        }
    }


    /**
     * Inflates the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_selection, container, false);
    }


    /**
     * Inflates sub-views and sorts retrieval of items.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up configuration for recycler view
        recyclerView = view.findViewById(R.id.rv_items);
        setUpRecyclerView();

        // Set up alert dialog for showing loading screen
        setUpLoadingDialog();

        // Get item as an array list and create adapter for displaying info on a single item
        getItems();

        // Set up view model for retrieving shared data amongst the fragments and activity
        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
    }


    /**
     * Once an item has been selected:
     *   - The loading screen is shown.
     *   - The model for the item is built.
     *   - ArView fragment is added/shown.
     */
    @Override
    public void onItemClick(View view, int position) {
        loadingDialog.showDialog();

        Item selectedItem = itemArrayList.get(position);


        // Build the model only if the device is connected to the internet.
        // Else, return an error message.
        Boolean isOnline = ((MainActivity) getActivity()).isOnline();

        if (isOnline) {
            buildModel(selectedItem);

        } else {
            setUpInternetErrorDialog();
            loadingDialog.dismissDialog();
        }
    }


    /**
     * Creates a Renderable object to be added to the scene in ArViewFragment.
     * @see <a href="https://developers.google.com/sceneform/reference/com/google/ar/sceneform/rendering/Renderable>Renderable</a>"}
     * @param selectedItem Item that was selected and should be built.
     */
    private void buildModel(@NonNull Item selectedItem) {

        Uri itemUri = Uri.parse(selectedItem.getModelUrl()); // model's url from Firebase storage as a Uri
        String itemId = selectedItem.getItemId();

        // Build .glb file as a renderable object.
        // Go to ArView fragment once complete.
        ModelRenderable.builder()
                .setSource(getActivity(), itemUri)
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {

                    itemViewModel.setRenderableToAdd(renderable);
                    itemViewModel.setItemId(itemId);

                    ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);

                    loadingDialog.dismissDialog();

                })
                .exceptionally(throwable -> {

                    loadingDialog.dismissDialog();

                    Log.i("ItemSelection", throwable.getMessage());

                    setUpModelErrorDialog();

                    return null;
                });
    }


    /**
     * Retrieves item documents from Firestore and maps to Item model and adds to array list.
     * Adapter is set once all documents are added.
     */
    private void getItems() {

        itemArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query; // A firebase query to perform.


        // Get the items with the specific category key or get all items.
        if (catKey != "") {
            query = db.collection("models").whereEqualTo("itemCategory", catKey);
        } else {
            query = db.collection("models");
        }

        // Perform the query.
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // Create an Item instance using the document's data and add to the arrayList.
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String itemId = document.getId();
                                String imageName = document.get("imageName") + "";
                                String imageUrl = document.get("imageUrl") + "";
                                String modelUrl = document.get("modelUrl") + "";

                                itemArrayList.add(new Item(itemId, imageName, imageUrl, modelUrl));
                            }

                            // Create a grid view of the items.
                            adapter = new ItemSelectionAdapter(getActivity(), ItemSelectionFragment.this::onItemClick, itemArrayList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }


    /**
     * Sets up an error dialog when there is a problem with getting to model.
     */
    private void setUpModelErrorDialog() {
        errorDialog = new ErrorDialog(getContext());
        errorDialog.createDialog(R.drawable.ic_error, "Could not get this model",
                "The selected model could not be loaded. Please try again later.");
        errorDialog.showDialog();
    }

    /**
     * Sets up an error dialog when there is no internet connected.
     * The internet is needed to load the model from Firebase storage.
     */
    private void setUpInternetErrorDialog() {
        errorDialog = new ErrorDialog(getContext());
        errorDialog.createDialog(R.drawable.ic_no_wifi, "No internet connection",
                "Please turn on your internet connection and try again.");
        errorDialog.showDialog();
    }

    /**
     * Sets up an alert dialog with the loading icon and given text.
     * For use when waiting for the model to be ready for rendering.
     */
    private void setUpLoadingDialog() {
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.createDialog("Getting your item, please wait...");
    }


    /**
     * Sets up configuration for the recycler view:
     *   - Uses grid layout with 2 columns.
     */
    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}