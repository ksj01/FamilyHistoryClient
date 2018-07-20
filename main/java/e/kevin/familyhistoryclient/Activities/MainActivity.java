package e.kevin.familyhistoryclient.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.*;

import com.joanzapata.iconify.*;
import com.joanzapata.iconify.fonts.*;

import e.kevin.familyhistoryclient.Fragments.*;
import e.kevin.familyhistoryclient.*;
import e.kevin.familyhistoryclient.Models.*;

/**
 * Main activity.
 * Checks whether the user is logged in or not and displays either a map fragment or a login fragment based on logged in status.
 */
public class MainActivity extends AppCompatActivity {

    private Menu _menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_main);

        /*
          Login Fragment is always displayed on first start.
         */
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main);
        if (fragment == null) {
            fragment = new LoginFragment();
            fm.beginTransaction().add(R.id.main, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this._menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        /*
        Create menu icons
         */
        menu.findItem(R.id.search_menu).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_search)
                        .colorRes(R.color.White)
                        .actionBarSize());
        menu.findItem(R.id.filter_menu).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_filter)
                        .colorRes(R.color.White)
                        .actionBarSize());
        menu.findItem(R.id.settings_menu).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.White)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem option) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);

        /*
        Set up listener for menu options
         */
        int id = option.getItemId();
        if (id == R.id.filter_menu) {
            intent = new Intent(MainActivity.this, FilterActivity.class);
        } else if (id == R.id.search_menu) {
            intent = new Intent(MainActivity.this, SearchActivity.class);
        } else if (id == R.id.settings_menu) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
        }
        startActivity(intent);
        return super.onOptionsItemSelected(option);
    }

    /**
     * Determines if the user should be directed to the map fragment or if they need to log in again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main);
        Bundle extras = getIntent().getExtras();
        boolean auth = SharedData.model.isLoggedIn();

        if (fragment == null) {
            if (auth) {
                fragment = new EventDetailsFragment();
                fragment.setArguments(extras);
            } else {
                fragment = new LoginFragment();
            }
            fm.beginTransaction().add(R.id.main, fragment).commit();
        } else {
            if (auth) {
                fragment = new EventDetailsFragment();
                fragment.setArguments(extras);
            } else {
                fragment = new LoginFragment();
            }
            fm.beginTransaction().replace(R.id.main, fragment).commit();
        }
    }

    public Menu get_menu() {
        return _menu;
    }
}
