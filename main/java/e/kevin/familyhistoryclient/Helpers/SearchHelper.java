package e.kevin.familyhistoryclient.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import e.kevin.familyhistoryclient.Models.EventModel;
import e.kevin.familyhistoryclient.Models.PersonModel;
import e.kevin.familyhistoryclient.Models.SharedData;

/**
 * Created by kevin on 4/18/18.
 */

public class SearchHelper {
    /**
     * Gets a list of people who haven't been filtered out so we can get any non-filtered people and events
     */
    public static void addItems(ArrayList<Object> searchResults, String query) {
        HashMap<String, PersonModel> people = SharedData.model.getFilteredPeople();
        Set<String> peopleKeys = people.keySet();
        addPeople(people, peopleKeys, searchResults, query);
        addEvents(peopleKeys, searchResults, query);
    }

    /**
     * Test search terms against event type, city, country, and year.
     *
     * @param peopleKeys List of IDs for all people who haven't been filtered out yet.
     */
    public static ArrayList<Object> addEvents(Set<String> peopleKeys, ArrayList<Object> searchResults, String query) {
        HashMap<String, EventModel> events = SharedData.model.getFilteredEvents();
        Set<String> eventKeys = events.keySet();
        if (events.size() > 0) {
            String[] eventIds = eventKeys.toArray(new String[eventKeys.size()]);
            for (int i = 0; i < peopleKeys.size(); i++) {

                String type = events.get(eventIds[i]).getType().toLowerCase();
                String city = events.get(eventIds[i]).getCity();
                String country = events.get(eventIds[i]).getCountry().toLowerCase();
                String year = Integer.toString(events.get(eventIds[i]).getYear());

                    /*
                    If the search term matches any of the a selected parameters, add the event to the searchResults arrayList that belongs to the recyclerView
                     */
                if (type.contains(query) || city.toLowerCase().contains(query) || country.contains(query) || year.contains(query)) {
                    searchResults.add(events.get(eventIds[i]));
                }
            }
        }
        return searchResults;
    }

    /**
     * Test search terms against person names
     *
     * @param people     Map of all non-filtered out people
     * @param peopleKeys List of IDs for non-filtered people.
     */
    public static ArrayList<Object> addPeople(HashMap<String, PersonModel> people, Set<String> peopleKeys, ArrayList<Object> searchResults, String query) {
        String[] peopleIds = peopleKeys.toArray(new String[peopleKeys.size()]);
        for (int i = 0; i < peopleKeys.size(); i++) {
            String name = people.get(peopleIds[i]).getFullName().toLowerCase();

                /*
                If the search term matches any person names, add the person to the searchResults arrayList that belongs to the recyclerView
                */
            if (name.contains(query)) {
                searchResults.add(people.get(peopleIds[i]));
            }
        }
        return searchResults;
    }
}
