package e.kevin.familyhistoryclient.Helpers;

import org.json.JSONObject;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

/**
 *
 */
public class ConnectionManagerTest {
    @Test
    public void connectionManager() throws Exception {

        //Test for Registration, Login, retrieving people, and retrieving events
        String ipAddress = "http://192.168.29.249";
        String port = "6060";
        String url = ipAddress + ":" + port + "/user/login";
        URL urlBuilt = new URL(url);

        JSONObject requestBody1 = new JSONObject();
        requestBody1.put("userName", "sheila");
        requestBody1.put("password", "parker");
        JSONObject results = ConnectionManager.connectionManager(urlBuilt, requestBody1, null);
        assertTrue(results.has("authToken"));
        assertFalse(results.has("message"));

        JSONObject requestBody2 = new JSONObject();
        requestBody2.put("userName", "lksjdflksj");
        requestBody2.put("password", "lksjdflo");
        JSONObject results2 = ConnectionManager.connectionManager(urlBuilt, requestBody2, null);

        assertFalse(results2.has("authToken"));
        assertTrue(results2.has("message"));

    }

}