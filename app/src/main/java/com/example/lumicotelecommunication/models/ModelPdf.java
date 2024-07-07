package com.example.lumicotelecommunication.models;

public class ModelPdf {

    String uid, id,  projectId, name_site, tanggal_survei, product, url;
    long timestamp, viewsCount, downloadCount;



    public ModelPdf() {


    }

    public ModelPdf(String uid, String id, String projectId, String name_site, String tanggal_survei, String product, String url, long timestamp, long viewsCount, long downloadCount) {
        this.uid = uid;
        this.id = id;
        this.projectId = projectId;
        this.name_site = name_site;
        this.tanggal_survei = tanggal_survei;
        this.product = product;
        this.url = url;
        this.timestamp = timestamp;
        this.viewsCount = viewsCount;
        this.downloadCount = downloadCount;
    }

   //setter and getter

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName_site() {
        return name_site;
    }

    public void setName_site(String name_site) {
        this.name_site = name_site;
    }

    public String getTanggal_survei() {
        return tanggal_survei;
    }

    public void setTanggal_survei(String tanggal_survei) {
        this.tanggal_survei = tanggal_survei;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(long downloadCount) {
        this.downloadCount = downloadCount;
    }
}
