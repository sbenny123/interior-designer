package com.example.horizoninteriordesigner.activities.Main.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.horizoninteriordesigner.models.Item;
import com.google.ar.sceneform.rendering.Renderable;

public class ItemViewModel extends ViewModel {
    private MutableLiveData<Item> item;
    private MutableLiveData<Renderable> renderable;

    public MutableLiveData<Renderable> getRenderable() {
        return renderable;
    }

    public void setRenderable(Renderable renderable) {
        this.renderable = new MutableLiveData<Renderable>();
        this.renderable.setValue(renderable);
    }

    public MutableLiveData<Item> getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = new MutableLiveData<Item>();
        this.item.setValue(item);
    }
}
