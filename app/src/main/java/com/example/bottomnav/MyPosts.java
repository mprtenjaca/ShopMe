package com.example.bottomnav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyPosts extends AppCompatActivity {

    Button backBtn;

    LinearLayoutManager mlinearLayoutManager;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference queryRef;
    FirebaseRecyclerAdapter<Post, ViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Post> options;

    List<String> keyList;


    SearchView searchV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        backBtn = (Button) findViewById(R.id.backToProfileBtn);

        mlinearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mlinearLayoutManager.setReverseLayout(true);
        mlinearLayoutManager.setStackFromEnd(true);

        recyclerView = (RecyclerView) findViewById(R.id.myRecycleView);

        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        keyList = new ArrayList<>();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Post");
        queryRef = databaseReference;




        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        showData();

       
    }

    public void showData() {

        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query filterQuery = databaseReference.orderByChild("uid").startAt(id).endAt(id + "\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(filterQuery, Post.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Post model) {

                keyList.add(getRef(position).getKey());

                holder.setDetails(MyPosts.this, capitalize(model.getName()), model.getDescription(), model.getPrice(), model.getImageLinks().get(0));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        Intent intent = new Intent(MyPosts.this, PostItemActivity.class);
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

    public void back(View view) {
    }
}
