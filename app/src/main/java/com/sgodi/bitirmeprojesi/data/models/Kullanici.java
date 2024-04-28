package com.sgodi.bitirmeprojesi.data.models;

import java.io.Serializable;

public class Kullanici implements Serializable {
    private String ad;
    private String soyad;
    private String email;
    private String kisilik;
    private String konum;
    private String tel;
    private String aciklama;
    private String kisilik_durum;
    private String bakici_durum;
    private Boolean oneri_durum;


    public Kullanici() {
    }

    public Kullanici(String ad, String soyad, String email, String kisilik, String konum, String tel, String aciklama,String kisilik_durum,String bakici_durum
    ,Boolean oneri_durum) {
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.kisilik = kisilik;
        this.konum = konum;
        this.tel = tel;
        this.aciklama = aciklama;
        this.kisilik_durum=kisilik_durum;
        this.bakici_durum=bakici_durum;
        this.oneri_durum=oneri_durum;

    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKisilik() {
        return kisilik;
    }

    public void setKisilik(String kisilik) {
        this.kisilik = kisilik;
    }

    public String getKonum() {
        return konum;
    }

    public void setKonum(String konum) {
        this.konum = konum;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getKisilik_durum() {
        return kisilik_durum;
    }

    public void setKisilik_durum(String kisilik_durum) {
        this.kisilik_durum = kisilik_durum;
    }

    public String getBakici_durum() {
        return bakici_durum;
    }

    public void setBakici_durum(String bakici_durum) {
        this.bakici_durum = bakici_durum;
    }

    public Boolean getOneri_durum() {
        return oneri_durum;
    }

    public void setOneri_durum(Boolean oneri_durum) {
        this.oneri_durum = oneri_durum;
    }
}
