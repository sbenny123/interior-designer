package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.horizoninteriordesigner.ItemDbApplication;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.models.ItemDB;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.lang.ref.WeakReference;

public class SceneformFragment extends ArFragment {

    public SceneformFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
