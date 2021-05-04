package com.example.horizoninteriordesigner.activities.Main.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.horizoninteriordesigner.models.Item;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.TransformableNode;


/**
 * Manages UI-related data which is shared amongst the fragments in MainActivity
 * {@link com.example.horizoninteriordesigner.activities.Main.MainActivity}
 *
 */
public class ItemViewModel extends ViewModel {
    private String itemId;
    private MutableLiveData<Renderable> renderableToAdd = new MutableLiveData<Renderable>();
    private MutableLiveData<TransformableNode> selectedModelNode = new MutableLiveData<TransformableNode>();


    /**
     * ItemId methods - get and set
     */
    public String getItemId() { return itemId; }

    public void setItemId(String itemId) { this.itemId = itemId; }


    /**
     * RenderableToAdd methods - get and set
     */
    public MutableLiveData<Renderable> getRenderableToAdd() { return renderableToAdd; }

    public void setRenderableToAdd(Renderable renderableToAdd) {
        this.renderableToAdd.setValue(renderableToAdd);
    }


    /**
     *  selectedModelNode methods - get and set
     */
    public MutableLiveData<TransformableNode> getSelectedModelNode() { return selectedModelNode; }

    public void setSelectedModelNode(TransformableNode selectedModelNode) {
        this.selectedModelNode.setValue(selectedModelNode);
    }
}
