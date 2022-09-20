
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import java.util.List;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Outlet implements Serializable, Parcelable
{

    @SerializedName("OutletLoaction")
    @Expose
    private OutletLoaction outletLoaction;
    @SerializedName("OutletVisitLoaction")
    @Expose
    private OutletVisitLoaction outletVisitLoaction;
    @SerializedName("DvsmLocation")
    @Expose
    private List<DvsmLocation> dvsmLocation = null;
    public final static Creator<Outlet> CREATOR = new Creator<Outlet>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Outlet createFromParcel(android.os.Parcel in) {
            return new Outlet(in);
        }

        public Outlet[] newArray(int size) {
            return (new Outlet[size]);
        }

    }
    ;
    private final static long serialVersionUID = -7371842197898580946L;

    protected Outlet(android.os.Parcel in) {
        this.outletLoaction = ((OutletLoaction) in.readValue((OutletLoaction.class.getClassLoader())));
        this.outletVisitLoaction = ((OutletVisitLoaction) in.readValue((OutletVisitLoaction.class.getClassLoader())));
        in.readList(this.dvsmLocation, (DvsmLocation.class.getClassLoader()));
    }

    public Outlet() {
    }

    public OutletLoaction getOutletLoaction() {
        return outletLoaction;
    }

    public void setOutletLoaction(OutletLoaction outletLoaction) {
        this.outletLoaction = outletLoaction;
    }

    public OutletVisitLoaction getOutletVisitLoaction() {
        return outletVisitLoaction;
    }

    public void setOutletVisitLoaction(OutletVisitLoaction outletVisitLoaction) {
        this.outletVisitLoaction = outletVisitLoaction;
    }

    public List<DvsmLocation> getDvsmLocation() {
        return dvsmLocation;
    }

    public void setDvsmLocation(List<DvsmLocation> dvsmLocation) {
        this.dvsmLocation = dvsmLocation;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(outletLoaction);
        dest.writeValue(outletVisitLoaction);
        dest.writeList(dvsmLocation);
    }

    public int describeContents() {
        return  0;
    }

}
