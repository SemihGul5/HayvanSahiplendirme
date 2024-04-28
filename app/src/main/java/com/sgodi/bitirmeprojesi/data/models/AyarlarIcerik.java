package com.sgodi.bitirmeprojesi.data.models;

public class AyarlarIcerik {
    private String baslik;
    private Boolean switchDurum;

    public AyarlarIcerik(String baslik, Boolean switchDurum) {
        this.baslik = baslik;
        this.switchDurum = switchDurum;
    }

    public AyarlarIcerik() {
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public Boolean getSwitchDurum() {
        return switchDurum;
    }

    public void setSwitchDurum(Boolean switchDurum) {
        this.switchDurum = switchDurum;
    }
}
