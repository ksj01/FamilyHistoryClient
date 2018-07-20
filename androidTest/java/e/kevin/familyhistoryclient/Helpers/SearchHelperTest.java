package e.kevin.familyhistoryclient.Helpers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import e.kevin.familyhistoryclient.Activities.SearchActivity;
import e.kevin.familyhistoryclient.Models.EventModel;
import e.kevin.familyhistoryclient.Models.PersonModel;
import e.kevin.familyhistoryclient.Models.SharedData;

import static org.junit.Assert.*;

/**
 * Created by kevin on 4/18/18.
 */
public class SearchHelperTest {
    @Test
    public void addEvents() throws Exception {
        //Test searching for people
        SharedData.model.people = new HashMap<>();
        SharedData.model.events = new HashMap<>();
        PersonModel person1 = new PersonModel();
        person1.setFirstName("first");
        person1.setId("first");
        person1.setGender('m');


        EventModel event1 = new EventModel();
        event1.setId("event1");
        event1.setPersonId("first");
        event1.setLatitude(1.1);
        event1.setLongitude(1.1);
        event1.setCountry("country");
        event1.setCity("city");
        event1.setType("birth");
        event1.setYear(6);

        EventModel event2 = new EventModel();
        event2.setId("event2");
        event2.setPersonId("first");
        event2.setLatitude(2.2);
        event2.setLongitude(2.2);
        event2.setCountry("country");
        event2.setCity("city");
        event2.setType("death");
        event2.setYear(1);

        EventModel event3 = new EventModel();
        event3.setId("event3");
        event3.setPersonId("first");
        event3.setLatitude(3.3);
        event3.setLongitude(3.3);
        event3.setCountry("country");
        event3.setCity("city");
        event3.setType("marriage");
        event3.setYear(18);

        EventModel event4 = new EventModel();
        event4.setId("event4");
        event4.setPersonId("first");
        event4.setLatitude(4.4);
        event4.setLongitude(4.4);
        event4.setCountry("country");
        event4.setCity("city");
        event4.setType("baptism");
        event4.setYear(4);

        SharedData.model.addEvent(event1.getId(), event1);
        SharedData.model.addEvent(event2.getId(), event2);
        SharedData.model.addEvent(event3.getId(), event3);
        SharedData.model.addEvent(event4.getId(), event4);
        SharedData.model.addPerson(person1.getId(), person1);

        ArrayList<Object> searchResults  = new ArrayList<>();
        String query = "baptism";
        searchResults = SearchHelper.addEvents(SharedData.model.getEvents().keySet(), searchResults, query);
        Object zero = searchResults.get(0);
        EventModel first = (EventModel) zero;

        int num = searchResults.size();

        assertTrue(num == 1);
        assertTrue(first.getType() == "baptism");
        assertFalse(first.getType() == "marriage");
    }

    @Test
    public void addPeople() throws Exception {

        //Test searching for events
        SharedData.model.people = new HashMap<>();
        SharedData.model.events = new HashMap<>();
        PersonModel person1 = new PersonModel();
        person1.setFirstName("first");
        person1.setId("first");
        person1.setGender('m');

        PersonModel person2 = new PersonModel();
        person2.setFirstName("second");
        person2.setId("second");
        person2.setGender('m');

        PersonModel person3 = new PersonModel();
        person3.setFirstName("third");
        person3.setId("third");
        person3.setGender('m');


        EventModel event1 = new EventModel();
        event1.setId("event1");
        event1.setPersonId("first");
        event1.setLatitude(1.1);
        event1.setLongitude(1.1);
        event1.setCountry("country");
        event1.setCity("city");
        event1.setType("birth");
        event1.setYear(6);

        SharedData.model.addEvent(event1.getId(), event1);
        SharedData.model.addPerson(person1.getId(), person1);
        SharedData.model.addPerson(person2.getId(), person2);
        SharedData.model.addPerson(person3.getId(), person3);

        ArrayList<Object> searchResults  = new ArrayList<>();
        String query = "first";
        HashMap<String, PersonModel> people = SharedData.model.getPeople();
        searchResults = SearchHelper.addPeople(people, people.keySet(), searchResults, query);
        Object zero = searchResults.get(0);
        PersonModel first = (PersonModel) zero;

        int num = searchResults.size();

        assertTrue(num == 1);
        assertTrue(first.getId() == "first");
        assertFalse(first.getId() == "second");
    }

}