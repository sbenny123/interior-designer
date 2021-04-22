package com.example.horizoninteriordesigner.activities.Main.Navigation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.Map;

public class CustomNavigator extends FragmentNavigator {
    private Context context;
    private FragmentManager fragmentManager;
    private int containerId;

    public CustomNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        String tag = String.valueOf(destination.getId());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        boolean initialNavigate = false;
        Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();

        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment);
        } else {
            initialNavigate = true;
        }

        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        if (fragment == null) {
            String className = destination.getClassName();
            fragment = fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), className);
            fragmentTransaction.add(containerId, fragment, tag);

        } else {
            fragmentTransaction.attach(fragment);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNow();

        if (initialNavigate) {
            return destination;
        } else {
            return null;
        }
    }
}
