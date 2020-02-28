package com.example.bottomnav;


import android.net.Uri;

import com.example.conditionenums.Category;
import com.example.conditionenums.Condition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Post {

    public String uid;
    public String seller;
    public String name;
    public String description;
    public String city;
    public String price;
    public String date;
    public Condition condition;
    public Category category;
    public List<String> imageLinks;


    public Post(String uid, String seller, String name, String description, String city, Condition condition, Category category, String price, String date) {
        this.uid = uid;
        this.seller = seller;
        this.name = name;
        this.description = description;
        this.city = city;
        this.condition = condition;
        this.category = category;
        this.price = price;
        this.date = date;

        this.imageLinks = new ArrayList<>();
    }

    public  Post(){}

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(List<String> imageLinks) {
        this.imageLinks = imageLinks;
    }
}
