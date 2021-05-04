package com.example.horizoninteriordesigner.utils;

import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.TransformableNode;

public final class SceneformUtils {

    public static AnchorNode getParentAnchorNode (TransformableNode model) {
        AnchorNode anchorNode = (AnchorNode) model.getParent();

        return anchorNode;
    }
}
