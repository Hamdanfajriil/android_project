package com.example.lumicotelecommunication.models;

public class Dataclass {
    private String nameproject;
    private String siteid;
    private String sitename;
    private String noPo;
    private String tglsurvei;
    private String alamat;
    private String lattitude;
    private String longtitude;
    private String jtower;
    private String ttower;
    private String plokasi;

    public String getNameproject() {
        return nameproject;
    }

    public String getSiteid() {
        return siteid;
    }

    public String getSitename() {
        return sitename;
    }

    public String getNoPo() {
        return noPo;
    }

    public String getTglsurvei() {
        return tglsurvei;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getLattitude() {
        return lattitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public String getJtower() {
        return jtower;
    }

    public String getTtower() {
        return ttower;
    }

    public String getPlokasi() {
        return plokasi;
    }

    public Dataclass(String nameproject, String siteid, String sitename, String noPo, String tglsurvei, String alamat, String lattitude, String longtitude, String jtower, String ttower, String plokasi) {
        this.nameproject = nameproject;
        this.siteid = siteid;
        this.sitename = sitename;
        this.noPo = noPo;
        this.tglsurvei = tglsurvei;
        this.alamat = alamat;
        this.lattitude = lattitude;
        this.longtitude = longtitude;
        this.jtower = jtower;
        this.ttower = ttower;
        this.plokasi = plokasi;
    }
}