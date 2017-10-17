package edu.gatech.cs2340.rattracker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kevinliao on 10/7/17.
 */

public class RatReport implements Parcelable {
    private String dateCreated;
    private String locationType;
    private double incidentZip;
    private String incidentAddress;
    private String city;
    private String borough;
    private double latitude;
    private double longitude;

    /**
     * No arg constructor for FirebaseUI
     */
    public RatReport() {}

    /**
     * Constructor to create Rat report with specific attributes
     *
     * @param dateCreated the date the rat report was created
     * @param locationType the type of location where the rat was sighted
     * @param incidentZip the zipcode of the location where the rat was sighted
     * @param incidentAddress the address of the location where the rat was sighted
     * @param city the city where the rat was sighted
     * @param borough the borough where the rat was sighted
     * @param latitude the latitude of the coordinates where the rat was sighted
     * @param longitude the longitude of the coordinates where the rat was sighted
     */
    public RatReport(String dateCreated, String locationType, double incidentZip,
                     String incidentAddress, String city, String borough, double latitude,
                     double longitude) {
        this.dateCreated = dateCreated;
        this.locationType = locationType;
        this.incidentZip = incidentZip;
        this.incidentAddress = incidentAddress;
        this.city = city;
        this.borough = borough;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getDateCreated() {
        return this.dateCreated;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public double getIncidentZip() {
        return this.incidentZip;
    }

    public String getIncidentAddress() {
        return this.incidentAddress;
    }

    public String getCity() {
        return this.city;
    }

    public String getBorough() {
        return this.borough;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }


    // Setters
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public void setIncidentZip(double incidentZip) {
        this.incidentZip = incidentZip;
    }

    public void setIncidentAddress(String incidentAddress) {
        this.incidentAddress = incidentAddress;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setBorough(String borough) {
        this.borough = borough;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    //toString
    @Override
    public String toString() {
        return "Rat Report: " + dateCreated + " at " + incidentAddress + ", "
                + borough + ", " + city + " " + (int) incidentZip + ". " + locationType + ". "
                + "Latitude: " + latitude + " Longitude: " + longitude;
    }

    /**
     * Constructor used by Parcel to make a RatReport out of the parceled information
     * @param in the parcel containing the report information
     */
    private RatReport(Parcel in) {
        dateCreated = in.readString();
        locationType = in.readString();
        incidentZip = in.readDouble();
        incidentAddress = in.readString();
        city = in.readString();
        borough = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Method that writes the report's data to a Parcel
     * @param dest the parcel to write to
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateCreated);
        dest.writeString(locationType);
        dest.writeDouble(incidentZip);
        dest.writeString(incidentAddress);
        dest.writeString(city);
        dest.writeString(borough);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    /**
     * Creator class defined for Parcelable implementation that generates instances of RatReport from passed in Parcels
     */
    public static final Parcelable.Creator<RatReport> CREATOR
            = new Parcelable.Creator<RatReport>() {
        public RatReport createFromParcel(Parcel in) {
            return new RatReport(in);
        }

        public RatReport[] newArray(int size) {
            return new RatReport[size];
        }
    };
}
