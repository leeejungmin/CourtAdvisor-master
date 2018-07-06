package com.edu.pc.courtadvisor;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Playground implements Parcelable {

    public int id;
    public String idF;
    private String address;
    private Double lat;
    private Double lng;
    private int numberOfHoops;
    private String image;

    public Playground() {

    }

    public Playground(int id, String address, Double lat, Double lng, int numberOfHoops) {
        this.id = id;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.numberOfHoops = numberOfHoops;
    }

    protected Playground(Parcel in) {
        this.id = in.readInt();
        this.idF = in.readString();
        this.address = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.numberOfHoops = in.readInt();
        this.image = in.readString();
        //picture = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Playground> CREATOR = new Creator<Playground>() {
        @Override
        public Playground createFromParcel(Parcel in) {
            return new Playground(in);
        }

        @Override
        public Playground[] newArray(int size) {
            return new Playground[size];
        }
    };

    /*
     * GETTERS
     */

    public int getId() {
        return id;
    }
    public String getAddress() {
        return address;
    }
    public Double getLat() { return lat; }
    public Double getLng() { return lng; }
    public int getNumberOfHoops() {
        return numberOfHoops;
    }
    public String getImage() {
        return image;
    }

    /*
    * SETTERS
    */
    public void setId(int id){
        this.id = id;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
    public void setNumberOfHoops(int numberOfHoops) {
        this.numberOfHoops = numberOfHoops;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public void setIdFirebase(String id) {
        this.idF = id;
    }

    public String getIdF() {
        return idF;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(idF);
        parcel.writeString(address);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeInt(numberOfHoops);
        parcel.writeString(image);
    }


}
