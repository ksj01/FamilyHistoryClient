package e.kevin.familyhistoryclient.Models;

import org.junit.Test;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;

/**
 *
 */
public class SharedDataTest {
    @Test
    public void getFilteredEvents() throws Exception {
        //Test for event filters

        SharedData data = new SharedData();
        PersonModel person = new PersonModel();
        person.setId("testUser");
        person.setGender('m');
        person.setSpouseId("user");


        EventModel event1 = new EventModel();
        event1.setId("event1");
        event1.setPersonId("testUser");
        event1.setLatitude(1.1);
        event1.setLongitude(1.1);
        event1.setCountry("country");
        event1.setCity("city");
        event1.setType("birth");
        event1.setYear(6);

        EventModel event2 = new EventModel();
        event2.setId("event2");
        event2.setPersonId("testUser");
        event2.setLatitude(2.2);
        event2.setLongitude(2.2);
        event2.setCountry("country");
        event2.setCity("city");
        event2.setType("death");
        event2.setYear(1);

        EventModel event3 = new EventModel();
        event3.setId("event3");
        event3.setPersonId("testUser");
        event3.setLatitude(3.3);
        event3.setLongitude(3.3);
        event3.setCountry("country");
        event3.setCity("city");
        event3.setType("marriage");
        event3.setYear(18);

        EventModel event4 = new EventModel();
        event4.setId("event4");
        event4.setPersonId("testUser");
        event4.setLatitude(4.4);
        event4.setLongitude(4.4);
        event4.setCountry("country");
        event4.setCity("city");
        event4.setType("baptism");
        event4.setYear(4);

        data.getFilters().put("death", false);

        data.addEvent(event1.getId(), event1);
        data.addEvent(event2.getId(), event2);
        data.addEvent(event3.getId(), event3);
        data.addEvent(event4.getId(), event4);
        data.addPerson(person.getId(), person);

        data.setEventsNeedsRefilter();
        HashMap<String, EventModel> filtered = data.getFilteredEvents();

        Set<String> keys = filtered.keySet();
        String[] keyArray = keys.toArray(new String[keys.size()]);

        assertTrue(filtered.size() == 3);
        assertFalse(filtered.size() == 4);
        assertFalse(Objects.equals(filtered.get(keyArray[0]).getType(), "death"));
        assertFalse(Objects.equals(filtered.get(keyArray[1]).getType(), "death"));
        assertFalse(Objects.equals(filtered.get(keyArray[2]).getType(), "death"));
        assertTrue(Objects.equals(filtered.get(keyArray[2]).getType().toLowerCase(), "birth") || Objects.equals(filtered.get(keyArray[2]).getType().toLowerCase(), "marriage") || Objects.equals(filtered.get(keyArray[2]).getType().toLowerCase(), "baptism"));
    }

    @Test
    public void getFilteredPeople() throws Exception {
        //Test for people filters
        SharedData data = new SharedData();
        PersonModel person = new PersonModel();
        person.setId("testUser");
        person.setGender('m');
        person.setSide("fatherside");

        PersonModel person1 = new PersonModel();
        person.setId("testUser");
        person.setGender('m');
        person.setSide("motherside");

        PersonModel person2 = new PersonModel();
        person.setId("testUser");
        person.setGender('m');
        person.setSide("user");


        EventModel event1 = new EventModel();
        event1.setId("event1");
        event1.setPersonId("testUser");
        event1.setLatitude(1.1);
        event1.setLongitude(1.1);
        event1.setCountry("country");
        event1.setCity("city");
        event1.setType("birth");
        event1.setYear(6);

        data.getFilters().put("fatherside", false);

        data.addEvent(event1.getId(), event1);
        data.addPerson(person.getId(), person);
        data.addPerson(person1.getId(), person1);
        data.addPerson(person2.getId(), person2);

        data.setPeopleNeedsRefilter();

        HashMap<String, PersonModel> filtered = data.getFilteredPeople();

        Set<String> keys = filtered.keySet();
        String[] keyArray = keys.toArray(new String[keys.size()]);

        assertTrue(filtered.size() == 2);
        assertFalse(filtered.size() == 3);
        assertFalse(Objects.equals(filtered.get(keyArray[0]).getSide(), "fatherside"));
        assertFalse(Objects.equals(filtered.get(keyArray[1]).getSide(), "fatherside"));
        assertTrue(Objects.equals(filtered.get(keyArray[1]).getSide(), "motherside") || Objects.equals(filtered.get(keyArray[1]).getSide(), "user"));
    }
}