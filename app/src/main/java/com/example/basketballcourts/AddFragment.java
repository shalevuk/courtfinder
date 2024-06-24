package com.example.basketballcourts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFragment extends Fragment {

    private EditText editTextEmail, editTextHouseNumber, editTextCity, editTextName, editTextPhone, editTextStreet;
    private Button buttonAddCourt;

    private DatabaseReference databaseReference;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("courts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initialize EditText fields and Button
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextHouseNumber = view.findViewById(R.id.editTextHouseNumber);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextStreet = view.findViewById(R.id.editTextStreet);
        buttonAddCourt = view.findViewById(R.id.buttonAddCourt);

        // Set OnClickListener for the button
        buttonAddCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourt();
            }
        });

        return view;
    }

    private void addCourt() {
        // Get values from EditText fields
        String email = editTextEmail.getText().toString().trim();
        String houseNumber = editTextHouseNumber.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String street = editTextStreet.getText().toString().trim();

        // Validate if fields are not empty
        if (email.isEmpty() || houseNumber.isEmpty() || city.isEmpty() || name.isEmpty() || phone.isEmpty() || street.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create unique key for new court
        String courtId = databaseReference.push().getKey();

        // Create Courts object
        Courts newCourt = new Courts(name, city, email, phone,street,houseNumber);
    ArrayList<Courts> lstCoutrs = new ArrayList<>();
        // Add court to Firebase Database
        databaseReference.child(city).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot snapshot1: task.getResult().getChildren()){
                    Courts temp = new Courts (snapshot1.child("name").getValue(String.class),snapshot1.child("city").getValue(String.class), snapshot1.child("email").getValue(String.class)
                            ,snapshot1.child("phone").getValue(String.class),snapshot1.child("address").getValue(String.class),snapshot1.child("housenum").getValue(String.class));
                    lstCoutrs.add(temp);
                }
                lstCoutrs.add(newCourt);
                HashMap<String, Object> map = new HashMap<>();
                map.put(city, lstCoutrs);
                databaseReference.updateChildren(map);
                Toast.makeText(getContext(), "ההוספה הושלמה", Toast.LENGTH_LONG).show();
                clearFields();

            }
        });

    }

    private void clearFields() {
        editTextEmail.setText("");
        editTextHouseNumber.setText("");
        editTextCity.setText("");
        editTextName.setText("");
        editTextPhone.setText("");
        editTextStreet.setText("");
    }
}
