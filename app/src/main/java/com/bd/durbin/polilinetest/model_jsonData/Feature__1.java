
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature__1 implements Serializable, Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("geometry")
    @Expose
    private Geometry__1 geometry;
    @SerializedName("properties")
    @Expose
    private Properties__1 properties;
    public final static Creator<Feature__1> CREATOR = new Creator<Feature__1>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Feature__1 createFromParcel(android.os.Parcel in) {
            return new Feature__1(in);
        }

        public Feature__1 [] newArray(int size) {
            return (new Feature__1[size]);
        }

    }
    ;
    private final static long serialVersionUID = 8890799401858608053L;

    protected Feature__1(android.os.Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.geometry = ((Geometry__1) in.readValue((Geometry__1.class.getClassLoader())));
        this.properties = ((Properties__1) in.readValue((Properties__1.class.getClassLoader())));
    }

    public Feature__1() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry__1 getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry__1 geometry) {
        this.geometry = geometry;
    }

    public Properties__1 getProperties() {
        return properties;
    }

    public void setProperties(Properties__1 properties) {
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
