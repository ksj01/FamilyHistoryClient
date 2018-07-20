package e.kevin.familyhistoryclient.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import java.util.*;

import e.kevin.familyhistoryclient.R;
import e.kevin.familyhistoryclient.Models.SharedData;

/**
 * Displays filter switches for a selection of events and conditions.
 */
public class FilterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        /*
          Load in switches
         */
        Switch birth = findViewById(R.id.birthSwitch);
        Switch baptism = findViewById(R.id.baptismSwitch);
        Switch death = findViewById(R.id.deathSwitch);
        Switch marriage = findViewById(R.id.marriageSwitch);
        Switch paternal = findViewById(R.id.paternalSwitch);
        Switch maternal = findViewById(R.id.maternalSwitch);
        Switch male = findViewById(R.id.maleSwitch);
        Switch female = findViewById(R.id.femaleSwitch);

        /*
          Get current filter settings
         */
        HashMap<String, Boolean> filters = SharedData.model.getFilters();
        birth.setChecked(filters.get("birth"));
        baptism.setChecked(filters.get("baptism"));
        death.setChecked(filters.get("death"));
        marriage.setChecked(filters.get("marriage"));
        paternal.setChecked(filters.get("fatherside"));
        maternal.setChecked(filters.get("motherside"));
        male.setChecked(filters.get("male"));
        female.setChecked(filters.get("female"));

        /*
          On Listeners
         */
        birth.setOnCheckedChangeListener(this);
        baptism.setOnCheckedChangeListener(this);
        death.setOnCheckedChangeListener(this);
        marriage.setOnCheckedChangeListener(this);
        paternal.setOnCheckedChangeListener(this);
        maternal.setOnCheckedChangeListener(this);
        male.setOnCheckedChangeListener(this);
        female.setOnCheckedChangeListener(this);
    }

    /**
     *
     * @param buttonView required to get SwitchId
     * @param isChecked boolean value of clicked switch
     * Sets the filter settings for whatever filter is clicked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        @SuppressLint("UseSparseArrays") Map<Integer, String> switches = new HashMap<>();
        switches.put(R.id.birthSwitch, "birth");
        switches.put(R.id.baptismSwitch, "baptism");
        switches.put(R.id.deathSwitch, "death");
        switches.put(R.id.marriageSwitch, "marriage");
        switches.put(R.id.paternalSwitch, "fatherside");
        switches.put(R.id.maternalSwitch, "motherside");
        switches.put(R.id.maleSwitch, "male");
        switches.put(R.id.femaleSwitch, "female");

        String switchString = switches.get(buttonView.getId());

        /*
          Sets the check for whether or not we need to re-filter all people/events
         */
        SharedData.model.setPeopleNeedsRefilter();
        SharedData.model.setEventsNeedsRefilter();

        /*
          Set filter
         */
        SharedData.model.getFilters().put(switchString, isChecked);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
    }
}
