package e.kevin.familyhistoryclient.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.*;

import e.kevin.familyhistoryclient.Helpers.ExpandableListAdapter;
import e.kevin.familyhistoryclient.Fragments.MapFragment;
import e.kevin.familyhistoryclient.R;
import e.kevin.familyhistoryclient.Models.*;

/**
 * Uses an ExpandableListView to display expandable lists to show the selected person's family and life events
 */
public class PersonActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expander;
    List<String> header;
    HashMap<String, List<String>> child;
    PersonModel currentPerson = MapFragment.currentPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Bundle extras = getIntent().getExtras();
        /*
        We need the person for which person activity we're creating.
         */
        if (extras != null) {
            currentPerson = (PersonModel) extras.getSerializable("person");
        }
        Iconify.with(new FontAwesomeModule());

        /*
        Begin making the overall page layout
         */
        buildPerson();

        /*
        Load in the expander to handle the meat of the interactivity
         */
        expander = findViewById(R.id.personExpandableList);
        listAdapter = new ExpandableListAdapter(this, header, child);
        expander.setAdapter(listAdapter);
        expander.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                /*
                groupPosition 0 will always be events. The only other option is family
                 */
                if (groupPosition == 0) {
                    getClickedEvent(childPosition);
                } else {
                    /*
                    The first two child positions are the headers for life events and family
                     */
                    int adjusted = childPosition + 2;
                    String personString = parent.getItemAtPosition(adjusted).toString();
                    getClickedPerson(personString.toLowerCase());
                }
                return false;
            }
        });
    }

    /**
     * Gets the selected user's information and sets it to display.
     * Also kicks off the getData() function which handles the majority of the logic
     */
    private void buildPerson() {
        TextView firstName = findViewById(R.id.firstNameTitle);
        TextView lastName = findViewById(R.id.lastNameTitle);
        TextView gender = findViewById(R.id.genderTitle);
        firstName.setText(currentPerson.getFirstName());
        lastName.setText(currentPerson.getLastName());
        String genderText = currentPerson.getGender() == 'm' ? "Male" : "Female";
        gender.setText(genderText);
        getData();
    }

    /**
     * Creates two lists (one for family members and one for life events) and adds the necessary information for the Expander to display the necessary links.
     */
    private void getData() {
        header = new ArrayList<>();
        child = new HashMap<>();

        header.add("Life Events");
        header.add("Family");

        /*
        Get person's life events in order by year and add them to the life event list
         */
        List<String> lifeEvents = new ArrayList<>();
        for (int i = 0; i < currentPerson.getYears().length; i++) {
            lifeEvents.add("{fa-map-marker} " + currentPerson.getYearKeyed().get(currentPerson.getYears()[i]).getInfo());
        }

        /*
        Get person's spouse to display if available and add them to the family list
         */
        List<String> family = new ArrayList<>();
        String spouseId = currentPerson.getSpouseId();
        PersonModel spouse = SharedData.model.getPerson(spouseId);
        if (spouse != null) {
            String icon = spouse.getGender() == 'm' ? "{fa-male} Spouse:" : "{fa-female} Spouse: ";
            family.add(icon + spouse.getFullName());
        }

        /*
        Get person's father to display if available and add them to the family list
         */
        String fatherId = currentPerson.getFatherId();
        PersonModel father = SharedData.model.getPerson(fatherId);
        if (father != null) {
            family.add("{fa-male} Father: " + father.getFullName());
        }

        /*
        Get person's mother to display if available and add them to the family list
         */
        String motherId = currentPerson.getMotherId();
        PersonModel mother = SharedData.model.getPerson(motherId);
        if (mother != null) {
            family.add("{fa-female} Mother: " + mother.getFullName());
        }

        /*
        Only display header if there's info to display
         */
        if (lifeEvents.size() != 0) {
            child.put(header.get(0), lifeEvents);
        }
        if (family.size() != 0) {
            child.put(header.get(1), family);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    /**
     *
     * If an event is clicked, we go to the map in the Event View Activity
     * @param childPosition the position of the selected event
     */
    private void getClickedEvent(int childPosition) {
        Intent intent = new Intent(PersonActivity.this, EventViewActivity.class);
        HashMap<String, EventModel> events = currentPerson.getEventsList();
        Set<String> eventKeys = events.keySet();
        String[] eventKeyArray = eventKeys.toArray(new String[eventKeys.size()]);
        intent.putExtra("event", currentPerson.getEventsList().get(eventKeyArray[childPosition]));
        intent.putExtra("person", currentPerson);
        startActivity(intent);
    }

    /**
     * If a person is clicked, we open a new person activity.
     * Note that this function will only work correctly if the person only has at most one each of spouse, father, and mother
     * @param personString a string identifying which family member was selected.
     */
    private void getClickedPerson(String personString) {
        Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
        int start = personString.indexOf('}');
        int end = personString.indexOf(':');
        String testText = personString.substring(start + 2, end);
        if (testText.contains("mother")) {
            intent.putExtra("person", SharedData.model.getPeople().get(currentPerson.getMotherId()));
        } else if (testText.contains("father")) {
            intent.putExtra("person", SharedData.model.getPeople().get(currentPerson.getFatherId()));
        } else if (testText.contains("spouse")) {
            intent.putExtra("person", SharedData.model.getPeople().get(currentPerson.getSpouseId()));
        }
        startActivity(intent);
    }
}
