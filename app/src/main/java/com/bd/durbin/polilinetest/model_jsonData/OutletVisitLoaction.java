
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import java.util.List;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OutletVisitLoaction implements Serializable, Parcelable
{

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("features")
    @Expose
    private List<Feature__1> features = null;
    public final static Creator<OutletVisitLoaction> CREATOR = new Creator<OutletVisitLoaction>() {


        @SuppressWarnings({
            "unchecked"
        })
        public OutletVisitLoaction createFromParcel(android.os.Parcel in) {
            return new OutletVisitLoaction(in);
        }

        public OutletVisitLoaction[] newArray(int size) {
            return (new OutletVisitLoaction[size]);
        }

    };
    private final static long serialVersionUID = 4787541266177596840L;

    protected OutletVisitLoaction(android.os.Parcel in) {
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.features, (Feature__1.class.getClassLoader()));
    }

    public OutletVisitLoaction() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature__1> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature__1> features) {
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
