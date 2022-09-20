
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import java.util.List;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutletLoaction implements Serializable, Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("features")
    @Expose
    private List<Feature> features = null;
    public final static Creator<OutletLoaction> CREATOR = new Creator<OutletLoaction>() {


        @SuppressWarnings({
            "unchecked"
        })
        public OutletLoaction createFromParcel(android.os.Parcel in) {
            return new OutletLoaction(in);
        }

        public OutletLoaction[] newArray(int size) {
            return (new OutletLoaction[size]);
        }

    }
    ;
    private final static long serialVersionUID = 3895558605060262789L;

    protected OutletLoaction(android.os.Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.features, (Feature.class.getClassLoader()));
    }

    public OutletLoaction() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeList(features);
    }

    public int describeContents() {
        return  0;
    }

}
