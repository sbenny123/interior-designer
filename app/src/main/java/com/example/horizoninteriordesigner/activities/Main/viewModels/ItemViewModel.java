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

    private MutableLiveData<Item> item = new MutableLiveData<Item>();
    private MutableLiveData<TransformableNode> modelNode = new MutableLiveData<TransformableNode>();
    private MutableLiveData<Renderable> renderable = new MutableLiveData<Renderable>();


    /**
     *
     */
    public MutableLiveData<Renderable> getRenderableToAdd() { return renderableToAdd; }

    public void setRenderableToAdd(Renderable renderableToAdd) { this.renderableToAdd.setValue(renderableToAdd); }


    /**
     *
     */
    public String getItemId() { return itemId; }

    public void setItemId(String itemId) { this.itemId = itemId; }







    /*
                         Methods for Item
                     */
    public MutableLiveData<Item> getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item.setValue(item);
    }


    /*
         Methods for Renderable
     */
    public MutableLiveData<Renderable> getRenderable() {
        return renderable;
    }

    public void setRenderable(Renderable renderable) {
        this.renderable.setValue(renderable);
    }


    /*
         Methods for TransoformableNode
     */
    public MutableLiveData<TransformableNode> getModelNode() { return modelNode; }

    public void setModelNode(TransformableNode modelNode) { this.modelNode.setValue(modelNode); }
}
