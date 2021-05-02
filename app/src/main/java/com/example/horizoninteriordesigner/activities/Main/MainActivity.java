package com.example.horizoninteriordesigner.activities.Main;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
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


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName(); // Used when writing logs and Toast text

    private static final double MIN_OPENGL_VERSION = 3.0;

    // Tag names of main fragments avaiable in this activity
    final public static String AR_VIEW_TAG = "FRAGMENT_AR_VIEW";
    final public static String ITEM_SELECT_TAG = "FRAGMEMTN_ITEM_SELECTION";


    /**
     * Actions when activity is first created:
     *   Checks if the device is supported
     *   Sets the layout to show to user
     *   Displays start fragment
     * @param savedInstanceState any saved types like string
     */
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
     * Used to configure the switching of the main fragments in the activity - Ar view and item
     * selection.
     * @param selectedFragment The tag of the fragment to show
     */
    public void manageFragmentTransaction(String selectedFragment) {

        FragmentManager fragmentManager = getSupportFragmentManager(); // to manage fragments
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true); // group of actions on fragments to perform at a time

        // Avaiable fragments
        @Nullable Fragment arViewFragment = fragmentManager.findFragmentByTag(AR_VIEW_TAG);
        @Nullable Fragment itemSelectionFragment = fragmentManager.findFragmentByTag(ITEM_SELECT_TAG);


        // Each case will show the fragment if it already exists or add the fragment to the fragment
        // manager if it doesn't.
        // Any existing fragments being shown will be hidden.
        switch (selectedFragment) {

            case AR_VIEW_TAG:

                if (itemSelectionFragment != null) {
                    fragmentTransaction.hide(itemSelectionFragment);
                }

                if (arViewFragment != null) {
                    fragmentTransaction.show(arViewFragment);
                } else {
                    fragmentTransaction.add(R.id.fragment_holder, new ArViewFragment(), AR_VIEW_TAG);
                }
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
                break;
        }

        fragmentTransaction.commit(); // signals the fragment manager that all operations have been added
    }
}

