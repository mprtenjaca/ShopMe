package com.example.bottomnav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conditionenums.Category;
import com.example.conditionenums.Condition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PostController extends AppCompatActivity {

    EditText title, desc, price, location;
    Button uploadImagesBtn, postaj, postBtn2, camera, backBtn;
    ImageView img1, img2, img3, img4;
    Spinner conditionSpinner, categorySpinner;

    String imageURL;

    List<Bitmap> bitmap = new ArrayList<>();
    List<String> urls;
    List<Uri> uriLista;
    List<ImageView> imageViewList;

    //FIREBASE
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    Uri uri;
    int uriBrojac;
    int pickCounter;
    Uri cameraUri;
    String conditionString;
    String categoryString;

    Condition conditionPost;
    Category categoryPost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_controller);



        title = (EditText) findViewById(R.id.postTitle);
        desc = (EditText) findViewById(R.id.postDesc);
        location = (EditText) findViewById(R.id.postLocation);
        price = (EditText) findViewById(R.id.postPrice);

        uploadImagesBtn = (Button) findViewById(R.id.uploadImagesBtn);
        postaj = (Button) findViewById(R.id.addNewPostBtn);
        postBtn2 = (Button) findViewById(R.id.postBtn2);
        backBtn = (Button) findViewById(R.id.backBtn);
        //camera = (Button) findViewById(R.id.camera);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);

        conditionSpinner = (Spinner) findViewById(R.id.conditionSpinner);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);


        urls = new ArrayList<>();
        uriLista = new ArrayList<>();

        //Add values to Dsc spinner

        List<Condition> conditionList = new ArrayList<>();
        conditionList.addAll(Arrays.asList(Condition.values()));
        ArrayAdapter<Condition> conditionArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, conditionList);
        conditionArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionArrayAdapter);
        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                Condition condition = (Condition) parent.getSelectedItem();
                conditionPost = condition;
                conditionString = condition.getOpis();
                Toast.makeText(getApplicationContext(), "" + condition.getOpis(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //Add values to Dsc spinner
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        List<Category> categoryList = new ArrayList<>();
        List<String> categoryStringList = new ArrayList<>();
        categoryList.addAll(Arrays.asList(Category.values()));


        ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryArrayAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                Category category = (Category) parent.getSelectedItem();
                categoryPost = category;
                categoryString = category.getDescription();
                Toast.makeText(getApplicationContext(), "" + category.getDescription(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        uriBrojac = 0;
        pickCounter = 0;

        //FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
        storageReference = FirebaseStorage.getInstance().getReference("Post");


        uploadImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImages();
            }
        });

        /*
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
         */


        postaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages(uriLista);
            }
        });

        postBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages(uriLista);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Log.d("IMAGES", "ULOGIRAN: " + firebaseAuth.getUid() + " " + firebaseAuth.getCurrentUser().getEmail());

                }else{
                    Log.d("IMAGES", "NIJE ULOGIRAN! ");
                }
            }
        };

        backBtn.setOnClickListener(goBack);

    }



    private void takePhoto() {
        //ContentValues values = new ContentValues();
        //values.put(MediaStore.Images.Media.TITLE, "New Picture");
        //values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        //cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    private void selectImages() {

        Toast.makeText(this, "" + bitmap.size(), Toast.LENGTH_SHORT).show();

        if(ActivityCompat.checkSelfPermission(PostController.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PostController.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select pictures!"), 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if(requestCode == 1){

                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        uriLista.add(imageUri);
                        uri = clipData.getItemAt(i).getUri();

                        try {
                            InputStream is = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap1 = BitmapFactory.decodeStream(is);
                            bitmap.add(bitmap1);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }

                    pickCounter += clipData.getItemCount()-1;
                } else {
                    if(pickCounter > 0){
                        pickCounter++;
                    }
                    Uri imageUri = data.getData();
                    uriLista.add(imageUri);
                    Toast.makeText(this, "Uploading single image...", Toast.LENGTH_SHORT).show();
                    //uploadImages(imageUri);
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap1 = BitmapFactory.decodeStream(is);
                        bitmap.add(bitmap1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }else if(requestCode == 2){
                Bitmap bit = (Bitmap) data.getExtras().get("data");
                //bitmap.add(bit);
                //img2.setImageBitmap(bit);
                Uri pic = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(pic);
                    Bitmap bitmap1 = BitmapFactory.decodeStream(is);
                    img2.setImageBitmap(bitmap1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            for(int i = 0; i < bitmap.size(); i++){
                if(i == 0 && img1.getDrawable() == null){
                    img1.setImageBitmap(bitmap.get(i));
                }
                if(i == 1 && img2.getDrawable() == null){
                    img2.setImageBitmap(bitmap.get(i));
                }
                if(i == 2 && img3.getDrawable() == null){
                    img3.setImageBitmap(bitmap.get(i));
                }
                if(i == 3 && img4.getDrawable() == null){
                    img4.setImageBitmap(bitmap.get(i));
                }
            }

        }



    }

    private void uploadImages(final List<Uri> listaUri) {

        for(Uri imageUri : listaUri) {
            if (imageUri != null) {
                StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                ref.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Here you store the uploaded Image URL as a string
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();

                                final String sdownload_url = String.valueOf(downloadUrl);
                                imageURL = String.valueOf(downloadUrl);
                                urls.add(String.valueOf(downloadUrl));

                                uriBrojac++;
                                Toast.makeText(PostController.this, "Images Uploaded!", Toast.LENGTH_SHORT).show();
                                if(uriBrojac == uriLista.size()){
                                    post();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PostController.this, "Failed to upload images!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            }
                        });
            }
        }


    }


    private void post() {

        double cijenaDouble = Double.parseDouble(price.getText().toString());
        String datum;
        LocalDate today = LocalDate.now();
        datum = today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));

        Post post = new Post(firebaseAuth.getUid(), firebaseAuth.getCurrentUser().getEmail(), title.getText().toString().toLowerCase(), desc.getText().toString(), location.getText().toString(), conditionPost, categoryPost, price.getText().toString(), datum);

        post.setImageLinks(urls);

        long restauCount = System.currentTimeMillis();
        String identifier ="post" + restauCount;

        FirebaseDatabase.getInstance().getReference("Post")
                .child(identifier)
                .setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(PostController.this, "POSTED", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    private View.OnClickListener goBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

}
