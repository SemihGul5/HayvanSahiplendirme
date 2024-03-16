package com.sgodi.bitirmeprojesi.data.models;

import android.content.Context;

import java.util.List;

public class AnasayfaIcerik {
    private String resim;
    private String baslik;
    private String icerik;
    private int siralama;

    public AnasayfaIcerik() {
    }

    public AnasayfaIcerik(String resim, String baslik, String icerik,int siralama) {
        this.resim = resim;
        this.baslik = baslik;
        this.icerik = icerik;
        this.siralama=siralama;
    }

    public int getSiralama() {
        return siralama;
    }

    public void setSiralama(int siralama) {
        this.siralama = siralama;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }
}
