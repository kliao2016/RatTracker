package edu.gatech.cs2340.rattracker.model;

/**
 * Created by kevinliao on 10/7/17.
 */

public class RatReport {
    private String key;
    private String createDate;
    private String locationType;
    private String incidentZip;
    private String incidentAddress;
    private String city;
    private String borough;
    private String latitude;
    private String longitude;

    /**
     * Constructor to create Rat report with specific attributes
     *
     * @param key the unique key of the rat report
     * @param createDate the date the rat report was created
     * @param locationType the type of location where the rat was sighted
     * @param incidentZip the zipcode of the location where the rat was sighted
     * @param incidentAddress the address of the location where the rat was sighted
     * @param city the city where the rat was sighted
     * @param borough the borough where the rat was sighted
     * @param latitude the latitude of the coordinates where the rat was sighted
     * @param longitude the longitude of the coordinates where the rat was sighted
     */
    public RatReport(String key, String createDate, String locationType, String incidentZip,
                     String incidentAddress, String city, String borough, String latitude,
                     String longitude) {
        this.key = key;
        this.createDate = createDate;
        this.locationType = locationType;
        this.incidentZip = incidentZip;
        this.incidentAddress = incidentAddress;
        this.city = city;
        this.borough = borough;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getKey() {
        return this.key;
    }

    public String getCreateDate() {
        return this.createDate;
    }

    public String getLocationType() {
        return this.locationType;
    }

    public String getIncidentZip() {
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

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }


    // Setters
    public void setKey(String key) {
        this.key = key;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public void setIncidentZip(String incidentZip) {
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

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
