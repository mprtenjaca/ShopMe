package com.example.bottomnav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Register extends AppCompatActivity {

    EditText ime, prezime, username, email, location, password, password2;
    Button registerMe;
    Button locateMe;
    ImageView slika;
    private ProgressBar progressBar;


    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    StorageTask storageTask;

    StorageReference storageReference;
    String dwnUrl = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ime = (EditText) findViewById(R.id.ime);
        prezime = (EditText) findViewById(R.id.prezime);
        username = (EditText) findViewById(R.id.usernameRegister);
        email = (EditText) findViewById(R.id.email);
        location = (EditText) findViewById(R.id.location);
        password = (EditText) findViewById(R.id.passRegister);
        password2 = (EditText) findViewById(R.id.rePassRegister);

        registerMe = (Button) findViewById(R.id.registerBtn);
        locateMe = (Button) findViewById(R.id.locateMe);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("User");
        storageReference = FirebaseStorage.getInstance().getReference("User");

        firebaseFirestore = FirebaseFirestore.getInstance();

        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps();
            }
        });

        registerMe.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                /*if(storageTask != null && storageTask.isInProgress()){
                    Toast.makeText(Register.this, "Registering in progress!", Toast.LENGTH_SHORT).show();
                }else{
                   registerUser();
                }*/
                registerUser();
               Log.d("MOJA", "onClickRegister");
            }
        });


    }

    private void openMaps() {
        Intent intent = new Intent(getApplicationContext(), GoogleMapsActivity.class);
        startActivity(intent);
    }

    private void registerUser() {
        Log.d("MOJA", "registerUser");
        final String imeS = ime.getText().toString().trim();
        final String prezimeS = prezime.getText().toString().trim();
        final String usernameS = username.getText().toString().trim();
        final String emailS = email.getText().toString().trim();
        final String locationS = location.getText().toString().trim();
        final String passwordS = password.getText().toString().trim();
        final String passwordS2 = password2.getText().toString().trim();
        final String url = dwnUrl;

        if(TextUtils.isEmpty(imeS)){
            ime.setError("First name is Required!");
            Log.d("MOJA", "ime ");
            return;
        }
        if(TextUtils.isEmpty(prezimeS)){
            prezime.setError("Last name is Required!");
            return;
        }
        if(TextUtils.isEmpty(usernameS)){
            username.setError("Username is Required!");
            return;
        }
        if(TextUtils.isEmpty(emailS)){
            email.setError("Email is Required!");
            return;
        }
        if(TextUtils.isEmpty(locationS)){
            location.setError("Location is Required!");
            return;
        }
        if(TextUtils.isEmpty(passwordS)){
            password.setError("Password is Required!");
            return;
        }
        if(password.length() < 6){
            password.setError("Password must be greater or equals 6 characters!");
            return;
        }
        if(passwordS == passwordS2){
            password2.setError("Passwords do not match!");
            return;
        }

        //Registriranje
        firebaseAuth.createUserWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("MOJA", "prijeSUCC");
                if(task.isSuccessful()){
                    Log.d("MOJA", "SUCC");
                    User user = new User(imeS, prezimeS, usernameS, emailS, passwordS, locationS, url.trim());

                    FirebaseDatabase.getInstance().getReference("User")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Register.this, "New User Created!", Toast.LENGTH_SHORT).show();

                        }
                    });
                    Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }if(!task.isSuccessful()){
                    Log.d("MOJA", "error");
                    Log.d("MOJA", task.getException().getMessage());
                    Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
