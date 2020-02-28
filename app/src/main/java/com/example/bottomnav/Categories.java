package com.example.bottomnav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.conditionenums.Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Categories extends AppCompatActivity {

    LinearLayoutManager mlinearLayoutManager;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<Post, ViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Post> options;


    Button backBtn;

    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


        category = getIntent().getStringExtra("category");

        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mlinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mlinearLayoutManager.setReverseLayout(true);
        mlinearLayoutManager.setStackFromEnd(true);

        recyclerView = (RecyclerView) findViewById(R.id.myRecycleView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Post");


        showData();


       /* databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


    }

    private void showData() {

        final String id = category;
        Query filterQuery = databaseReference.orderByChild("category").startAt(id).endAt(id + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(filterQuery, Post.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Post model) {

                //keyList.add(getRef(position).getKey());

                holder.setDetails(Categories.this, capitalize(model.getName()), model.getDescription(), model.getPrice(), model.getImageLinks().get(0));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        Intent intent = new Intent(Categories.this, PostItemActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);

                ViewHolder viewHolder = new ViewHolder(itemView);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), "Hello! " + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), PostItemActivity.class);
                        intent.putExtra("naslov", position);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), "Long Click!", Toast.LENGTH_SHORT).show();
                    }
                });

                return viewHolder;
            }
        };

        recyclerView.setLayoutManager(mlinearLayoutManager);
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }

    private void fetchData(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if(ds.child("category").equals(category)){
                Toast.makeText(this, "da", Toast.LENGTH_SHORT).show();
                Post post = ds.getValue(Post.class);


            }
        }

    }
    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
        return str;
    }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public void onStart(){
        super.onStart();

        if(firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening();
        }

    }

}
