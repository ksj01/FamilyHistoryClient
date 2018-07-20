package e.kevin.familyhistoryclient.Models;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Set;

/**
 * Main data store for all user data that will remain stored while the current user is still logged in
 */
public class SharedData {
    public static SharedData model = new SharedData();

    private Boolean loggedIn = false;
    private String url;
    private String authToken;
    private String username;
    public HashMap<String, EventModel> events;
    public HashMap<String, PersonModel> people;
    private HashMap<Marker, EventModel> markers;
    private HashMap<String, SettingModel> settings;
    private HashMap<String, Boolean> toggles;
    private HashMap<String, Boolean> filters;
    private boolean peopleNeedsRefilter = true;
    private boolean eventsNeedsRefilter = true;
    private HashMap<String, EventModel> filteredEvents;
    private HashMap<String, PersonModel> filteredPeople;
    private HashMap<String, Integer> eventColors;

    /**
     * Constructor sets up our hashmaps so we can easily access data later by keys
     */
    public SharedData() {
        events = new HashMap<>();
        people = new HashMap<>();
        markers = new HashMap<>();
        settings = new HashMap<>();
        filters = new HashMap<>();
        toggles = new HashMap<>();
        filteredEvents = new HashMap<>();
        filteredPeople = new HashMap<>();
        eventColors = new HashMap<>();

        /**
         * Initially assign data to the map lines and map style
         */
        String[] settingKeys = {"storylines", "treelines", "spouselines", "map_type"};
        settings.put(settingKeys[0], new SettingModel("Red"));
        settings.put(settingKeys[1], new SettingModel("Green"));
        settings.put(settingKeys[2], new SettingModel("Blue"));
        settings.put(settingKeys[3], new SettingModel("Normal"));

        /**
         * Set up necessary info for our filters.
         */
        String[] filterKeys = {"baptism", "birth", "death", "marriage", "fatherside", "motherside", "male", "female"};
        for (String key : filterKeys) {
            filters.put(key, true);
        }

        /**
         * Set up switches for our map line settings
         */
        String[] toggleKeys = {"storylines", "treelines", "spouselines"};
        for (String key : toggleKeys) {
            toggles.put(key, true);
        }

    }

    /**
     * Boolean that tells us if we need to filter our people based on assigned filters
     */
    public void setPeopleNeedsRefilter() {
        peopleNeedsRefilter = true;
    }

    /**
     * Boolean that tells us if we need to filter our events based on assigned filters
     */
    public void setEventsNeedsRefilter() {
        eventsNeedsRefilter = true;
    }

    /**
     * Sets the auth token for this user session so we can resync later
     *
     * @param auth AuthToken to send to the server if needed
     */
    public void setAuthToken(String auth) {
        authToken = auth;
    }

    /**
     * We store the URL of the server so we can resync without additional user input telling us the URL again
     *
     * @param passedUrl URL of the server
     */
    public void setUrl(String passedUrl) {
        url = passedUrl;
    }

    /**
     * Current user's ID
     *
     * @param personId ID retrieved from the server on login or registration
     */
    public void setUser(String personId) {
        username = personId;
    }

    /**
     * Getter for the current User's username
     *
     * @return returns the username for the current user
     */
    public String getUser() {
        return username;
    }

    /**
     * Getter for the authToken
     *
     * @return authToken for resync purposes
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Getter for the server URL
     *
     * @return returns the server ip address and port as a string
     */
    public String getUrl() {
        return url;
    }

    /**
     * Adds an event that belongs to the current user to the data store
     *
     * @param id    ID of the event being stored, used as the key for the hashmap
     * @param event EventModel object to be stored
     */
    public void addEvent(String id, EventModel event) {
        events.put(id, event);
    }

    /**
     * Adds a person that belongs to the current user to the data store
     *
     * @param id     ID of the person being stored, used as the key for the hashmap
     * @param person PersonModel to be stored
     */
    public void addPerson(String id, PersonModel person) {
        people.put(id, person);
    }

    /**
     * Combines a marker with an event so we can easily access them later.
     *
     * @param marker Marker to be assigned to an event
     * @param event  Event to which we want to assign a marker
     */
    public void addMarker(Marker marker, EventModel event) {
        markers.put(marker, event);
    }

    /**
     * Getter for the events Hashmap
     *
     * @return Hashmap of all events
     */
    public HashMap<String, EventModel> getEvents() {
        return events;
    }

    /**
     * If the NeedsFilteringEvents bool is set, we filter out all filters and only return a Hashmap of filtered events.
     * Otherwise, we can just return the current hashmap
     *
     * @return Hashmap of filtered events
     */
    public HashMap<String, EventModel> getFilteredEvents() {
        if (eventsNeedsRefilter) {
            filteredEvents.clear();
            Set<String> eventKeys = events.keySet();
            String[] eventIds = eventKeys.toArray(new String[eventKeys.size()]);

            for (int i = 0; i < events.size(); i++) {
                if (events.get(eventIds[i]).getType().toLowerCase().equals("birth") && !filters.get("birth")) {
                    continue;
                } else if (events.get(eventIds[i]).getType().toLowerCase().equals("death") && !filters.get("death")) {
                    continue;
                } else if (events.get(eventIds[i]).getType().toLowerCase().equals("marriage") && !filters.get("marriage")) {
                    continue;
                } else if (events.get(eventIds[i]).getType().toLowerCase().equals("baptism") && !filters.get("baptism")) {
                    continue;
                } else if (people.get(events.get(eventIds[i]).getPersonId()).getGender() == 'm' && !filters.get("male")) {
                    continue;
                } else if (people.get(events.get(eventIds[i]).getPersonId()).getGender() == 'f' && !filters.get("female")) {
                    continue;
                } else if (people.get(events.get(eventIds[i]).getPersonId()).getSide().equals("father") && !filters.get("fatherside")) {
                    continue;
                } else if (people.get(events.get(eventIds[i]).getPersonId()).getSide().equals("mother") && !filters.get("motherside")) {
                    continue;
                }
                filteredEvents.put(events.get(eventIds[i]).getId(), events.get(eventIds[i]));
            }
            eventsNeedsRefilter = false;
        }
        return filteredEvents;
    }

    /**
     * If the NeedsFilteringPeople bool is set, we filter out all filters and only return a Hashmap of filtered persons.
     * Otherwise, we can just return the current hashmap
     *
     * @return Hashmap of filtered persons
     */
    public HashMap<String, PersonModel> getFilteredPeople() {
        if (peopleNeedsRefilter) {
            filteredPeople.clear();
            Set<String> peopleKeys = people.keySet();
            String[] peopleIds = peopleKeys.toArray(new String[peopleKeys.size()]);

            for (int i = 0; i < people.size(); i++) {
                if (people.get(peopleIds[i]).getGender() == 'm' && !filters.get("male")) {
                    continue;
                } else if (people.get(peopleIds[i]).getGender() == 'f' && !filters.get("female")) {
                    continue;
                } else if (people.get(peopleIds[i]).getSide().equals("father") && !filters.get("fatherside")) {
                    continue;
                } else if (people.get(peopleIds[i]).getSide().equals("mother") && !filters.get("motherside")) {
                    continue;
                }
                filteredPeople.put(people.get(peopleIds[i]).getId(), people.get(peopleIds[i]));
            }
            peopleNeedsRefilter = false;
        }
        return filteredPeople;
    }

    /**
     * Getter for all people
     *
     * @return Hashmap of all people
     */
    public HashMap<String, PersonModel> getPeople() {
        return people;
    }

    /**
     * Getter for a specific person
     *
     * @param personId ID of the person to get
     * @return Returns a PersonModel for the requested person
     */
    public PersonModel getPerson(String personId) {
        return people.get(personId);
    }

    /**
     * Getter for markers and events
     *
     * @return Returns the hashmap of markers and events
     */
    public HashMap<Marker, EventModel> getMarkers() {
        return markers;
    }

    /**
     * Getter for the settings
     *
     * @return Returns hashmap of Setting names and Setting objects
     */
    public HashMap<String, SettingModel> getSettings() {
        return settings;
    }

    /**
     * Setter for settings
     *
     * @param settingName Setting to change
     * @param filter      Value to assign to setting
     */
    public void setSetting(String settingName, String filter) {
        settings.get(settingName).setValue(filter);
    }

    /**
     * Getter for all filters
     *
     * @return Hashmap of filter names and whether or not they are set
     */
    public HashMap<String, Boolean> getFilters() {
        return filters;
    }

    /**
     * Getter for settings toggles
     *
     * @return Hashmap of settings toggles and whether or not they are set
     */
    public HashMap<String, Boolean> getToggles() {
        return toggles;
    }

    /**
     * Getter for loggedIn boolean
     *
     * @return Boolean indicating whether or not the user is logged in
     */
    public Boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Setter for logged in status
     *
     * @param loggedIn boolean indicating if the user is currently logged in
     */
    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * Getter for event colors
     *
     * @return Hashmap of color name and int ID
     */
    public HashMap<String, Integer> getColors() {
        return eventColors;
    }

}
