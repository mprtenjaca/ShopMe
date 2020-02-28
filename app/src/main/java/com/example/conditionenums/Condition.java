package com.example.conditionenums;

public enum Condition {
    
    NOVO(1, "NOVO"),
    IZVRSNO(2, "IZVRSNO"),
    RABLJENO(3, "RABLJENO"),
    NEISPRAVNO(4, "RABLJENO");

    private Integer kod;
    private String opis;

    Condition(Integer kod, String opis) {
        this.kod = kod;
        this.opis = opis;
    }

    public Integer getKod() {
        return kod;
    }

    public String getOpis() {
        return opis;
    }
    
}
