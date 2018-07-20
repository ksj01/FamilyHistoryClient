package e.kevin.familyhistoryclient.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ConnectionManager handles http requests to the server and returns the JSON returned
 */
class ConnectionManager {

    /**
     * Sends any passed data to the URL provided.
     * @param url Full URI to send data to
     * @param requestBody Data to be sent to the server, if any
     * @param authKey AuthKey used for GET requests
     * @return JSON object containing server response
     * @throws IOException Thrown if the OutputStreams fails for some reason
     */
    static JSONObject connectionManager(URL url, JSONObject requestBody, String authKey) throws IOException {
System.out.println(url.toString());
        /*
        Open HTTP Connection
         */
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        try {
            /*
            Attach Authorization header if passed in
             */
            if (authKey != null) {
                conn.addRequestProperty("Authorization", authKey);
            }
            /*
            Send request body if passed in
             */
            if (requestBody != null) {
                conn.setRequestMethod("POST");
                String output = requestBody.toString();

                conn.setDoOutput(true);

                conn.setChunkedStreamingMode(0);
                conn.getOutputStream();
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(output.getBytes());
                os.flush();
            }
            else {
                conn.setRequestMethod("GET");
            }
            /*
            Make connection
             */
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                /*
                Get server response
                 */
                InputStream responseBody = new BufferedInputStream(conn.getInputStream());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = responseBody.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }

                String responseBodyData = baos.toString();
                try {
                    /*
                    Return server response as a JSONObject
                     */
                    return new JSONObject(responseBodyData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                /*
                Return null if connection fails
                 */
                conn.disconnect();
                return null;
            }
        } catch (IOException e) {
            /*
            Return null if the OutputStream fails
             */
            System.out.println(e.getMessage());
            conn.disconnect();
            return null;
        }
        /*
        Return null if everything goes wrong.
         */
        return null;
    }

}
