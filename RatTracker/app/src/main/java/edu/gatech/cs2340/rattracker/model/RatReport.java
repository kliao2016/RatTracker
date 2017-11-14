package edu.gatech.cs2340.rattracker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kevin Liao on 10/7/17.
 *
 * Represents a rat report filed by a user
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

    /**
     * @return the date the report was created
     */
    public String getDateCreated() {
        return this.dateCreated;
    }
    /**
     * @return the the location type of the report
     */
    public String getLocationType() {
        return this.locationType;
    }
    /**
     * @return the zip for the report
     */
    public double getIncidentZip() {
        return this.incidentZip;
    }
    /**
     * @return the address for the report
     */
    public String getIncidentAddress() {
        return this.incidentAddress;
    }

    /**
     * @return the borough for the report
     */
    public String getBorough() {
        return this.borough;
    }
    /**
     * @return the latitude for the report
     */
    public double getLatitude() {
        return this.latitude;
    }
    /**
     * @return the longitude for the report
     */
    public double getLongitude() {
        return this.longitude;
    }



    // Setters

    /**
     * Sets date created
     * @param dateCreated the date created
     */
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * sets the location type
     * @param locationType the location type
     */
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    /**
     * sets the zipcode of the sighting
     * @param incidentZip the zipcode
     */
    public void setIncidentZip(double incidentZip) {
        this.incidentZip = incidentZip;
    }

    /**
     * sets the incident address
     * @param incidentAddress the incident address
     */
    public void setIncidentAddress(String incidentAddress) {
        this.incidentAddress = incidentAddress;
    }

    /**
     * @param borough the borough to be set
     */
    public void setBorough(String borough) {
        this.borough = borough;
    }

    /**
     * @param latitude the latitude to be set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @param longitude the longitude to be set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

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
     * @param flags the flag that defines ow the object is created
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
     * Creator class defined for Parcelable implementation that generates
     * instances of RatReport from passed in Parcels
     */
    public static final Parcelable.Creator<RatReport> CREATOR
            = new Parcelable.Creator<RatReport>() {
        @Override
        public RatReport createFromParcel(Parcel in) {
            return new RatReport(in);
        }
        @Override
        public RatReport[] newArray(int size) {
            return new RatReport[size];
        }
    };
}
