package e.kevin.familyhistoryclient.Helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.*;

import java.io.IOException;
import java.net.*;

/**
 * HTTP logic for getting user first and last name
 */
public class PersonIdHandler extends AsyncTask<String, Void, JSONObject> {
    private URL URLParam;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    /**
     * Builds URL and prepares the request for the server
     * @param url URL for the server
     * @param appContext Context needed to display a toast
     * @param uri URI path needed to get correct info from the server
     * @param optionalId If an id is passed, we can get a specific person. In this case, the user
     */
    PersonIdHandler(String url, Context appContext, String uri, String optionalId) {
        String urlBuilder = url + uri;
        this.context = appContext;
        if (optionalId != null) {
            urlBuilder = urlBuilder + "/" + optionalId;
        }

        try {
            this.URLParam = new URL(urlBuilder);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends the requested data to the server via the connection manager and returns the JSONObject retrieved from the server
     * @param params AuthToken for the server
     * @return
     * JSON object with info from the server
     */
    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            return ConnectionManager.connectionManager(URLParam, null, params[0]);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Displays either an error message from the server or the user's first and last name
     * @param loginResponse JsonObject with info from the server
     */
    @Override
    protected void onPostExecute(JSONObject loginResponse) {
        /*
        Display an error message if we got one from the server
         */
        if (loginResponse == null || loginResponse.has("message")) {
            String message = "Failed to get user data.";
            try {
                assert loginResponse != null;
                message = loginResponse.has("message") ? loginResponse.get("message").toString() : "Failed to get user data.";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
            /*
            Otherwise, display user's first and last name
             */
        } else {
            try {
                String firstName = loginResponse.getString("firstName");
                String lastName = loginResponse.getString("lastName");
                Toast toast = Toast.makeText(context, firstName + " " + lastName, Toast.LENGTH_LONG);
                toast.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
