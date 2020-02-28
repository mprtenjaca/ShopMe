package com.example.bottomnav;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;

    TextView mTitle, mDesc, mPrice, mDate;
    ImageView mImage;


    public ViewHolder(@NonNull View itemView) {
        super(itemView);



        view = itemView;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mClickListener.onItemLongClick(v, getAdapterPosition());

                return true;
            }
        });

    }

    public void setDetails(Context ctx, String title, String description, String price, String image){


        mTitle = (TextView) view.findViewById(R.id.naslovView);
        mDesc = (TextView) view.findViewById(R.id.opisView);
        mPrice = (TextView) view.findViewById(R.id.cijenaView);
        mImage = (ImageView) view.findViewById(R.id.imagePost);


        mTitle.setText(title);
        mDesc.setText(description);
        mPrice.setText(price);
        //Picasso.get().load(image).into(mImage);
        Glide.with(ctx.getApplicationContext())
                .load(image)
                .thumbnail(0.05f)
                .into(mImage);

    }

    private ViewHolder.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);

    }

    public void setOnClickListener(ViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }

}
