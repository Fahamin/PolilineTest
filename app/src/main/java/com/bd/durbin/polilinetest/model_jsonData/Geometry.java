
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import java.util.List;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry implements Serializable, Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private List<String> coordinates = null;
    public final static Creator<Geometry> CREATOR = new Creator<Geometry>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Geometry createFromParcel(android.os.Parcel in) {
            return new Geometry(in);
        }

        public Geometry[] newArray(int size) {
            return (new Geometry[size]);
        }

    }
    ;
    private final static long serialVersionUID = -8134741697492672785L;

    protected Geometry(android.os.Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.coordinates, (String.class.getClassLoader()));
    }

    public Geometry() {
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
