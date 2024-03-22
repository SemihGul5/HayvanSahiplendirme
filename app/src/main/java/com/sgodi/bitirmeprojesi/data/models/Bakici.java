package com.sgodi.bitirmeprojesi.data.models;

import java.io.Serializable;

public class Bakici implements Serializable {
    private String ad;
    private String soyad;
    private String email;
    private String kisilik;
    private String konum;
    private String tel;
    private String aciklama;
    private String foto;
    private String cinsiyet;

    public Bakici() {
    }

    public Bakici(String ad, String soyad, String email, String kisilik, String konum, String tel, String aciklama,String foto,String cinsiyet) {
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.kisilik = kisilik;
        this.konum = konum;
        this.tel = tel;
        this.aciklama = aciklama;
        this.foto=foto;
        this.cinsiyet=cinsiyet;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }
}
