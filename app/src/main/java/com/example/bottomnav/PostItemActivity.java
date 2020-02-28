package com.example.bottomnav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class PostItemActivity extends AppCompatActivity {

    //FXML
    TextView naslov, cijena, lokacija, stanje, kategorija, opis, idPost, datum, usernamePost;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        //FXML
        naslov = (TextView) findViewById(R.id.naslovOglasa);
        cijena = (TextView) findViewById(R.id.cijenaOglasa);
        lokacija = (TextView) findViewById(R.id.lokacijaOglasa);
        stanje = (TextView) findViewById(R.id.contitionValue);
        kategorija = (TextView) findViewById(R.id.categoryValue);
        opis = (TextView) findViewById(R.id.opisOglasa);
        idPost = (TextView) findViewById(R.id.idOglasa);
        datum = (TextView) findViewById(R.id.datumOglasa);
        usernamePost = (TextView) findViewById(R.id.usernameOglas);

        String id = getIntent().getStringExtra("id");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post").child(id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                getIncomingIntent(post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PostItemActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIncomingIntent(Post post){

        ViewPager viewPager = findViewById(R.id.pager);
        ImagePostAdapter adapter = new ImagePostAdapter(this, post.getImageLinks());
        viewPager.setAdapter(adapter);

        naslov.setText(capitalize(post.getName()));
        opis.setText(post.getDescription());
        cijena.setText(post.getPrice() + " kn");
        lokacija.setText(post.getCity());
        stanje.setText(post.getCondition().getOpis());
        kategorija.setText(post.getCategory().getDescription());
        datum.setText(post.getDate());
        idPost.setText(post.getUid());
        usernamePost.setText(post.getSeller());

    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
