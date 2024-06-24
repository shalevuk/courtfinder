package com.example.basketballcourts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private EditText etUserName, etPassword, etEmail, etNumber;
    private Button submitButton;
    private ImageView profileImage;
    private Uri uri;
    private AlertDialog.Builder adb;
    private AlertDialog ad;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private String picName = "";
    private ActivityResultLauncher activityResultLauncher;
    private int flag;
    private byte[] bytes;
    private SharedPreferences sp;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private boolean f = false;

    private static final int FROM_GALLERY = 1;
    private static final int FROM_CAMERA = 2;

    private CheckBox checkBox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        sp = getActivity().getSharedPreferences("Users", 0);
        etUserName = view.findViewById(R.id.editTextText1);
        etPassword = view.findViewById(R.id.editTextText2);
        etEmail = view.findViewById(R.id.editTextText3);
        etNumber = view.findViewById(R.id.editTextText4);
        checkBox = view.findViewById(R.id.checkBoxSignUp);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        profileImage = view.findViewById(R.id.imageView);
        profileImage.setOnClickListener(this);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getData() != null && result.getData().getAction() == null && result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            profileImage.setImageURI(uri);
                            picName = System.currentTimeMillis() + "." + getFileExtension(uri);
                            Toast.makeText(getActivity(), picName, Toast.LENGTH_LONG).show();
                            flag = FROM_GALLERY;
                            f = true;
                        } else if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                            Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                            picName = System.currentTimeMillis() + ".jpg";
                            Toast.makeText(getActivity(), picName, Toast.LENGTH_LONG).show();
                            profileImage.setImageBitmap(bitmap);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bytes = baos.toByteArray();
                            flag = FROM_CAMERA;
                            f = true;
                        }
                    }
                });

        return view;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onClick(View view) {
        if (profileImage == view) {
            adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("pick picture");
            adb.setMessage("choose picture from gallery/camera");

            adb.setCancelable(true);
            adb.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int i) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    activityResultLauncher.launch(intent);
                }
            });
            adb.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int i) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            activityResultLauncher.launch(intent);
                        } else {
                            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                        }
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activityResultLauncher.launch(intent);
                    }
                }
            });
            ad = adb.create();
            ad.show();
        }
        if (submitButton == view) {
            firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        myRef = firebaseDatabase.getReference("Users").child(etEmail.getText().toString().replace(".", " "));
                        StorageReference ref = FirebaseStorage.getInstance().getReference("images/" + picName);

                        if (flag == FROM_GALLERY) {
                            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            User u = new User(etUserName.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(), etNumber.getText().toString(), uri.toString());
                                            myRef.setValue(u);
                                            SharedPreferences.Editor edit = sp.edit();
                                            edit.putString("name", etUserName.getText().toString());
                                            edit.putString("email", etEmail.getText().toString());
                                            edit.putString("number", etNumber.getText().toString());
                                            edit.putString("pass", etPassword.getText().toString());
                                            edit.putString("picture", uri.toString());
                                            edit.commit();

                                            if (checkBox.isChecked()) {
                                                edit.putBoolean("isChecked", true);
                                            }
                                            edit.apply();

                                            Intent i = new Intent(getActivity(), HomePage.class);
                                            startActivity(i);
                                        }
                                    });
                                }
                            });
                        } else if (flag == FROM_CAMERA) {
                            ref.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            User u = new User(etUserName.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(), etNumber.getText().toString(), uri.toString());
                                            myRef.setValue(u);
                                            SharedPreferences.Editor edit = sp.edit();
                                            edit.putString("name", etUserName.getText().toString());
                                            edit.putString("email", etEmail.getText().toString());
                                            edit.putString("number", etNumber.getText().toString());
                                            edit.putString("pass", etPassword.getText().toString());
                                            edit.putString("picture", uri.toString());
                                            edit.commit();

                                            if (checkBox.isChecked()) {
                                                edit.putBoolean("isChecked", true);
                                            }
                                            edit.apply();

                                            Intent i = new Intent(getActivity(), HomePage.class);
                                            startActivity(i);
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getActivity(), "User registration failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
