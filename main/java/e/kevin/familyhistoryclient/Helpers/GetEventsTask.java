package e.kevin.familyhistoryclient.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.*;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

import e.kevin.familyhistoryclient.Activities.*;
import e.kevin.familyhistoryclient.Fragments.*;
import e.kevin.familyhistoryclient.Models.*;
import e.kevin.familyhistoryclient.R;

/**
 * Handles the logic for getting all events from the server and assigning them to people objects
 */
public class GetEventsTask extends AsyncTask<String, Void, JSONObject> {
    @SuppressLint("StaticFieldLeak")
    private
    Context context;
    @SuppressLint("StaticFieldLeak")
    private
    Activity activity;
    private URL urlParam;

    /**
     * Sets the context so we can use this class statically
     *
     * @param c context that we'll use at the time this is called.
     */
    public void setContext(Context c) {
        context = c;
    }

    /**
     * Sets the activity so we can use this class statically
     *
     * @param activity activity that we'll use at the time this is called.
     */
    void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Setup the info so we can connect to the server, then get the data
     *
     * @param params URL and authkey Params
     * @return JSONObject containing info from the server
     */
    @Override
    protected JSONObject doInBackground(String... params) {
        /*
        Build URL and get authKey
         */
        String baseUrl = params[0];
        String authorization = params[1];
        String url = baseUrl + "/event/";
        try {
            urlParam = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            /*
            Call the connection manager to get data from the server, then pass the results to the onPostExecute method
             */
            return ConnectionManager.connectionManager(urlParam, null, authorization);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Breaks down the data from the server, creates event objects, then assigns those events to people
     *
     * @param response JSONObject containing info from the server
     */
    @Override
    protected void onPostExecute(JSONObject response) {
        /*
        Display an error if we got one from the server, or a generic error if something else went wrong
         */
        if (response == null || response.has("message")) {
            String message = "Failed to pull user events";
            try {
                assert response != null;
                message = response.has("message") ? response.get("message").toString() : "Failed to pull user events";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        } else {
            /*
            If we do have data, break it down into EventModel objects
             */
            try {
                JSONArray data = response.getJSONArray("data");
                int dataCount = data.length();
                for (int i = 0; i < dataCount; i++) {
                    JSONObject eventObj = data.getJSONObject(i);
                    EventModel event = new EventModel();
                    event.setId(eventObj.getString("eventID"));
                    event.setPersonId(eventObj.getString("personID"));
                    event.setLatitude(eventObj.getDouble("latitude"));
                    event.setLongitude(eventObj.getDouble("longitude"));
                    event.setCountry(eventObj.getString("country"));
                    event.setCity(eventObj.getString("city"));
                    event.setType(eventObj.getString("eventType"));
                    event.setYear(Integer.parseInt(eventObj.getString("year")));

                    /*
                    Store events for the duration of our session
                     */
                    SharedData.model.addEvent(event.getId(), event);
                    /*
                    Attach events to their respective people
                     */
                    addEventToPerson(event);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
            Determine which events are paternal and which are maternal
             */
            discoverSides();
            SharedData.model.setLoggedIn(true);
            /*
            If we're in the main activity, then we know we need to go to the map after getting data
            Otherwise we're doing a resync and should handle things accordingly
             */
            if (activity instanceof MainActivity) {
                MainActivity mainActivity = ((MainActivity) activity);
                mainActivity.onPrepareOptionsMenu(mainActivity.get_menu());
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new EventDetailsFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast toast = Toast.makeText(context, "Resync Succeeded", Toast.LENGTH_LONG);
                toast.show();
                if (activity instanceof SettingsActivity) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            }
        }
    }

    /**
     * Recursively goes through the family and assigns a value stating if the event belongs to the father's side, mother's side, or belongs to the user or their spouse.
     */
    private void discoverSides() {
        HashMap<String, PersonModel> people = SharedData.model.getPeople();
        PersonModel user = people.get(SharedData.model.getUser());
        user.setSide("user");
        PersonModel spouse = SharedData.model.getPeople().get(user.getSpouseId());
        /*
        User's spouse should neither be filtered out by mother's side filters or father's side filters
         */
        if (spouse != null) {
            spouse.setSide("user");
        }
        /*
        Recursively go through father's side of the family and assign them all a father variable for later filtering
         */
        PersonModel father = SharedData.model.getPeople().get(user.getFatherId());
        if (father != null) {
            discoverSidesHelper(father, "father");
        }
        /*
        Recursively go through mother's side of the family and assign them all a mother variable for later filtering
         */
        PersonModel mother = SharedData.model.getPeople().get(user.getMotherId());
        if (mother != null) {
            discoverSidesHelper(mother, "mother");
        }
    }

    /**
     * Recursive helper for assigning either a mother or father variable to each person for later filtering
     *
     * @param person Person whose family is being assigned a variable
     * @param side   Variable that we'll assign this person and all ancestors
     */
    private void discoverSidesHelper(PersonModel person, String side) {
        person.setSide(side);

        /*
        Recursively set this person's father's side as the selected side
         */
        PersonModel father = SharedData.model.getPeople().get(person.getFatherId());
        if (father != null) {
            discoverSidesHelper(father, side);
        }

        /*
        Recursively set this person's mother's side as the selected side
         */
        PersonModel mother = SharedData.model.getPeople().get(person.getMotherId());
        if (mother != null) {
            discoverSidesHelper(mother, side);
        }

        /*
        Recursively set this person's spouse as the selected side
         */
        PersonModel spouse = SharedData.model.getPeople().get(person.getSpouseId());
        if (spouse != null) {
            spouse.setSide(side);
        }

    }

    /**
     * Attaches the passed event to the event's associated person
     *
     * @param event event to be tied to a person
     */
    private void addEventToPerson(EventModel event) {
        PersonModel person = SharedData.model.getPeople().get(event.getPersonId());
        person.attachEvent(event);
    }
}
