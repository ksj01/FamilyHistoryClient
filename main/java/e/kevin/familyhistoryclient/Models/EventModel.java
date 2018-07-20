package e.kevin.familyhistoryclient.Models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * EventModel class for event objects
 */
public class EventModel implements Serializable {
    private String id;

    private String personId;
    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String type;
    private int year = 0;

    /**
     * Getter for the ID of this event
     * @return String containing the ID of this event
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for this event's ID
     * @param id String containing the ID of this event
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for the ID of the person to whom this event belongs
     * @return String containing the person's ID
     */
    public String getPersonId() {
        return personId;
    }

    /**
     * Setter for the ID to whom this event belongs
     * @param personId Id of the user that this event will belong to
     */
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    /**
     * Getter for this event's latitude
     * @return latitude of this event
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Setter for this event's latitude
     * @param latitude Latitude of this event's location
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter for this event's longitude
     * @return longitude of this event's location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter for this event's longitude
     * @param longitude longitude of this event's location
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for this event's country
     * @return Country that this event occurred in
     */
    public String getCountry() {
        return country;
    }

    /**
     * Setter for this event's country
     * @param country Country in which this event occurred
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Getter for this event' city
     * @return City in which this event occurred
     */
    public String getCity() {
        return city;
    }

    /**
     * Setter for this event's City
     * @param city City in which this event occurred
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Getter for this event's type
     * @return The type/description of this event
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for this event's type
     * @param type Type or description of this event
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for this event's year
     * @return Year in which this event occurred
     */
    public int getYear() {
        return year;
    }

    /**
     * Setter for this event's year
     * @param year Year in which this event occurred
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Gathers all of this event's relevant information to be displayed in the search results
     * @return Returns a string of this user's information, formatted to be displayed in certain parts of the app
     */
    public String getInfo() {
        return getType() + ": " + getCity() + ", " + getCountry() + " (" + getYear() + ")";
    }

    /**
     * Getter for a LatLng object containing the event's latitude and longitude
     * @return LatLng object for this event's location
     */
    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
