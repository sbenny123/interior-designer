package com.example.horizoninteriordesigner.activities.Main.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.horizoninteriordesigner.models.Item;

public class ItemViewModel extends ViewModel {
    private MutableLiveData<Item> item;

    public MutableLiveData<Item> getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = new MutableLiveData<Item>();
        this.item.setValue(item);
    }
}
