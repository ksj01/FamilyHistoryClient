package e.kevin.familyhistoryclient.Models;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.*;

/**
 * PersonModel class for creating person objects
 */
public class PersonModel implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private char gender;
    private String spouseId = null;
    private String fatherId = null;
    private String motherId = null;
    private String side = "spouse";
    private HashMap<String, EventModel> personEvents = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, EventModel> yearKeyed = new HashMap<>();

    /**
     * Assigns a side of the family for each person
     *
     * @param side Which side of the family the person belongs to
     */
    public void setSide(String side) {
        this.side = side;
    }

    /**
     * Getter for this person's side of the family
     *
     * @return returns a string identifying which side of the family the person belongs to
     */
    String getSide() {
        return side;
    }

    /**
     * Getter for this person's id
     *
     * @return String with this person's id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for this person's id
     *
     * @param id String id to assign to this person
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for person's first name
     *
     * @return String obtaining the person's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for this person's first name
     *
     * @param firstName String to be assigned as this person's first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for this person's last name
     *
     * @return String containing person's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter for this person's last name
     *
     * @param lastName Last name to assign to this person
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for this person's full name
     *
     * @return String containing both the first and last name of this person
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Getter for this person's gender
     *
     * @return char identifying the person as 'm' or 'f'
     */
    public char getGender() {
        return gender;
    }

    /**
     * Setter for this person's gender
     *
     * @param gender 'm' or 'f' char representing this person's gender
     */
    public void setGender(char gender) {
        this.gender = gender;
    }

    /**
     * Setter for the id of this person's spouse
     *
     * @return String containing this person's spouse's ID
     */
    public String getSpouseId() {
        return spouseId;
    }

    /**
     * Setter for this person's spouse ID
     *
     * @param spouseId String containing the ID for this person's spouse ID
     */
    public void setSpouseId(String spouseId) {
        this.spouseId = spouseId;
    }

    /**
     * Getter for this person's father's ID
     *
     * @return String containing this person's father's ID
     */
    public String getFatherId() {
        return fatherId;
    }

    /**
     * Setter for this person's father's ID
     *
     * @param fatherId String of the ID of this person's father
     */
    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    /**
     * Getter for this person's mother's ID
     *
     * @return String containing the ID of this person's mother
     */
    public String getMotherId() {
        return motherId;
    }

    /**
     * Setter for this person's mother's ID
     *
     * @param motherId String with the ID of this person's mother's ID
     */
    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    /**
     * Attaches an event to this particular person for later sorting or retrieval
     *
     * @param event event to be assigned to this person
     */
    public void attachEvent(EventModel event) {
        personEvents.put(event.getId(), event);
    }

    /**
     * Getter for this person's events
     *
     * @return Hashmap containing all events assigned to this person
     */
    public HashMap<String, EventModel> getEventsList() {
        return personEvents;
    }

    /**
     * Getter for this person's events, keyed by the year that they occurred
     *
     * @return Hashmap containing this user's events, keyed by the year they occurred
     */
    public HashMap<Integer, EventModel> getYearKeyed() {
        return yearKeyed;
    }

    /**
     * Gets all the years in which this person had events occurred, and returns them as a sorted array
     *
     * @return Integer array containing all the years in which events occurred, sorted by year
     */
    public Integer[] getYears() {
        Vector<Integer> yearsArray = new Vector<>();
        Set<String> eventKeys = personEvents.keySet();
        String[] keysArray = eventKeys.toArray(new String[eventKeys.size()]);
        for (int i = 0; i < personEvents.size(); i++) {
            int year = personEvents.get(keysArray[i]).getYear();
            if (yearsArray.contains(year)) {
                year++;
            }
            yearsArray.add(year);
            yearKeyed.put(year, personEvents.get(keysArray[i]));
        }
        yearsArray.toArray();
        Integer[] sorted = yearsArray.toArray(new Integer[yearsArray.size()]);
        Arrays.sort(sorted);
        return sorted;
    }
}
