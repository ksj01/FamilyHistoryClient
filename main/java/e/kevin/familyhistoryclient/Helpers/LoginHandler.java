package e.kevin.familyhistoryclient.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.*;

import java.io.IOException;
import java.net.*;

import e.kevin.familyhistoryclient.Fragments.LoginFragment;
import e.kevin.familyhistoryclient.Models.SharedData;

/**
 * HTTP logic for logging a user in
 */
public class LoginHandler extends AsyncTask<String, Void, JSONObject> {
    private LoginFragment loginFragment;
    private URL URLParam;
    private String baseURL;
    private JSONObject jsonObjSend;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    /**
     * Builds the necessary URL and gets JSON request ready to be sent
     * @param loginFragment Login fragment gives us the necessary activity for the event and person getters
     * @param url URL for the server
     * @param jsonObjSend Request data to be sent to the server
     * @param appContext We need the current context so the event and person getters can function properly
     * @param uri URI for logging a user in to the server
     */
    public LoginHandler(LoginFragment loginFragment, String url, JSONObject jsonObjSend, Context appContext, String uri) {
        this.loginFragment = loginFragment;
        String urlBuilder = url + uri;
        this.baseURL = url;
        this.jsonObjSend = jsonObjSend;
        this.context = appContext;

        try {
            this.URLParam = new URL(urlBuilder);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends the necessary parameters to the connection manager so we can get a Json Object from the server
     * @param params URL and Json parameters for accessing the server
     * @return JSONObject with data from the server
     */
    @Override
    protected JSONObject doInBackground(String... params) {

        try {
            return ConnectionManager.connectionManager(URLParam, jsonObjSend, null);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Handles the logic for breaking down the response from the server
     * @param loginResponse Response data from the server
     */
    @Override
    protected void onPostExecute(JSONObject loginResponse) {
        /*
        Display an error if we got one from the server, or a generic error if something else went wrong
        */
        if (loginResponse == null || loginResponse.has("message")) {
            String message = "Failed to log in";
            try {
                assert loginResponse != null;
                message = loginResponse.has("message") ? loginResponse.get("message").toString() : "Failed to log in";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        } else {
            /*
            If we do have data, get the authToken and send it to the PersonHandler so we can start getting the user's first and last name
            */
            try {
                String auth = loginResponse.getString("authToken");
                SharedData.model.setAuthToken(auth);
                LoginFragment.personId = loginResponse.getString("personId");
                SharedData.model.setUser(LoginFragment.personId);
                PersonIdHandler handler = new PersonIdHandler(baseURL, context, "/person", LoginFragment.personId);
                handler.execute(auth);
                GetPeopleTask syncHandler = new GetPeopleTask();
                syncHandler.setContext(context);
                syncHandler.setActivity(loginFragment.getActivity());
                syncHandler.execute(baseURL, auth);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
