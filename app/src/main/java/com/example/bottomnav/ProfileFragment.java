package com.example.bottomnav;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = "MyActivity";
    private static final int PICK_IMAGE = 71;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    StorageReference storageReference;
    String uid;

    TextView imeIPrezimeView, usernameView, emailView;
    ImageView slika;
    Button postBtn, myPostsBtn, logOutBtn, editProfile;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imeIPrezimeView = (TextView) view.findViewById(R.id.imeIprezimeView);
        usernameView = (TextView) view.findViewById(R.id.usernameView);
        emailView = (TextView) view.findViewById(R.id.emailView);
        slika = (ImageView) view.findViewById(R.id.imageView);
        postBtn = (Button) view.findViewById(R.id.postBtn);
        myPostsBtn = (Button) view.findViewById(R.id.myPostsBtn);
        logOutBtn = (Button) view.findViewById(R.id.logOutBtn);
        editProfile = (Button) view.findViewById(R.id.editProfile);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference("User");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Log.d(TAG, "ULOGIRAN: " + firebaseAuth.getUid() + " " + firebaseAuth.getCurrentUser().getEmail());
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseAuth.getUid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            fetchData(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    Log.d(TAG, "NIJE ULOGIRAN! ");
                }
            }
        };

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), PostController.class);
                startActivity(intent);
            }
        });

        myPostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opeMyPostsActivity();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutAlert("Log Out", "Are you sure you want to log out?");
            }
        });


        return view;
    }

    private void logOutAlert(final String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close

                        if(title.equals("Log Out")) {
                            Activity act = getActivity();
                            if (act instanceof MainActivity) {
                                ((MainActivity) act).logOut();
                            }
                        }if(title.equals("Profile image")){
                            //
                        }
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private  void opeMyPostsActivity(){
        Intent intent = new Intent(getActivity().getApplicationContext(), MyPosts.class);
        startActivity(intent);
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        imeIPrezimeView.setText(user.getName() + " " + user.getLastName());
        usernameView.setText(user.getUsername());
        String mail = "<font color=\"#D3D3D3\">EMAIL</font>";

        emailView.setText(Html.fromHtml(mail) + " \n");
        emailView.append(user.getEmail());
        emailView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        if(!user.getImageAddress().isEmpty()){
            Picasso.get().load(user.getImageAddress()).fit().centerCrop().into(slika);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void post(){
        Intent intent = new Intent(getActivity().getApplicationContext(), PostController.class);
        startActivity(intent);
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
    }


}
