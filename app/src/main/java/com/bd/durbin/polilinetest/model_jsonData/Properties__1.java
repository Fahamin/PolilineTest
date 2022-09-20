
package com.bd.durbin.polilinetest.model_jsonData;

import java.io.Serializable;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties__1 implements Serializable, Parcelable
{

    @SerializedName("Sl")
    @Expose
    private Integer sl;
    @SerializedName("OutletName")
    @Expose
    private String outletName;
    @SerializedName("OutletCode")
    @Expose
    private String outletCode;
    @SerializedName("Address")
    @Expose
    private Object address;
    @SerializedName("ChannelName")
    @Expose
    private String channelName;
    @SerializedName("OutletGrade")
    @Expose
    private String outletGrade;
    @SerializedName("HaveVisicooler")
    @Expose
    private Object haveVisicooler;
    @SerializedName("StartTime")
    @Expose
    private String startTime;
    @SerializedName("EndTime")
    @Expose
    private String endTime;
    @SerializedName("TimeSpend")
    @Expose
    private String timeSpend;
    @SerializedName("VisitType")
    @Expose
    private Integer visitType;
    @SerializedName("Distance")
    @Expose
    private Integer distance;
    @SerializedName("Remark")
    @Expose
    private String remark;
    public final static Creator<Properties__1> CREATOR = new Creator<Properties__1>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Properties__1 createFromParcel(android.os.Parcel in) {
            return new Properties__1(in);
        }

        public Properties__1 [] newArray(int size) {
            return (new Properties__1[size]);
        }

    };
    private final static long serialVersionUID = -5224699251443170698L;

    protected Properties__1(android.os.Parcel in) {
        this.sl = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.outletName = ((String) in.readValue((String.class.getClassLoader())));
        this.outletCode = ((String) in.readValue((String.class.getClassLoader())));
        this.address = ((Object) in.readValue((Object.class.getClassLoader())));
        this.channelName = ((String) in.readValue((String.class.getClassLoader())));
        this.outletGrade = ((String) in.readValue((String.class.getClassLoader())));
        this.haveVisicooler = ((Object) in.readValue((Object.class.getClassLoader())));
        this.startTime = ((String) in.readValue((String.class.getClassLoader())));
        this.endTime = ((String) in.readValue((String.class.getClassLoader())));
        this.timeSpend = ((String) in.readValue((String.class.getClassLoader())));
        this.visitType = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.distance = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.remark = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Properties__1() {
    }

    public Integer getSl() {
        return sl;
    }

    public void setSl(Integer sl) {
        this.sl = sl;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getOutletGrade() {
        return outletGrade;
    }

    public void setOutletGrade(String outletGrade) {
        this.outletGrade = outletGrade;
    }

    public Object getHaveVisicooler() {
        return haveVisicooler;
    }

    public void setHaveVisicooler(Object haveVisicooler) {
        this.haveVisicooler = haveVisicooler;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTimeSpend() {
        return timeSpend;
    }

    public void setTimeSpend(String timeSpend) {
        this.timeSpend = timeSpend;
    }

    public Integer getVisitType() {
        return visitType;
    }

    public void setVisitType(Integer visitType) {
        this.visitType = visitType;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(sl);
        dest.writeValue(outletName);
        dest.writeValue(outletCode);
        dest.writeValue(address);
        dest.writeValue(channelName);
        dest.writeValue(outletGrade);
        dest.writeValue(haveVisicooler);
        dest.writeValue(startTime);
        dest.writeValue(endTime);
        dest.writeValue(timeSpend);
        dest.writeValue(visitType);
        dest.writeValue(distance);
        dest.writeValue(remark);
    }

    public int describeContents() {
        return  0;
    }

}
