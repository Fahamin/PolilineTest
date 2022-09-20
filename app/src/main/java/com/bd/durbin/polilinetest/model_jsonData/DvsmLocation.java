
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DvsmLocation implements Serializable, Parcelable
{

    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    public final static Creator<DvsmLocation> CREATOR = new Creator<DvsmLocation>() {


        @SuppressWarnings({
            "unchecked"
        })
        public DvsmLocation createFromParcel(android.os.Parcel in) {
            return new DvsmLocation(in);
        }

        public DvsmLocation[] newArray(int size) {
            return (new DvsmLocation[size]);
        }

    }
    ;
    private final static long serialVersionUID = -2318700672678882606L;

    protected DvsmLocation(android.os.Parcel in) {
        this.lat = ((String) in.readValue((String.class.getClassLoader())));
        this.lng = ((String) in.readValue((String.class.getClassLoader())));
    }

    public DvsmLocation() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lng);
    }

    public int describeContents() {
        return  0;
    }

}
