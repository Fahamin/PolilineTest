package com.bd.durbin.polilinetest.OflineLocationDB.model;

public class OfflineLocationModel {

    int id;
    String jsondata;

    public OfflineLocationModel() {
    }

    public OfflineLocationModel(int id, String jsondata) {
        this.id = id;
        this.jsondata = jsondata;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsondata() {
        return jsondata;
    }

    public void setJsondata(String jsondata) {
        this.jsondata = jsondata;
    }
}
