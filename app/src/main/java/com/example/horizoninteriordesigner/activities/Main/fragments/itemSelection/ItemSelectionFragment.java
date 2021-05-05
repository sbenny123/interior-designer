package com.example.horizoninteriordesigner.activities.Main.fragments.itemSelection;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.activities.Main.adapters.itemSelection.ItemSelectionAdapter;
import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.viewModels.ItemViewModel;
import com.example.horizoninteriordesigner.models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.AR_VIEW_TAG;

import java.util.ArrayList;


/**
 * Item selection fragment
 * All selectable items are shown and the renderable for sceneform is created once selected.
 */
public class ItemSelectionFragment extends Fragment implements ItemSelectionAdapter.ItemClickListener {

    private RecyclerView recyclerView; // Reuses the view to show new items in place of old ones
    private ItemSelectionAdapter adapter; // Specifies how each item card should look like
    private AlertDialog progressDialog; // For showing loading screen between item selection and ar view
    private ArrayList<Item> itemArrayList; // List of items available to select and render in AR view
    private ItemViewModel itemViewModel; // Used to retrieve data shared amongst the fragments and activity


    public ItemSelectionFragment() {
        // Required empty public constructor
    }


    /**
     * Inflates the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_selection, container, false);
    }


    /**
     * Inflates sub-views
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up configuration for recycler view
        recyclerView = view.findViewById(R.id.rv_items);
        setUpRecyclerView();

        // Set up alert dialog for showing loading screen
        setUpProgressDialog();

        // Get item as an array list and create adapter for displaying info on a single item
        getItems();

        // Set up view model for retrieving shared data amongst the fragments and activity
        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
    }


    /**
     * Once an item has been selected:
     *   The loading screen is shown
     *   The model for the item is built
     *   ArView fragment is added/shown
     */
    @Override
    public void onItemClick(View view, int position) {
        progressDialog.show();

        Item selectedItem = itemArrayList.get(position);
        buildModel(selectedItem);
    }


    /**
     * Creates a Renderable object to be added to the scene in ArViewFragment.
     * @see <a href="https://developers.google.com/sceneform/reference/com/google/ar/sceneform/rendering/Renderable>Renderable</a>"}
     * @param selectedItem Item that was selected and should be built.
     */
    private void buildModel(@NotNull Item selectedItem) {

        Uri itemUri = Uri.parse(selectedItem.getModelUrl()); // model's url from Firebase storage as a Uri
        String itemId = selectedItem.getItemId();


        // Build .glb file as a renderable object
        // Go to ArView fragment once complete
        ModelRenderable.builder()
                .setSource(getActivity(), itemUri)
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {

                    itemViewModel.setRenderableToAdd(renderable);
                    itemViewModel.setItemId(itemId);

                    ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);

                    progressDialog.dismiss();

                })
                .exceptionally(throwable -> {

                            Toast.makeText(getActivity(), "Unable to load renderable", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
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


        db.collection("models")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String itemId = document.getId();
                                String imageName = document.get("imageName") + "";
                                String imageUrl = document.get("imageUrl") + "";
                                String modelUrl = document.get("modelUrl") + "";

                                itemArrayList.add(new Item(itemId, imageName, imageUrl, modelUrl));
                            }

                            adapter = new ItemSelectionAdapter(getActivity(), ItemSelectionFragment.this::onItemClick, itemArrayList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }


    /**
     * Sets up an alert dialog with the loading icon and text
     */
    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_item_progress);

        progressDialog = builder.create();
    }


    /**
     * Sets up configuration for the recycler view:
     *   Uses grid layout with 2 columns
     */
    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}