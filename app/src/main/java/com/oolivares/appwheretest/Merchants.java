package com.oolivares.appwheretest;

import android.os.Parcel;
import android.os.Parcelable;

public class Merchants implements Parcelable {
    private String id;
    private String merchantName;
    private String merchantAddress;
    private String merchantTelephone;
    private Double latitude;
    private Double longitude;
    private String registrationDate;

    public Merchants(String id, String merchantName, String merchantAddress, String merchantTelephone, Double latitude, Double longitude, String registrationDate) {
        this.id = id;
        this.merchantName = merchantName;
        this.merchantAddress = merchantAddress;
        this.merchantTelephone = merchantTelephone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.registrationDate = registrationDate;
    }

    protected Merchants(Parcel in) {
        id = in.readString();
        merchantName = in.readString();
        merchantAddress = in.readString();
        merchantTelephone = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        registrationDate = in.readString();
    }

    public static final Creator<Merchants> CREATOR = new Creator<Merchants>() {
        @Override
        public Merchants createFromParcel(Parcel in) {
            return new Merchants(in);
        }

        @Override
        public Merchants[] newArray(int size) {
            return new Merchants[size];
        }
    };

    public Merchants() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getMerchantTelephone() {
        return merchantTelephone;
    }

    public void setMerchantTelephone(String merchantTelephone) {
        this.merchantTelephone = merchantTelephone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(merchantName);
        parcel.writeString(merchantAddress);
        parcel.writeString(merchantTelephone);
        if (latitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(latitude);
        }
        if (longitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(longitude);
        }
        parcel.writeString(registrationDate);
    }
}
