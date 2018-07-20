package e.kevin.familyhistoryclient.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.*;

import e.kevin.familyhistoryclient.Helpers.GetPeopleTask;
import e.kevin.familyhistoryclient.R;
import e.kevin.familyhistoryclient.Models.SharedData;

/**
 * Handles all the settings available to the user
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    /*
    Set available options for drop downs
     */
    String[] colors = {"Blue", "Red", "Yellow", "Orange", "Pink", "Purple", "Green"};
    String[] mapTypes = {"Normal", "Hybrid", "Satellite", "Terrain"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*
        Set up drop downs and switches
         */
        Spinner storyColor = findViewById(R.id.lifeStoryColors);
        Spinner treeColor = findViewById(R.id.familyTreeColors);
        Spinner spouseColor = findViewById(R.id.spouseColor);
        Spinner mapType = findViewById(R.id.mapType);
        Switch treeSwitch = findViewById(R.id.familyTreeSwitch);
        Switch spouseSwitch = findViewById(R.id.spouseSwitch);
        Switch storySwitch = findViewById(R.id.lifeStorySwitch);
        ConstraintLayout logout = findViewById(R.id.logout);
        ConstraintLayout resync = findViewById(R.id.resync);

        ArrayAdapter<String> colorOptions = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, colors);
        colorOptions.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<String> mapOptions = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mapTypes);
        mapOptions.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        /*
        Assign values to drop downs
         */
        spouseColor.setAdapter(colorOptions);
        storyColor.setAdapter(colorOptions);
        treeColor.setAdapter(colorOptions);
        mapType.setAdapter(mapOptions);

        /*
        Set drop downs and switches to match present values
         */
        spouseColor.setSelection(colorOptions.getPosition(SharedData.model.getSettings().get("spouselines").getValue()));
        spouseSwitch.setChecked(SharedData.model.getToggles().get("spouselines"));
        treeColor.setSelection(colorOptions.getPosition(SharedData.model.getSettings().get("treelines").getValue()));
        treeSwitch.setChecked(SharedData.model.getToggles().get("treelines"));
        storyColor.setSelection(colorOptions.getPosition(SharedData.model.getSettings().get("storylines").getValue()));
        storySwitch.setChecked(SharedData.model.getToggles().get("storylines"));
        mapType.setSelection(mapOptions.getPosition(SharedData.model.getSettings().get("map_type").getValue()));

        /*
        Set onclick listeners
         */
        spouseColor.setOnItemSelectedListener(this);
        treeColor.setOnItemSelectedListener(this);
        storyColor.setOnItemSelectedListener(this);
        mapType.setOnItemSelectedListener(this);
        storySwitch.setOnCheckedChangeListener(this);
        spouseSwitch.setOnCheckedChangeListener(this);
        treeSwitch.setOnCheckedChangeListener(this);

        /*
        If the logout button is selected, set logged in flag to false and go back to the main activity.
         */
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedData.model.setLoggedIn(false);
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        /*
        If the resync button is selected, simply request new data from the server without logging out.
         */
        resync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPeopleTask handler = new GetPeopleTask();
                handler.setContext(getBaseContext());
                handler.setActivity(SettingsActivity.this);
                handler.execute(SharedData.model.getUrl(), SharedData.model.getAuthToken());
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Object filter = parent.getItemAtPosition(position);
        @SuppressLint("UseSparseArrays") HashMap<Integer, String> filterNames = new HashMap<>();
        filterNames.put(R.id.spouseColor, "spouselines");
        filterNames.put(R.id.lifeStoryColors, "storylines");
        filterNames.put(R.id.familyTreeColors, "treelines");
        filterNames.put(R.id.mapType, "map_type");

        String settingName = filterNames.get(parent.getId());
        SharedData.model.setSetting(settingName, filter.toString());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        @SuppressLint("UseSparseArrays") Map<Integer, String> switches = new HashMap<>();
        switches.put(R.id.familyTreeSwitch, "treelines");
        switches.put(R.id.spouseSwitch, "spouselines");
        switches.put(R.id.lifeStorySwitch, "storylines");

        String switchString = switches.get(buttonView.getId());

        SharedData.model.getToggles().put(switchString, isChecked);
    }
}
