package com.example.basketballcourts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class Search extends Fragment {

    private RecyclerView recyclerView;
    private CourtAdapter courtAdapter;
    private ArrayList<Courts> courtsList;
    private ArrayList<String> cities;
    private ArrayList<Courts> filteredCourtsList;
    private TextView filter;
    private ArrayList<Integer> cList;
    private boolean[] selectedCities;
    private String[] sCities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        cities = new ArrayList<>();
        courtsList = new ArrayList<>();
        filteredCourtsList = new ArrayList<>();
        cList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.search_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        courtAdapter = new CourtAdapter(getContext(), filteredCourtsList);
        recyclerView.setAdapter(courtAdapter);

        filter = view.findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sCities == null || sCities.length == 0) {
                    Log.d("SearchFragment", "No cities available for selection");
                    return; // Exit if no cities are available
                }
                showFilterDialog();
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference db = firebaseDatabase.getReference("courts");
        db.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                cities.clear();
                courtsList.clear();
                for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                    String cityKey = citySnapshot.getKey(); // Get city key (e.g., "city1")
                    cities.add(cityKey); // Add city key to cities list

                    for (DataSnapshot courtSnapshot : citySnapshot.getChildren()) {
                        Courts court = courtSnapshot.getValue(Courts.class); // Convert each courtSnapshot to Courts object
                        if (court != null) {
                            court.setCity(cityKey); // Assuming you have a setCity method in Courts class
                            courtsList.add(court); // Add court to courtsList
                        }
                    }
                }

                selectedCities = new boolean[cities.size()];
                sCities = cities.toArray(new String[0]);

                // Reset previous selections
                resetSelections();

                // Debugging prints
                Log.d("SearchFragment", "Cities: " + cities.toString());
                Log.d("SearchFragment", "Selected Cities length: " + selectedCities.length);
                for (String city : sCities) {
                    Log.d("SearchFragment", "City: " + city);
                }

                // Initially show all courts
                filteredCourtsList.clear();
                filteredCourtsList.addAll(courtsList);
                courtAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("SearchFragment", "Error fetching data", e); // Debugging error
            }
        });

        return view;
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select City");
        builder.setCancelable(false);
        builder.setMultiChoiceItems(sCities, selectedCities, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    cList.add(i);
                    Collections.sort(cList);
                } else {
                    cList.remove(Integer.valueOf(i)); // Remove the Integer object
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filteredCourtsList.clear();
                StringBuilder stringBuilder = new StringBuilder();
                for (int cityIndex : cList) {
                    String city = cities.get(cityIndex);
                    for (Courts court : courtsList) {
                        if (court.getCity().equals(city)) {
                            filteredCourtsList.add(court);
                        }
                    }
                    stringBuilder.append(city).append(", ");
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.setLength(stringBuilder.length() - 2); // Remove the trailing comma and space
                }
                filter.setText(stringBuilder.toString());
                courtAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetSelections();
                filter.setText("");
                filteredCourtsList.clear();
                filteredCourtsList.addAll(courtsList); // Restore all courts
                courtAdapter.notifyDataSetChanged();
            }
        });

        builder.show();
    }

    private void resetSelections() {
        for (int j = 0; j < selectedCities.length; j++) {
            selectedCities[j] = false;
        }
        cList.clear();
    }
}
