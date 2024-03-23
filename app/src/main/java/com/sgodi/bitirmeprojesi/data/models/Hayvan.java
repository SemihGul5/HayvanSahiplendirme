package com.sgodi.bitirmeprojesi.data.models;

import java.io.Serializable;

public class Hayvan implements Serializable {
    private String email;
    private String foto;
    private String ad;
    private String tur;
    private String irk;
    private String cinsiyet;
    private String yas;
    private String saglik;
    private String aciklama;
    private String kisilik;
    private String docID;
    private String sahipliMi;

    public Hayvan() {
    }

    public Hayvan(String email, String foto, String ad, String tur,
                  String irk, String cinsiyet, String yas, String saglik,
                  String aciklama, String kisilik,String docID,String sahipliMi) {
        this.email = email;
        this.foto = foto;
        this.ad = ad;
        this.tur = tur;
        this.irk = irk;
        this.cinsiyet = cinsiyet;
        this.yas = yas;
        this.saglik = saglik;
        this.aciklama = aciklama;
        this.kisilik = kisilik;
        this.docID=docID;
        this.sahipliMi=sahipliMi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getTur() {
        return tur;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public String getIrk() {
        return irk;
    }

    public void setIrk(String irk) {
        this.irk = irk;
    }

    public String getCinsiyet() {
        return cinsiyet;
    }

    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    public String getYas() {
        return yas;
    }

    public void setYas(String yas) {
        this.yas = yas;
    }

    public String getSaglik() {
        return saglik;
    }

    public void setSaglik(String saglik) {
        this.saglik = saglik;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getKisilik() {
        return kisilik;
    }

    public void setKisilik(String kisilik) {
        this.kisilik = kisilik;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getSahipliMi() {
        return sahipliMi;
    }

    public void setSahipliMi(String sahipliMi) {
        this.sahipliMi = sahipliMi;
    }
}
