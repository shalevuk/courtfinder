package com.example.basketballcourts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    CheckBox checkBox;
    SharedPreferences sp;

    Button Reg,Sub;

    EditText pass,email;
    String password;
    String mail;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        pass = v.findViewById(R.id.editTextText6);
        email = v.findViewById(R.id.editTextText5);
        Sub = v.findViewById(R.id.loginButton);
        Sub.setOnClickListener(this);
        checkBox = v.findViewById(R.id.checkBox);
        sp = getActivity().getSharedPreferences("Users", 0);
        return v;
    }

    @Override
    public void onClick(View v) {
        password = pass.getText().toString();
        mail = email.getText().toString();
        final ProgressDialog pd = ProgressDialog.show(getContext(), "מתחבר...", "המתן אנא", true);
        pd.show();
        final FirebaseAuth Auth = FirebaseAuth.getInstance();
        Auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "משתמש מחובר!", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                    if (checkBox.isChecked()) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("isChecked", true);
                        editor.putString("Email", mail);
                        editor.putString("Password", password);
                        editor.commit();
                    }
                    Intent in = new Intent(getActivity(), HomePage.class);
                    in.putExtra("email", mail);
                    in.putExtra("pass", password);
                    startActivity(in);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
    }
}
