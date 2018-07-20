package e.kevin.familyhistoryclient.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.*;

import e.kevin.familyhistoryclient.Helpers.SearchHelper;
import e.kevin.familyhistoryclient.Models.*;
import e.kevin.familyhistoryclient.R;

/**
 * Uses a RecyclerView to display search results based on user input.
 */
public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String query;
    private static ArrayList<Object> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        Create all utilities necessary for a successful search result
         */
        searchResults = new ArrayList<>();
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        setContentView(R.layout.activity_search);
        recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TextInputEditText searchBox = findViewById(R.id.search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*
                When the text is changed, we need to refresh the search results
                 */
                query = charSequence.toString().toLowerCase();
                refresh();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
    }

    /**
     * Passes the search results into a ResultAdapter, then passes that to the recyclerView for display
     */
    private void showResults() {
        List<Object> results = searchResults;

        ResultAdapter adapter = new ResultAdapter(results);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Executes the FetchItemsTask to get new search results
     */
    private void refresh() {
        new FetchItemsTask().execute();
    }

    /**
     * Required class for the RecyclerView. Handles object views.
     * Most of the meat is in the ResultBuilder method or behind the scenes.
     */
    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Object currentObject;
        private ImageView ObjectIcon;
        private TextView ResultItem;

        /**
         * Sets the icon and text of the result tiles
         *
         * @param itemView main view object
         */
        Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            ObjectIcon = itemView.findViewById(R.id.results_list_icon);
            ResultItem = itemView.findViewById(R.id.results_list_item_text);
        }

        /**
         * Builds up the search results based on what was passed in
         *
         * @param object Can either be a PersonModel object or an EventModel object.
         */
        void ResultBuilder(Object object) {
            currentObject = object;


            if (currentObject instanceof PersonModel) {

                /*
                Logic for PersonModel objects
                */

                PersonModel person = (PersonModel) currentObject;
                char gender = person.getGender();

                /*
                Set Name as the display text
                 */
                ResultItem.setText(person.getFullName());
                FontAwesomeIcons icon = gender == 'm' ? FontAwesomeIcons.fa_male : FontAwesomeIcons.fa_female;
                int color = gender == 'm' ? R.color.Blue : R.color.Pink;

                /*
                Icon is based on gender of the person
                 */
                ObjectIcon.setImageDrawable(
                        new IconDrawable(getBaseContext(), icon)
                                .sizeDp(40)
                                .colorRes(color)
                );
            } else if (currentObject instanceof EventModel) {

                /*
                Logic for EventModel objects
                 */
                EventModel event = (EventModel) currentObject;
                FontAwesomeIcons icon = FontAwesomeIcons.fa_map_marker;

                /*
                Color of marker is dependent on event type
                 */
                Map<String, Integer> colorMap = SharedData.model.getColors();
                int color;
                String colorKey = event.getType().toLowerCase();
                if (colorMap.containsKey(colorKey)) {
                    color = colorMap.get(colorKey);
                } else {
                    color = R.color.Blue;
                }
                ObjectIcon.setImageDrawable(
                        new IconDrawable(getBaseContext(), icon)
                                .sizeDp(40)
                                .colorRes(color)
                );
                PersonModel grabbed;
                String personId = event.getPersonId();
                grabbed = SharedData.model.getPerson(personId);
                String name = grabbed.getFullName();
                String year = " (" + event.getYear() + ")";

                /*
                Set event info as the display text
                 */
                String text = event.getType().toLowerCase() + ": " + event.getCity() + ", " + event.getCountry() + year + name;
                ResultItem.setText(text);
            }
        }

        @Override
        public void onClick(View v) {
            if (currentObject instanceof PersonModel) {
                /*
                Open Person Activity if selected item is a person
                 */
                PersonModel person = (PersonModel) currentObject;
                loadPerson(person);
            } else if (currentObject instanceof EventModel) {
                /*
                Open Event View Activity if selected item is an event
                 */
                EventModel event = (EventModel) currentObject;
                PersonModel grabbed = SharedData.model.getPerson(event.getPersonId());
                assert grabbed != null;
                loadEvent(event);
            }
        }
    }

    /**
     * Build new intent and launch activity for selected event
     *
     * @param event Event object to focus on when we move to the map view
     */
    public void loadEvent(EventModel event) {
        Intent intent = new Intent(SearchActivity.this, EventViewActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    /**
     * Build new intent and launch activity for selected person
     *
     * @param person Person object to send to the Person Activity
     */
    public void loadPerson(PersonModel person) {
        Intent i = new Intent(this, PersonActivity.class);
        i.putExtra("person", person);
        startActivity(i);
    }

    /**
     * Necessary class for the search function to work. Handles logic for finding objects that match the search query
     */
    private class ResultAdapter extends RecyclerView.Adapter<Holder> {
        private List<Object> results;

        ResultAdapter(List<Object> results) {
            this.results = results;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater.inflate(R.layout.search, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Object result = results.get(position);
            holder.ResultBuilder(result);
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchItemsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            /*
            Don't display any results if search query is empty
             */
            if (query == null) {
                return false;
            } else if (query.equals("")) {
                /*
                If search query is removed, delete any existing search results
                 */
                searchResults = new ArrayList<>();
            } else {
                searchResults = new ArrayList<>();
                /*
                If search text exists, add items to be displayed
                 */
                SearchHelper.addItems(searchResults, query);
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result) showResults();
        }
    }
}