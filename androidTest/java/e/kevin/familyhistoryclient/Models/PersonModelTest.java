package e.kevin.familyhistoryclient.Models;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

/**
 *
 */
public class PersonModelTest {
    @Test
    public void getYearKeyed() throws Exception {
        PersonModel person = new PersonModel();
        EventModel event1 = new EventModel();
        event1.setId("event1");
        event1.setPersonId("testUser");
        event1.setLatitude(1.1);
        event1.setLongitude(1.1);
        event1.setCountry("country");
        event1.setCity("city");
        event1.setType("type1");
        event1.setYear(6);

        EventModel event2 = new EventModel();
        event2.setId("event2");
        event2.setPersonId("testUser");
        event2.setLatitude(2.2);
        event2.setLongitude(2.2);
        event2.setCountry("country");
        event2.setCity("city");
        event2.setType("type2");
        event2.setYear(1);

        EventModel event3 = new EventModel();
        event3.setId("event3");
        event3.setPersonId("testUser");
        event3.setLatitude(3.3);
        event3.setLongitude(3.3);
        event3.setCountry("country");
        event3.setCity("city");
        event3.setType("type3");
        event3.setYear(18);

        EventModel event4 = new EventModel();
        event4.setId("event4");
        event4.setPersonId("testUser");
        event4.setLatitude(4.4);
        event4.setLongitude(4.4);
        event4.setCountry("country");
        event4.setCity("city");
        event4.setType("type4");
        event4.setYear(4);

        person.attachEvent(event1);
        person.attachEvent(event2);
        person.attachEvent(event3);
        person.attachEvent(event4);

        Integer[] years = person.getYears();

        assertTrue(Objects.equals(person.getYearKeyed().get(years[0]).getId(), event2.getId()));
        assertTrue(Objects.equals(person.getYearKeyed().get(years[1]).getId(), event4.getId()));
        assertTrue(Objects.equals(person.getYearKeyed().get(years[2]).getId(), event1.getId()));
        assertTrue(Objects.equals(person.getYearKeyed().get(years[3]).getId(), event3.getId()));
        assertFalse(Objects.equals(person.getYearKeyed().get(years[0]).getId(), event3.getId()));
        assertFalse(Objects.equals(person.getYearKeyed().get(years[1]).getId(), event1.getId()));
        assertFalse(Objects.equals(person.getYearKeyed().get(years[2]).getId(), event4.getId()));
        assertFalse(Objects.equals(person.getYearKeyed().get(years[3]).getId(), event2.getId()));
    }

}