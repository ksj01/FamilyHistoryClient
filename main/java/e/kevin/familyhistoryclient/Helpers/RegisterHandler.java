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
 * HTTP logic for registering a user
 */
public class RegisterHandler extends AsyncTask<String, Void, JSONObject> {
    private LoginFragment loginFragment;
    private URL URLParam;
    private String baseURL;
    private JSONObject jsonObjSend;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    /**
     * Builds the URL info to connect to the server and prepares the data to be sent
     * @param loginFragment LoginFragment provides necessary activity for handling people and events later in the process
     * @param url URL of the server
     * @param jsonObjSend JsonObject with info to send to the server
     * @param appContext Context needed for toasts and such
     * @param uri URI of the rregistration path
     */
    public RegisterHandler(LoginFragment loginFragment, String url, JSONObject jsonObjSend, Context appContext, String uri) {
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
     * Sends the necessary parameters to the connection manager so we can register a user
     * @param params URL and Json parameters for accessing the server and sending user registration info
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
     * @param registerResponse Response data from the server
     */
    @Override
    protected void onPostExecute(JSONObject registerResponse) {
        /*
        Display an error if we got one from the server, or a generic error if something else went wrong
        */
        if (registerResponse == null || registerResponse.has("message")) {
            String message = "Failed to register user";
            try {
                assert registerResponse != null;
                message = registerResponse.has("message") ? registerResponse.get("message").toString() : "Failed to register user";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
        }
        try {
            /*
            If we do get data back from the server, we get the authcode and person ID and continue on to log the user in and get event and person data
             */
            String auth = registerResponse.getString("authToken");
            String personId = registerResponse.getString("personId");
            SharedData.model.setUser(personId);
            PersonIdHandler handler = new PersonIdHandler(baseURL, context, "/person", personId);
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

