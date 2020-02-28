package com.example.bottomnav;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.conditionenums.Category;

public class HomeFragment extends Fragment {

    LinearLayout mobilePhones, foodnDrins, services, tech, cars, allForHome, apparel, beauty, gifts, others;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mobilePhones = (LinearLayout) view.findViewById(R.id.mobilePhonesCategory);
        cars = (LinearLayout) view.findViewById(R.id.carsCategory);
        allForHome = (LinearLayout) view.findViewById(R.id.allforhomeCategory);
        tech = (LinearLayout) view.findViewById(R.id.techCategory);
        foodnDrins = (LinearLayout) view.findViewById(R.id.foodanddrinksCategory);
        services = (LinearLayout) view.findViewById(R.id.servicesCategory);
        apparel = (LinearLayout) view.findViewById(R.id.apparelCategory);
        beauty = (LinearLayout) view.findViewById(R.id.beautyCategory);
        gifts = (LinearLayout) view.findViewById(R.id.giftsCategory);
        others = (LinearLayout) view.findViewById(R.id.othersCategory);



        mobilePhones.setOnClickListener(categoryListener);
        cars.setOnClickListener(categoryListener);
        allForHome.setOnClickListener(categoryListener);
        tech.setOnClickListener(categoryListener);
        foodnDrins.setOnClickListener(categoryListener);
        services.setOnClickListener(categoryListener);
        apparel.setOnClickListener(categoryListener);
        beauty.setOnClickListener(categoryListener);
        gifts.setOnClickListener(categoryListener);
        others.setOnClickListener(categoryListener);


        return view;
    }

    private View.OnClickListener categoryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.mobilePhonesCategory:
                    String category = Category.valueOf("MOBILEPHONES").getDescription();
                    openCategories(category);
                    break;
                case R.id.carsCategory:
                    String category1 = Category.valueOf("CARS").getDescription();
                    openCategories(category1);
                    break;
                case R.id.allforhomeCategory:
                    String category2 = Category.valueOf("ALLFORHOME").getDescription();
                    openCategories(category2);
                    break;
                case R.id.techCategory:
                    String category3 = Category.valueOf("TECHNOLOGY").getDescription();
                    openCategories(category3);
                    break;
                case R.id.foodanddrinksCategory:
                    String category4 = Category.valueOf("FOODANDDRINKS").getDescription();
                    openCategories(category4);
                    break;
                case R.id.servicesCategory:
                    String category5 = Category.valueOf("SERVICES").getDescription();
                    openCategories(category5);
                    break;
                case R.id.apparelCategory:
                    String category6 = Category.valueOf("APPAREL").getDescription();
                    openCategories(category6);
                    break;
                case R.id.beautyCategory:
                    String category7 = Category.valueOf("BEAUTYANDSKINCARE").getDescription();
                    openCategories(category7);
                    break;
                case R.id.giftsCategory:
                    String category8 = Category.valueOf("GIFTS").getDescription();
                    openCategories(category8);
                    break;
                case R.id.othersCategory:
                    String category9 = Category.valueOf("OTHERS").getDescription();
                    openCategories(category9);
                    break;

            }
        }
    };


    public void openCategories(String category){
        Intent intent = new Intent(getContext(), Categories.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

}

