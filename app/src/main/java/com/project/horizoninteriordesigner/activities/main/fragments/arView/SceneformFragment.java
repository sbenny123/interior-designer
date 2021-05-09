package com.project.horizoninteriordesigner.activities.main.fragments.arView;

import android.Manifest;
import android.os.Bundle;
import com.google.ar.sceneform.ux.ArFragment;


/**
 * Fragment for Sceneform's camera view and scene.
 */
public class SceneformFragment extends ArFragment {

    public SceneformFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of SceneformFragment.
     * @return a new instance of fragment SceneformFragment.
     */
    public static SceneformFragment newInstance() {
        SceneformFragment fragment = new SceneformFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * List of permissions to request from user when AR view is shown e.g. accessing camera and
     * saving to files.
     * @return
     */
    @Override
    public String[] getAdditionalPermissions() {
        String[] addlPerms; // Default list of additional permissions to request from user
        int permLength; // Length of addlPerms or 0 if it doesn't exist
        String[] perms; // Final list of additional permissions to request from user; Replaces addlPerms


        addlPerms = super.getAdditionalPermissions();
        permLength = addlPerms != null ? addlPerms.length : 0; // Initialises length of default permissions list

        perms = new String[permLength + 1]; // Has one extra index for adding permission to write to storage
        perms[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        // Copies over default permission so they can be requested to.
        if (permLength > 0) {
            System.arraycopy(addlPerms, 0, perms, 1, permLength);
        }

        return perms;
    }
}
