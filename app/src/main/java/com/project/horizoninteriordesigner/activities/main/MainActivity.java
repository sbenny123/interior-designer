package com.project.horizoninteriordesigner.activities.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.fragments.arView.ArViewFragment;
import com.project.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideViewPagerFragment;
import com.project.horizoninteriordesigner.activities.main.fragments.itemSelection.ItemViewPagerFragment;


/**
 * Main activity shown and used by the user.
 * Used to control visibility of fragments, its app bar and check device is compatible with the app.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName(); // Used when writing logs and Toast text.
    private static final double MIN_OPENGL_VERSION = 3.0;

    // Tag names of main fragments available in this activity.
    final public static String AR_VIEW_TAG = "FRAGMENT_AR_VIEW";
    final public static String ITEM_SELECT_TAG = "FRAGMENT_ITEM_SELECTION";
    final public static String HELP_GUIDE_TAG = "FRAGMENT_HELP_GUIDE";


    /**
     * Actions when activity is first created:
     *   - Checks if the device is supported.
     *   - Sets the layout to show to user.
     *   - Sets top action bar.
     *   - Displays start fragment.
     * @param savedInstanceState any saved types like strings.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        manageFragmentTransaction(ITEM_SELECT_TAG);
    }


    /**
     * Sets the menu to be used by the action bar.
     * @param menu: Menu to inflate to.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_action_bar, menu);

        return true;
    }


    /**
     * Used to handle actions to take when a user has selected an item in the app bar.
     * If the method is unrecognised, it invokes the superclass method.
     * @param item: selected item from menu.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_help) {
            manageFragmentTransaction(HELP_GUIDE_TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        // Check Device can work with OpenGL ES version.
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
     * @param selectedFragment The tag of the fragment to show.
     */
    public void manageFragmentTransaction(@NonNull String selectedFragment) {

        FragmentManager fragmentManager = getSupportFragmentManager(); // to manage fragments.
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true); // group of actions on fragments to perform at a time.

        // Available fragments.
        @Nullable Fragment arViewFragment = fragmentManager.findFragmentByTag(AR_VIEW_TAG);
        @Nullable Fragment helpGuideFragment = fragmentManager.findFragmentByTag(HELP_GUIDE_TAG);
        @Nullable Fragment itemSelectionFragment = fragmentManager.findFragmentByTag(ITEM_SELECT_TAG);


        // Each case will:
        //   - Show the fragment if it already exists.
        //   - (or) Add the fragment to the fragment manager if it doesn't.
        //   - Any existing fragments being shown will be hidden.
        switch (selectedFragment) {

            case AR_VIEW_TAG:
                if (helpGuideFragment != null) { fragmentTransaction.hide(helpGuideFragment); }
                if (itemSelectionFragment != null) { fragmentTransaction.hide(itemSelectionFragment); }

                if (arViewFragment != null) {
                    fragmentTransaction.show(arViewFragment);
                } else {
                    fragmentTransaction.add(
                            R.id.fragment_holder,
                            ArViewFragment.newInstance(),
                            AR_VIEW_TAG);
                }

                break;


            case ITEM_SELECT_TAG:
                if (arViewFragment != null) { fragmentTransaction.hide(arViewFragment); }
                if (helpGuideFragment != null) { fragmentTransaction.hide(helpGuideFragment); }

                if (itemSelectionFragment != null) {
                    fragmentTransaction.show(itemSelectionFragment);
                } else {
                    fragmentTransaction.add(
                            R.id.fragment_holder,
                            ItemViewPagerFragment.newInstance(),
                            ITEM_SELECT_TAG);
                }

                break;


            case HELP_GUIDE_TAG:
                if (arViewFragment != null) { fragmentTransaction.hide(arViewFragment); }
                if (itemSelectionFragment != null) { fragmentTransaction.hide(itemSelectionFragment); }

                if (helpGuideFragment != null) {
                    fragmentTransaction.show(helpGuideFragment);
                } else {
                    fragmentTransaction.add(
                            R.id.fragment_holder,
                            HelpGuideViewPagerFragment.newInstance(),
                            HELP_GUIDE_TAG);
                }

                break;


            default:
                return;
        }

        // Run fragment transactions (show/hide) on the UI thread immediately.
        fragmentTransaction.commitNow();
    }
}

