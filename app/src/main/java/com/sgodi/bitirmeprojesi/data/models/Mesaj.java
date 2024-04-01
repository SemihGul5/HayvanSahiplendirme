package com.sgodi.bitirmeprojesi.data.models;

public class Mesaj {
    private String alici_ad;
    private String alici_email;
    private String gonderen_ad;
    private String gonderen_email;
    private String mesaj;
    private String okunduMu;

    public Mesaj() {
    }

    public Mesaj(String alici_ad, String alici_email, String gonderen_ad, String gonderen_email, String mesaj, String okunduMu) {
        this.alici_ad = alici_ad;
        this.alici_email = alici_email;
        this.gonderen_ad = gonderen_ad;
        this.gonderen_email = gonderen_email;
        this.mesaj = mesaj;
        this.okunduMu = okunduMu;
    }

    public String getAlici_ad() {
        return alici_ad;
    }

    public void setAlici_ad(String alici_ad) {
        this.alici_ad = alici_ad;
    }

    public String getAlici_email() {
        return alici_email;
    }

    public void setAlici_email(String alici_email) {
        this.alici_email = alici_email;
    }

    public String getGonderen_ad() {
        return gonderen_ad;
    }

    public void setGonderen_ad(String gonderen_ad) {
        this.gonderen_ad = gonderen_ad;
    }

    public String getGonderen_email() {
        return gonderen_email;
    }

    public void setGonderen_email(String gonderen_email) {
        this.gonderen_email = gonderen_email;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getOkunduMu() {
        return okunduMu;
    }

    public void setOkunduMu(String okunduMu) {
        this.okunduMu = okunduMu;
    }
}
