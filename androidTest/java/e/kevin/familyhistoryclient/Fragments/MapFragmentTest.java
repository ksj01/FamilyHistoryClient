package e.kevin.familyhistoryclient.Fragments;

import org.junit.Test;

import java.util.Objects;

import e.kevin.familyhistoryclient.Models.EventModel;
import e.kevin.familyhistoryclient.Models.PersonModel;
import e.kevin.familyhistoryclient.Models.SharedData;

import static org.junit.Assert.*;

/**
 *
 */
public class MapFragmentTest {
    @Test
    public void connectSpouse() throws Exception {
        PersonModel person = new PersonModel();
        PersonModel spouse = new PersonModel();
        person.setId("testUser");
        person.setSpouseId("spouse");
        spouse.setId("spouse");

        SharedData.model.addPerson(person.getId(), person);
        SharedData.model.addPerson(spouse.getId(), spouse);
        EventModel event1 = new EventModel();
        event1.setId("event1");
        event1.setPersonId("testUser");
        event1.setLatitude(1.1);
        event1.setLongitude(1.1);
        event1.setCountry("country");
        event1.setCity("city");
        event1.setType("type1");
        event1.setYear(6);

        PersonModel eventPerson = SharedData.model.getPerson(event1.getPersonId());

        assertTrue(eventPerson == person);
        assertTrue(Objects.equals(eventPerson.getSpouseId(), person.getSpouseId()));
        assertTrue(Objects.equals(eventPerson.getSpouseId(), spouse.getId()));
        assertFalse(Objects.equals(eventPerson.getId(), spouse.getId()));
    }

    @Test
    public void drawFamilyTree() throws Exception {
        PersonModel person = new PersonModel();
        PersonModel mother = new PersonModel();
        PersonModel father = new PersonModel();
        person.setId("testUser");
        person.setFatherId("father");
        person.setMotherId("mother");

        mother.setId("mother");
        father.setId("father");

        mother.setMotherId("maternal grandmother");
        father.setMotherId("paternal grandmother");
        mother.setFatherId("maternal grandfather");
        father.setFatherId("paternal grandfather");

        SharedData.model.addPerson(person.getId(), person);
        SharedData.model.addPerson(father.getId(), father);
        SharedData.model.addPerson(mother.getId(), mother);

        assertTrue(Objects.equals(person.getFatherId(), "father"));
        assertTrue(Objects.equals(person.getMotherId(), "mother"));
        assertTrue(Objects.equals(person.getFatherId(), father.getId()));
        assertTrue(Objects.equals(person.getMotherId(), mother.getId()));

        PersonModel personsFather = SharedData.model.getPerson(person.getFatherId());
        PersonModel personsMother = SharedData.model.getPerson(person.getMotherId());
        assertTrue(Objects.equals(father, personsFather));
        assertTrue(Objects.equals(mother, personsMother));

        assertTrue(Objects.equals(father.getFatherId(), personsFather.getFatherId()));
        assertTrue(Objects.equals(father.getMotherId(), personsFather.getMotherId()));

        assertTrue(Objects.equals(mother.getFatherId(), personsMother.getFatherId()));
        assertTrue(Objects.equals(mother.getMotherId(), personsMother.getMotherId()));

        assertFalse(person == mother);
        assertFalse(personsFather == mother);
        assertFalse(personsMother.getFatherId() == father.getFatherId());
    }

    @Test
    public void drawLifeStory() throws Exception {
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

        EventModel firstEvent =  person.getYearKeyed().get(years[0]);
        EventModel secondEvent =  person.getYearKeyed().get(years[1]);
        EventModel thirdEvent =  person.getYearKeyed().get(years[2]);
        EventModel fourthEvent =  person.getYearKeyed().get(years[3]);

        assertTrue(firstEvent.getYear() < secondEvent.getYear());
        assertTrue(firstEvent.getYear() < thirdEvent.getYear());
        assertTrue(firstEvent.getYear() < fourthEvent.getYear());
        assertFalse(firstEvent.getYear() > fourthEvent.getYear());

        assertTrue(secondEvent.getYear() > firstEvent.getYear());
        assertTrue(secondEvent.getYear() < thirdEvent.getYear());
        assertTrue(secondEvent.getYear() < fourthEvent.getYear());
        assertFalse(secondEvent.getYear() < firstEvent.getYear());

        assertTrue(thirdEvent.getYear() > firstEvent.getYear());
        assertTrue(thirdEvent.getYear() > secondEvent.getYear());
        assertTrue(thirdEvent.getYear() < fourthEvent.getYear());
        assertFalse(thirdEvent.getYear() < secondEvent.getYear());

        assertTrue(fourthEvent.getYear() > firstEvent.getYear());
        assertTrue(fourthEvent.getYear() > secondEvent.getYear());
        assertTrue(fourthEvent.getYear() > thirdEvent.getYear());
        assertFalse(fourthEvent.getYear() < secondEvent.getYear());
    }
}