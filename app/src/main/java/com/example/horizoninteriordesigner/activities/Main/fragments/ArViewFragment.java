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
import com.google.android.filament.MaterialInstance;
import com.google.android.filament.gltfio.FilamentAsset;
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
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.ITEM_SELECT_TAG;
import static com.example.horizoninteriordesigner.utils.CameraUtils.*;


public class ArViewFragment extends Fragment implements View.OnClickListener,
        BaseArFragment.OnTapArPlaneListener, BaseArFragment.OnSessionInitializationListener {

    private SceneformFragment sceneformFragment;
    private AnchorNode currentAnchorNode;
    private TransformableNode currentModel;
    private ItemViewModel itemViewModel;
    private Renderable renderable;


    public ArViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sceneformFragment = new SceneformFragment();
        sceneformFragment.setOnTapArPlaneListener(this::onTapPlane);
        sceneformFragment.setOnSessionInitializationListener(this::onSessionInitialization);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_ar, sceneformFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ar_view, container, false);

        initialiseButtons(v);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
    }

    private void initialiseButtons(View v) {
        FloatingActionButton takePhotoBtn = v.findViewById(R.id.btn_take_photo);
        FloatingActionButton selectItemsBtn = v.findViewById(R.id.btn_select_items);
        FloatingActionButton showItemOptionsBtn = v.findViewById(R.id.btn_show_item_options);

        takePhotoBtn.setOnClickListener(this);
        selectItemsBtn.setOnClickListener(this);
        showItemOptionsBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_select_items:
                currentAnchorNode = null;
                showItemSelectionFragment();
                break;

            case R.id.btn_show_item_options:
                showItemOptionsPopup(v);
                break;

            case R.id.btn_take_photo:
                takePhoto(v);
                break;
        }
    }

    /**
     * Opens the item selection page.
     */
    private void showItemSelectionFragment() {
        ((MainActivity) getActivity()).manageFragmentTransaction(ITEM_SELECT_TAG);
    }


    private void showMaterialSelectionFragment() {
        Fragment materialSelectionFragment = new MaterialSelectionFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_materials, materialSelectionFragment).commit();
    }

    /**
     *
     * @param v
     */
    private void showItemOptionsPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_model_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.item_change_design:
                        showMaterialSelectionFragment();
                        break;

                    case R.id.item_remove_item:
                        removeAnchorNode(currentAnchorNode);
                        currentAnchorNode = null;
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
     *
     * @param nodeToRemove
     */
    private void removeAnchorNode(AnchorNode nodeToRemove) {
        if (nodeToRemove != null) {
            sceneformFragment.getArSceneView().getScene().removeChild(nodeToRemove);
            nodeToRemove.getAnchor().detach();
            nodeToRemove.setParent(null);
            nodeToRemove = null;
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
        Log.i("ArView", "Tapped on plane!");

        itemViewModel.getRenderable().observe(getViewLifecycleOwner(), renderable -> {
            this.renderable = renderable;
        });

        if (renderable == null) {
            Toast.makeText(getActivity(), "Select a model", Toast.LENGTH_SHORT).show();
            return;
        }

        Anchor anchor = hitResult.createAnchor();

        if (currentAnchorNode != null) {
            currentAnchorNode.setAnchor(anchor);

        } else {
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(sceneformFragment.getArSceneView().getScene());

            // Create the transformable model and add it to the anchor.
            TransformableNode model = new TransformableNode(sceneformFragment.getTransformationSystem());

            model.setParent(anchorNode);
            model.setRenderable(renderable);
            model.select();
            model.setOnTapListener(new Node.OnTapListener() {
                @Override
                public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                    TransformableNode selectedModel = (TransformableNode) hitTestResult.getNode();
                    AnchorNode selectedAnchorNode = (AnchorNode) selectedModel.getParent();
                    Renderable selectedRenderable = selectedModel.getRenderable();

                    currentAnchorNode = selectedAnchorNode;
                    currentModel = selectedModel;

                    Texture.builder()
                            .setSource(getActivity(), R.drawable.blue_1)
                            .build()
                            .thenAccept(texture -> {
                                selectedModel.getRenderable().getMaterial().setTexture("baseColorMap", texture);
                            });


                  /*  Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.parquet);

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
                    }*/
                }
            });


            currentAnchorNode = anchorNode;
            currentModel = model;
        }
    }


    private void addModelToScene(Anchor anchor, Renderable modelToRender) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(sceneformFragment.getArSceneView().getScene());

        // Create the transformable model and add it to the anchor.
        TransformableNode model = new TransformableNode(sceneformFragment.getTransformationSystem());

        model.setParent(anchorNode);
        model.setRenderable(modelToRender);
        model.select();
        model.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                TransformableNode selectedModel = (TransformableNode) hitTestResult.getNode();
                AnchorNode selectedAnchorNode = (AnchorNode) selectedModel.getParent();
                Renderable selectedRenderable = selectedModel.getRenderable();

                currentAnchorNode = selectedAnchorNode;
            }
        });

        currentAnchorNode = anchorNode;
    }

    @Override
    public void onSessionInitialization(Session session) {
        Scene scene = sceneformFragment.getArSceneView().getScene();
        scene.addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                if (hitTestResult.getNode() != null) {
                    currentAnchorNode = (AnchorNode) hitTestResult.getNode().getParent();
                }
            }
        });

    }
}