
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import android.os.Parcelable;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature implements Serializable, Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("properties")
    @Expose
    private Properties properties;
    public final static Creator<Feature> CREATOR = new Creator<Feature>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Feature createFromParcel(android.os.Parcel in) {
            return new Feature(in);
        }

        public Feature[] newArray(int size) {
            return (new Feature[size]);
        }

    }
    ;
    private final static long serialVersionUID = 240048605444750231L;

    protected Feature(android.os.Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.geometry = ((Geometry) in.readValue((Geometry.class.getClassLoader())));
        this.properties = ((Properties) in.readValue((Properties.class.getClassLoader())));
    }

    public Feature() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(geometry);
        dest.writeValue(properties);
    }

    public int describeContents() {
        return  0;
    }

}
