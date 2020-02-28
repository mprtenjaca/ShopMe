package com.example.conditionenums;

public enum Category {

    MOBILEPHONES(1, "MOBILE PHONES"),
    CARS(2, "CARS"),
    ALLFORHOME(3, "ALLFORHOME"),
    TECHNOLOGY(4, "TECHNOLOGY"),
    FOODANDDRINKS(5, "FOODANDDRINKS"),
    SERVICES(6, "SERVICES"),
    APPAREL(7, "APPAREL"),
    BEAUTYANDSKINCARE(8, "BEAUTYANDSKINCARE"),
    GIFTS(9, "GIFTS"),
    OTHERS(10,"OTHERS");

    private Integer code;
    private  String description;

    Category(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
