package com.example.bottomnav;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;

import com.example.bottomnav.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Favourite extends AppCompatActivity {

    ImageView favouriteImg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


        favouriteImg = (ImageView) findViewById(R.id.favouriteImg);

        favouriteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.get().load(R.drawable.favouriteclick).into(favouriteImg);
            }
        });

    }
}
