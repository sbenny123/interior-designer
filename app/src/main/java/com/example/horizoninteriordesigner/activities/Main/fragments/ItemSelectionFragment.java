package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.horizoninteriordesigner.ItemDbApplication;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.activities.Main.adapters.ItemSelectionAdapter;
import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.models.Item;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.AR_VIEW_TAG;

import java.util.ArrayList;


public class ItemSelectionFragment extends Fragment implements ItemSelectionAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private ItemSelectionAdapter adapter;
    private ArrayList<Item> itemArrayList;


    public ItemSelectionFragment() {
        // Required empty public constructor
    }

    public ItemSelectionFragment(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        ItemDbApplication itemDbApplication = (ItemDbApplication) getActivity().getApplication();
        itemArrayList = itemDbApplication.getItemDB().getItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_selection, container, false);

        recyclerView = view.findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //adapter = new ItemSelectionAdapter(getActivity(), this, itemArrayList);
        adapter = new ItemSelectionAdapter(getActivity(), this, itemArrayList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {

        ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);
       /*Fragment arFragment = getParentFragmentManager().findFragmentByTag("ArViewFragment");

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.detach(getParentFragmentManager().findFragmentByTag(""));

        fragmentTransaction.attach(arFragment);
        fragmentTransaction.addToBackStack(null);

       NavController navController = Navigation.findNavController(getActivity(), R.id.fragment_main_nav_host);

        if (navController.popBackStack(R.id.arViewFragment, false)) {
            Log.i("Fragment", "Found arView in backStack");
        } else {
            navController.navigate(R.id.action_itemSelectionFragment_to_arViewFragment);
            Log.i("Fragment", "Did not find arView in backstack");
        }

       navController.navigate(R.id.arViewFragment);
        navController.navigate(R.id.action_itemSelectionFragment_to_arViewFragment);

           Toast.makeText(getActivity(), itemArrayList.get(position).getName() + " has been selected", Toast.LENGTH_SHORT).show();
           */
    }
}