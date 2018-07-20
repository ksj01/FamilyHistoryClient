package e.kevin.familyhistoryclient.Fragments;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;

import org.json.*;

import e.kevin.familyhistoryclient.Helpers.*;
import e.kevin.familyhistoryclient.R;
import e.kevin.familyhistoryclient.Models.SharedData;

/**
 * Activity allowing the user to either log in or register
 */
public class LoginFragment extends Fragment {

    private EditText ipAddress;
    private EditText port;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup gender;
    @SuppressLint("StaticFieldLeak")
    public static Button loginButton1;
    private Button registerButton1;
    private char genderChar;
    public static String personId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);
        ipAddress = v.findViewById(R.id.ipAddress);

        /*
        Text watcher allows us to disable the login and register button until the necessary fields are filled out
         */
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int ipLength = ipAddress.length();
                int portLength = port.length();
                int userLength = username.length();
                int passLength = password.length();

                int firstLength = firstName.length();
                int lastLength = lastName.length();
                int emailLength = email.length();

                /*
                IP Address, port number, username, and password are required to login
                 */
                if (ipLength > 0 && portLength > 0 && userLength > 0 && passLength > 0) {
                    loginButton1.setEnabled(false);

                    /*
                    First and last name, email are required for registration
                     */
                    if (firstLength > 0 && lastLength > 0 && emailLength > 0) {
                        registerButton1.setEnabled(true);
                    } else {
                        registerButton1.setEnabled(false);
                    }
                } else {
                    loginButton1.setEnabled(false);
                    registerButton1.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        gatherButtons(v, watcher);
        setOnClickListeners();
        return v;
    }

    /**
     * Gather all info needed for fields and buttons
     * @param v View object
     * @param watcher TextWatcher to verify correct fields are filled out
     */
    public void gatherButtons(View v, TextWatcher watcher) {
        /*
        Get fields
         */
        port = v.findViewById(R.id.port);
        username = v.findViewById(R.id.username);
        password = v.findViewById(R.id.password);
        firstName = v.findViewById(R.id.firstNameTitle);
        lastName = v.findViewById(R.id.lastNameTitle);
        email = v.findViewById(R.id.email);
        gender = v.findViewById(R.id.genderGroup);
        loginButton1 = v.findViewById(R.id.loginButton);

        /*
        Add textchange listeners
         */
        ipAddress.addTextChangedListener(watcher);
        port.addTextChangedListener(watcher);
        username.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        firstName.addTextChangedListener(watcher);
        lastName.addTextChangedListener(watcher);
        email.addTextChangedListener(watcher);
        loginButton1.setEnabled(true);
        registerButton1 = v.findViewById(R.id.registerButton);
        registerButton1.setEnabled(false);
    }

    /**
     * Sets the onclick listener for the login and registration buttons
     */
    private void setOnClickListeners() {

        /*
        Login button just sends login request
         */
        loginButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        /*
        Registration button has to handle the gender radio button logic before we can register
         */
        registerButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int buttonId = gender.getCheckedRadioButtonId();
                View radioButton = gender.findViewById(buttonId);
                int radioId = gender.indexOfChild(radioButton);
                RadioButton genderSelection = (RadioButton) gender.getChildAt(radioId);
                String selection = (String) genderSelection.getText();
                selection = selection.toLowerCase();
                genderChar = selection.charAt(0);
                Register();
            }
        });
    }


    /**
     * Gathers necessary input fields and passes them on to the LoginHandler class to log the user in
     */
    public void Login() {
        JSONObject json = new JSONObject();
        /*
        Only user data needed is username and password
         */
        try {
            json.put("userName", username.getText().toString());
            json.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        Other necessary fields are the ipaddress and port
         */
        String urlBuilder = "http://" + ipAddress.getText().toString() + ":" + port.getText().toString();
        LoginHandler handler = new LoginHandler(this, urlBuilder, json, getContext(), "/user/login");
        handler.execute();

        /*
        We store the username info if we want to resync later
         */
        SharedData.model.setUser(username.getText().toString());
    }

    /**
     * Gathers necessary input fields and passed them to the RegisterHandler class to register the user and then log them in
     */
    public void Register() {
        JSONObject json = new JSONObject();

        /*
        Add necessary data to Json Object
         */
        try {
            json.put("userName", username.getText().toString());
            json.put("password", password.getText().toString());
            json.put("email", email.getText().toString());
            json.put("firstName", firstName.getText().toString());
            json.put("lastName", lastName.getText().toString());
            json.put("gender", genderChar + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        Build the registration URL
         */
        String urlBuilder = "http://" + ipAddress.getText().toString() + ":" + port.getText().toString();
        RegisterHandler handler = new RegisterHandler(this, urlBuilder, json, getContext(), "/user/register");
        handler.execute();
        /*
        Again, we store the username info for a later resync
         */
        SharedData.model.setUrl(username.getText().toString());
    }

}

