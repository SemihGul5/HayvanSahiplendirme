package com.sgodi.bitirmeprojesi.data.models;

import java.io.Serializable;

public class Hayvan implements Serializable {
    private String email;
    private String foto1;
    private String foto2;
    private String foto3;
    private String foto4;
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
    private String ilandaMi;
    private String enlem;
    private String boylam;
    private String sehir;
    private String ilce;

    public Hayvan() {
    }

    public Hayvan(String email, String foto1,String foto2,String foto3,String foto4, String ad, String tur,
                  String irk, String cinsiyet, String yas, String saglik,
                  String aciklama, String kisilik,String docID,String sahipliMi,
                  String ilandaMi, String enlem,String boylam, String sehir,String ilce) {
        this.email = email;
        this.foto1 = foto1;
        this.foto2 = foto2;
        this.foto3 = foto3;
        this.foto4 = foto4;
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
        this.ilandaMi=ilandaMi;
        this.enlem=enlem;
        this.boylam=boylam;
        this.sehir=sehir;
        this.ilce=ilce;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public String getFoto4() {
        return foto4;
    }

    public void setFoto4(String foto4) {
        this.foto4 = foto4;
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

    public String getIlandaMi() {
        return ilandaMi;
    }

    public void setIlandaMi(String ilandaMi) {
        this.ilandaMi = ilandaMi;
    }

    public String getEnlem() {
        return enlem;
    }

    public void setEnlem(String enlem) {
        this.enlem = enlem;
    }

    public String getBoylam() {
        return boylam;
    }

    public void setBoylam(String boylam) {
        this.boylam = boylam;
    }

    public String getSehir() {
        return sehir;
    }

    public void setSehir(String sehir) {
        this.sehir = sehir;
    }

    public String getIlce() {
        return ilce;
    }

    public void setIlce(String ilce) {
        this.ilce = ilce;
    }
}
