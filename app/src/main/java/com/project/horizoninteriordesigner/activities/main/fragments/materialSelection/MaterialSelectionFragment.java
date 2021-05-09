package com.project.horizoninteriordesigner.activities.main.fragments.materialSelection;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.MainActivity;
import com.project.horizoninteriordesigner.activities.main.adapters.materialSelection.MaterialSelectionAdapter;
import com.project.horizoninteriordesigner.activities.main.viewModels.ItemViewModel;
import com.project.horizoninteriordesigner.dialogs.LoadingDialog;
import com.project.horizoninteriordesigner.models.Material;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.project.horizoninteriordesigner.activities.main.MainActivity.AR_VIEW_TAG;


/**
 *
 */
public class MaterialSelectionFragment extends Fragment implements MaterialSelectionAdapter.ItemClickListener {

    // Fragment initialisation parameters.
    private static final String ITEM_ID_KEY = "itemId";

    private MaterialSelectionAdapter adapter;
    private FirebaseFirestore db;
    private ItemViewModel itemViewModel;
    private LoadingDialog loadingDialog;
    private ArrayList<Material> materialArrayList;
    private RecyclerView recyclerView;
    private String selectedItemId;


    public MaterialSelectionFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of MaterialSelectionFragment.
     * @param itemId: selected model's item id - to be used to retrieve materials for the item.
     * @return a new instance of fragment MaterialSelectionFragment.
     */
    public static MaterialSelectionFragment newInstance(String itemId) {
        MaterialSelectionFragment fragment = new MaterialSelectionFragment();

        Bundle args = new Bundle();
        args.putString(ITEM_ID_KEY, itemId);

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
            selectedItemId = getArguments().getString(ITEM_ID_KEY);
        }
    }


    /**
     * Inflates the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material_selection, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar_materials);
        toolbar.inflateMenu(R.menu.menu_materials_action_bar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_back:
                        getParentFragmentManager().beginTransaction()
                                .remove(MaterialSelectionFragment.this).commit();

                        showMainButtons();
                        break;

                    default:
                        return false;
                }

                return true;
            }
        });


        // Set up configuration for recycler view.
        recyclerView = view.findViewById(R.id.rv_materials);
        setUpRecyclerView();

        // Set up alert dialog for showing loading screen.
        setUpLoadingDialog();

        // Gets itemId to be used to retrieve materials for the item.
        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
        //String itemId = itemViewModel.getItemId();

        // Initialise a Firestore instance.
        db = FirebaseFirestore.getInstance();

        // Set material data in array list for a given item and creates adapter.
        getMaterialsById();
    }


    @Override
    public void onItemClick(View view, int position) {
        Material selectedMaterial = materialArrayList.get(position);
        AtomicReference<TransformableNode> selectedModel = new AtomicReference<>();

        loadingDialog.showDialog();

        itemViewModel.getSelectedModelNode().observe(getViewLifecycleOwner(), transformableNode -> {
            selectedModel.set(transformableNode);
            updateModelMaterial(selectedModel.get(), selectedMaterial);
        });
    }

    private void updateModelMaterial(TransformableNode selectedModel, Material updatedMaterial) {
        Uri materialUri = Uri.parse(updatedMaterial.getMaterialUrl());

        Texture.builder()
                .setSource(getActivity(), materialUri)
                .build()
                .thenAccept(texture -> {
                    selectedModel.getRenderable().getMaterial().setTexture("baseColorMap", texture);

                    ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);

                    loadingDialog.dismissDialog();
                });
    }


    private void getMaterialsById() {

        DocumentReference docRef = db.collection("models").document(selectedItemId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists() && document.contains("materials")) {
                        Map documentData = document.getData();
                        ArrayList materialNmes = (ArrayList) documentData.get("materials");

                        getSelectedMaterials(materialNmes);
                    }
                }
            }
        });
    }


    /**
     * Retrieves item documents from Firestore and maps to Item model and adds to array list.
     * Adapter is set once all documents are added.
     */
    private void getSelectedMaterials(ArrayList materialsToRetrieve) {
        materialArrayList = new ArrayList<>();

        db.collection("materials")
                .whereIn("materialName", materialsToRetrieve)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String materialId = document.getId();
                                String materialName = document.get("materialName") + "";
                                String materialUrl = document.get("materialUrl") + "";

                                materialArrayList.add(new Material(materialId, materialName, materialUrl));
                            }

                            // Create adapter for materials view - bridges recyclerview and materials (data source).
                            adapter = new MaterialSelectionAdapter(getContext(),
                                    MaterialSelectionFragment.this::onItemClick, materialArrayList);

                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }


    /**
     * Sets up an alert dialog with the loading icon and given text.
     * For use when waiting for a material to be updated.
     */
    private void setUpLoadingDialog() {
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.createDialog("Changing the item's design");
    }


    /**
     * Sets up configuration for the recycler view:
     *   - Uses a 1 line layout with items going horizontally across the screen.
     */
    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void showMainButtons() {
        View arViewFragmentView = getParentFragment().getView();
        Button selectItemsBtn = arViewFragmentView.findViewById(R.id.btn_select_items);
        Button takePhotoBtn = arViewFragmentView.findViewById(R.id.btn_take_photo);

        selectItemsBtn.setVisibility(View.VISIBLE);
        takePhotoBtn.setVisibility(View.VISIBLE);
    }
}