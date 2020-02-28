package com.example.bottomnav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static android.content.Context.MODE_PRIVATE;

public class ShopFragment extends Fragment {


    LinearLayoutManager mlinearLayoutManager;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<Post, ViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Post> options;


    SearchView searchV;
    Button refreshBtn;

    SharedPreferences prefs = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        prefs = getContext().getSharedPreferences("Glavna1", MODE_PRIVATE);

        mlinearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        mlinearLayoutManager.setReverseLayout(true);
        mlinearLayoutManager.setStackFromEnd(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.myRecycleView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Post");



        searchV = (SearchView) view.findViewById(R.id.searchView);
        searchV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });


        /*
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("firstrun", true).commit();
            }
        });


        if (prefs.getBoolean("firstrun", true)) {

            Toast.makeText(getContext(), "First in fragment", Toast.LENGTH_SHORT).show();
            showData();

            prefs.edit().putBoolean("firstrun", false).commit();

        }
         */
        showData();

        return view;
    }

    public void showData() {
        //Toast.makeText(getContext(), "DIDIT", Toast.LENGTH_SHORT).show();
        options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(databaseReference, Post.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Post model) {

                holder.setDetails(getContext(), capitalize(model.getName()), model.getDescription(), model.getPrice() + " kn", model.getImageLinks().get(0));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        Intent intent = new Intent(getContext(), PostItemActivity.class);
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
                        Toast.makeText(getContext(), "Hello! " + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), PostItemActivity.class);
                        intent.putExtra("naslov", position);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(getContext(), "Long Click!", Toast.LENGTH_SHORT).show();
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

    private void firebaseSearch(String search){

        String query = search;

        Query firebaseSearchQuery = databaseReference.orderByChild("name").startAt(query).endAt(query + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(firebaseSearchQuery, Post.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Post model) {
                holder.setDetails(getContext(), model.getName(), model.getDescription(), model.getPrice(), model.getImageLinks().get(0));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        Intent intent = new Intent(getContext(), PostItemActivity.class);
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
                        Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        Toast.makeText(getContext(), "Long Click", Toast.LENGTH_SHORT).show();
                    }
                });

                return viewHolder;
            }
        };

        recyclerView.setLayoutManager(mlinearLayoutManager);
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public void onStart(){
        super.onStart();

        if(firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }
}
