package com.example.bottomnav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private static final int PICK_IMAGE = 71;

    Button saveBtn, goBackBtn;
    EditText editName, editLastName, editUsername, editEmail, editLocation;
    ImageView imageView;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    StorageTask storageTask;
    StorageReference storageReference1;

    Uri uriFile;
    String dwnUrl = "";
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = (EditText) findViewById(R.id.editName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editUsername = (EditText) findViewById(R.id.editUsername);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editLocation = (EditText) findViewById(R.id.editLocation);

        saveBtn = (Button) findViewById(R.id.saveChangesBtn);
        goBackBtn = (Button) findViewById(R.id.backToProfileBtn);

        imageView = (ImageView) findViewById(R.id.imageView);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference("User");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


    }

    private void fetchData(DataSnapshot dataSnapshot) {

        User user = dataSnapshot.getValue(User.class);


        editName.setText(user.getName());
        editLastName.setText(user.getLastName());
        editUsername.setText(user.getUsername());
        editEmail.setText(user.getEmail());
        editLocation.setText(user.getLocation());
        if(!user.getImageAddress().isEmpty()) {
            Picasso.get().load(user.getImageAddress()).fit().centerCrop().into(imageView);
        }

        storeNewValuesToFirebase();

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void storeNewValuesToFirebase() {
        validation();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User").child(uid);
                databaseReference1.child("name").setValue(editName.getText().toString().trim());
                databaseReference1.child("lastName").setValue(editLastName.getText().toString().trim());
                databaseReference1.child("username").setValue(editUsername.getText().toString().trim());
                databaseReference1.child("email").setValue(editEmail.getText().toString().trim());
                databaseReference1.child("location").setValue(editLocation.getText().toString().trim());
                if(!dwnUrl.isEmpty()) {
                    databaseReference1.child("imageAddress").setValue(dwnUrl);
                }

                finish();

            }
        });
    }

    private void updateImage() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(uid);
        //Picasso.get().load(dwnUrl).fit().centerCrop().into(imageView);
        Glide.with(getApplicationContext())
                .load(dwnUrl)
                .thumbnail(0.05f)
                .into(imageView);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriFile = data.getData();
            uploadImage();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditProfile.this.getContentResolver(), uriFile);
                imageView.setImageBitmap(bitmap);

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void uploadImage() {
        if(uriFile != null){
            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uriFile));
            storageTask = ref.putFile(uriFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //progressBar.setProgress(0);
                        }
                    }, 500);

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    final String sdownload_url = String.valueOf(downloadUrl);
                    dwnUrl = String.valueOf(downloadUrl);
                    updateImage();

                    Toast.makeText(EditProfile.this, "Upload Successful!", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress= (100.0* taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                    //progressBar.setProgress((int) progress);
                }
            });
        }else{
            Toast.makeText(EditProfile.this, "No file selected!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //registerMe.setEnabled(true);
                }
            }, 2000);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = EditProfile.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uri));
    }


    private void validation() {
        String nameS = editName.getText().toString().trim();
        String lastNameS = editLastName.getText().toString().trim();
        String usernameS = editUsername.getText().toString().trim();
        String emailS = editEmail.getText().toString().trim();
        String locationS = editLocation.getText().toString().trim();

        if(TextUtils.isEmpty(nameS)){
            editName.setError("You have to enter your name.");
            return;
        }
        if(TextUtils.isEmpty(lastNameS)){
            editLastName.setError("You have to enter your last name.");
            return;
        }
        if(TextUtils.isEmpty(usernameS)){
            editUsername.setError("You have to enter your username.");
            return;
        }
        if(TextUtils.isEmpty(emailS)){
            editEmail.setError("You have to enter your E-mail.");
            return;
        }
        if(TextUtils.isEmpty(locationS)){
            editLocation.setError("You have to enter your Address.");
            return;
        }
    }

    public void backToProfile(View view) {
        finish();
    }
}
