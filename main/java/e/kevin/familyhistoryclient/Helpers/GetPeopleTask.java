package e.kevin.familyhistoryclient.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import e.kevin.familyhistoryclient.Models.PersonModel;
import e.kevin.familyhistoryclient.Models.SharedData;

/**
 * Gets all people from the server and then kicks off the task for getting events
 */
public class GetPeopleTask extends AsyncTask<String, Void, JSONObject> {
    private String baseUrl;
    private String authorization;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
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
    public void setActivity(Activity activity) {
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
        baseUrl = params[0];
        SharedData.model.setUrl(baseUrl);
        authorization = params[1];
        String url = baseUrl + "/person/";
        try {
            urlParam = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        /*
        Call the connection manager to get data from the server, then pass the results to the onPostExecute method
        */
        try {
            return ConnectionManager.connectionManager(urlParam, null, authorization);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Breaks down the data from the server, creates person objects, then initiates the event grabber task
     *
     * @param response JSONObject containing info from the server
     */
    @Override
    protected void onPostExecute(JSONObject response) {

        /*
        Display an error if we got one from the server, or a generic error if something else went wrong
        */
        if (response == null || response.has("message")) {
            String message = "Failed to pull user data";
            try {
                assert response != null;
                message = response.has("message") ? response.get("message").toString() : "Failed to pull user data";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();

        /*
        If we do have data, break it down into personModel objects
        */
        } else {
            try {
                JSONArray data = response.getJSONArray("data");
                int dataCount = data.length();
                SharedData.model.getPeople().clear();
                SharedData.model.getEvents().clear();
                for (int i = 0; i < dataCount; i++) {
                    JSONObject personObj = data.getJSONObject(i);
                    PersonModel person = new PersonModel();
                    person.setId(personObj.getString("personID"));
                    person.setFirstName(personObj.getString("firstName"));
                    person.setLastName(personObj.getString("lastName"));
                    person.setGender(personObj.getString("gender").charAt(0));
                    try {
                        person.setSpouseId(personObj.getString("spouse"));
                        person.setFatherId(personObj.getString("father"));
                        person.setMotherId(personObj.getString("mother"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*
                    Store people objects for the duration of our session
                     */
                    SharedData.model.addPerson(person.getId(), person);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*
            Kick off task to grab events
             */
            GetEventsTask handler = new GetEventsTask();
            handler.setContext(context);
            handler.setActivity(activity);
            handler.execute(this.baseUrl, this.authorization);
        }
    }
}
