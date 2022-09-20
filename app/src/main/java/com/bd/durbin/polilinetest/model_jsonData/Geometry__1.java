
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import java.util.List;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry__1 implements Serializable, Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private List<String> coordinates = null;
    public final static Creator<Geometry__1> CREATOR = new Creator<Geometry__1>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Geometry__1 createFromParcel(android.os.Parcel in) {
            return new Geometry__1(in);
        }

        public Geometry__1 [] newArray(int size) {
            return (new Geometry__1[size]);
        }

    }
    ;
    private final static long serialVersionUID = -417018372921645505L;

    protected Geometry__1(android.os.Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.coordinates, (String.class.getClassLoader()));
    }

    public Geometry__1() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeList(coordinates);
    }

    public int describeContents() {
        return  0;
    }

}
