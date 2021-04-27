package com.example.horizoninteriordesigner.activities.Main;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.fragments.ArViewFragment;
import com.example.horizoninteriordesigner.activities.Main.fragments.ItemSelectionFragment;
import com.example.horizoninteriordesigner.models.Item;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity implements ItemSelectionFragment.SendFragmentListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final double MIN_OPENGL_VERSION = 3.0;

    final public static String AR_VIEW_TAG = "FRAGMENT_AR_VIEW";
    final public static String ITEM_SELECT_TAG = "FRAGMEMTN_ITEM_SELECTION";

    private Renderable renderable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.activity_main);

        manageFragmentTransaction(ITEM_SELECT_TAG);
    }


    /**
     *
     * @param itemUri
     */
    public void buildModel(Uri itemUri) {
        WeakReference<MainActivity> weakActivity = new WeakReference<>(this);
        ModelRenderable.builder()
                .setSource(this, itemUri)
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    MainActivity activity = weakActivity.get();
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


    /**
     * Checks if the device is compatible with Sceneform and ARCore.
     * Returns true if compatible and false if not.
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {

        // Check Android version is compatible
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Android N or later is required");
            activity.finish();
            return false;
        }

        // Check Device can work with OpenGL ES version
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();

        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "OpenGL ES 3.0 or later is required");
            activity.finish();
            return false;
        }

        return true;
    }


    /**
     *
     * @param selectedFragment
     */
    public void manageFragmentTransaction(String selectedFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        @Nullable Fragment arViewFragment = fragmentManager.findFragmentByTag(AR_VIEW_TAG);
        @Nullable Fragment itemSelectionFragment = fragmentManager.findFragmentByTag(ITEM_SELECT_TAG);

        switch (selectedFragment) {
            case AR_VIEW_TAG:
                // Show fragment if it exists
                if (arViewFragment != null) {
                    fragmentTransaction.show(arViewFragment);

                    // Add to fragment manager as it doesn't exist
                } else {
                    fragmentTransaction.add(R.id.fragment_holder, new ArViewFragment(), AR_VIEW_TAG);
                }

                if (itemSelectionFragment != null) {
                    fragmentTransaction.hide(itemSelectionFragment);
                }

                fragmentTransaction.commit();
                break;

            case ITEM_SELECT_TAG:
                if (itemSelectionFragment != null) {
                    fragmentTransaction.show(itemSelectionFragment);
                } else {
                    fragmentTransaction.add(R.id.fragment_holder, new ItemSelectionFragment(), ITEM_SELECT_TAG);
                }

                if (arViewFragment != null) {
                    fragmentTransaction.hide(arViewFragment);
                }

                fragmentTransaction.commit();
                break;
        }
    }

    /**
     * Builds item to be rendered.
     * @param item
     */
    @Override
    public void sendItem(Item item) {
        if (!item.getModelUrl().isEmpty()) {
            buildModel(Uri.parse(item.getModelUrl()));
        }
    }

    public Renderable getRenderable() {
        return renderable;
    }
}

