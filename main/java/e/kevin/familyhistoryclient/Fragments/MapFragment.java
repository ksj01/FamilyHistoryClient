package e.kevin.familyhistoryclient.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import e.kevin.familyhistoryclient.Models.EventModel;
import e.kevin.familyhistoryclient.Models.PersonModel;
import e.kevin.familyhistoryclient.R;
import e.kevin.familyhistoryclient.Models.SharedData;

/**
 * Main logic center for the Family History Client.
 * Handles map views, generating family history lines, life event lines, spouse lines, drawing markers, and deals with assigning events a color
 */
public class MapFragment extends SupportMapFragment {
    private GoogleMap curMap;
    public static PersonModel currentPerson = null;
    private EventModel currentEvent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            currentEvent = (EventModel) getArguments().getSerializable("event");
            currentPerson = (PersonModel) getArguments().getSerializable("person");
        }

        /*
        Start by assigning distinct events a distinct color so we can draw the markers correctly.
         */
        mapColors(setColors());
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setHasOptionsMenu(true);

        /*
        Google does some fancy map stuff
         */
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                curMap = googleMap;
                /*
                Get map display style
                 */
                mapType();

                /*
                Add the markers to the map for all events that haven't been filtered out.
                 */
                addMarkers();

                /*
                Center on current event if exists
                 */
                if (currentEvent != null) {
                    PersonModel person = SharedData.model.getPeople().get(currentEvent.getPersonId());

                    /*
                    Create the event details popup
                     */
                    buildEvent(person, currentEvent);
                    zoomToEvent(currentEvent);
                }

                /*
                Put onclick listeners on all markers
                 */
                curMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        EventModel event = SharedData.model.getMarkers().get(marker);
                        PersonModel person = SharedData.model.getPeople().get(event.getPersonId());
                        currentPerson = person;
                        buildEvent(person, event);
                        zoomToEvent(event);
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (curMap != null) {
            /*
            Clear the map so old lines and filtered events don't persist
             */
            curMap.clear();

            /*
            Verify that the map style hasn't changed
             */
            mapType();

            /*
            Add new markers
             */
            addMarkers();
        }
    }

    /**
     * Adds markers to the map for all events that haven't been filtered out
     */
    private void addMarkers() {
        /*
        Set up expected icons and other map info
         */
        IconDrawable icon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_map_marker)
                .sizeDp(30)
                .alpha(255);

        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);

        /*
        Get all events that have survived the filter
         */
        HashMap<String, EventModel> events = SharedData.model.getFilteredEvents();
        Set<String> eventKeys = events.keySet();
        String[] keyIds = eventKeys.toArray(new String[eventKeys.size()]);

        /*
        Iterate through those events to add their icon to the map
         */
        for (int i = 0; i < eventKeys.size(); i++) {
            EventModel event = events.get(keyIds[i]);

            /*
            Get the marker color based on the event type
             */
            icon.colorRes(markerColor(event.getType()));
            icon.draw(canvas);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            Marker marker = curMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptor)
                    .position(location));

            SharedData.model.addMarker(marker, event);
        }
    }

    /**
     * Draws a line between the selected event and the event person's spouse
     *
     * @param event  Event that we're currently viewing
     * @param spouse spouse of the current user
     */
    public void connectSpouse(EventModel event, PersonModel spouse) {
        LatLng location = new LatLng(event.getLatitude(), event.getLongitude());

        /*
        Make sure the spouse has events to link to
         */
        if (spouse.getYearKeyed().size() > 0) {
            EventModel spouseEvent = spouse.getYearKeyed().get(spouse.getYears()[0]);
            if (spouseEvent != null) {

                /*
                Draw a polyline if we can
                 */
                LatLng spouseLocation = new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude());
                String color = SharedData.model.getSettings().get("spouselines").getValue();
                curMap.addPolyline(new PolylineOptions()
                        .add(location, spouseLocation)
                        .width(6)
                        .color(getColor(color)));
            }
        }
    }

    /**
     * Recursively draws line from the selected person's selected event and draws a line to the earliest father and mother events that aren't filtered out.
     *
     * @param person    starting person
     * @param event     starting event
     * @param thickness current thickness of the drawn line
     */
    public void drawFamilyTree(PersonModel person, EventModel event, float thickness) {
        /*
        Start a list of points to add lines between
         */
        List<LatLng> familyPoints = new ArrayList<>();
        familyPoints.add(event.getLatLng());

        /*
        Get the current color we expect for family tree lines
         */
        String color = SharedData.model.getSettings().get("treelines").getValue();

        /*
        Make sure we're only working with the people who survived the filter
         */
        HashMap<String, PersonModel> people = SharedData.model.getFilteredPeople();
        PersonModel father = people.get(person.getFatherId());
        if (father != null) {
            /*
            If we have a dad, get his earliest event
             */
            EventModel fatherEvent = father.getYearKeyed().get(father.getYears()[0]);
            if (fatherEvent != null) {
                /*
                Reduce the thickness of the line for each generation. We clone it so that we don't double reduce between the father and mother
                 */
                float thicknessClone = thickness;
                if (thicknessClone > 4) {
                    thicknessClone -= 4;
                }
                /*
                Add the line
                 */
                familyPoints.add(fatherEvent.getLatLng());
                PolylineOptions polylineOptions = new PolylineOptions().addAll(familyPoints).color(getColor(color)).width(thickness);
                curMap.addPolyline(polylineOptions);

                /*
                Recursively do the same for the father
                 */
                drawFamilyTree(father, fatherEvent, thicknessClone);
            }
        }

        /*
        Repeat the same as above, but this time for the mom.
         */
        List<LatLng> motherPoints = new ArrayList<>();
        motherPoints.add(event.getLatLng());
        PersonModel mother = people.get(person.getMotherId());
        if (mother != null) {
            EventModel motherEvent = mother.getYearKeyed().get(mother.getYears()[0]);
            if (motherEvent != null) {
                float thicknessClone = thickness;
                if (thicknessClone > 4) {
                    thicknessClone -= 4;
                }
                motherPoints.add(motherEvent.getLatLng());
                PolylineOptions polylineOptions = new PolylineOptions().addAll(motherPoints).color(getColor(color)).width(thickness);
                curMap.addPolyline(polylineOptions);
                drawFamilyTree(mother, motherEvent, thicknessClone);
            }
        }
    }


    /**
     * Draws a line in chronological order for the selected person's life events
     * @param event event that we're currently centered on
     */
    public void drawLifeStory(EventModel event) {
        /*
        Get the current event's person and chronological events
         */
        PersonModel person = SharedData.model.getPerson(event.getPersonId());
        int numEvents = person.getYearKeyed().size();
        /*
        Get life story line color
         */
        String color = SharedData.model.getSettings().get("storylines").getValue();
        EventModel event1;
        EventModel event2;
        /*
        Add lines between each event in order
         */
        for (int i = 0; i < numEvents - 1; i++) {
            event1 = person.getYearKeyed().get(person.getYears()[i]);
            event2 = person.getYearKeyed().get(person.getYears()[i + 1]);
            if (event1 != null && event2 != null) {
                LatLng latLon1 = new LatLng(event1.getLatitude(), event1.getLongitude());
                LatLng latLon2 = new LatLng(event2.getLatitude(), event2.getLongitude());
                curMap.addPolyline(new PolylineOptions()
                        .add(latLon1, latLon2)
                        .width(6)
                        .color(getColor(color)));
            }
        }
    }

    /**
     * Zooms in on an event when one is selected
     * @param event event to zoom in on
     */
    private void zoomToEvent(EventModel event) {
        LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
        curMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 1));
        /*
        We clear the map so we don't end up with extra lines and such
         */
        curMap.clear();
        addMarkers();

        /*
        Draw all necessary lines going out from this event
         */
        PersonModel person = SharedData.model.getPerson(event.getPersonId());
        if (SharedData.model.getToggles().get("spouselines")) {
            PersonModel spouse = SharedData.model.getFilteredPeople().get(person.getSpouseId());
            if (spouse != null) {
                connectSpouse(event, spouse);
            }
        }
        if (SharedData.model.getToggles().get("treelines")) {
            drawFamilyTree(person, event, 14);
        }
        if (SharedData.model.getToggles().get("storylines")) {
            drawLifeStory(event);
        }
    }

    /**
     * Builds the event info for the current event popup
     * @param person person to whom the selected event belongs
     * @param event event we want to show
     */
    private void buildEvent(PersonModel person, EventModel event) {

        /*
        Gather up all data for the event and person to be displayed
         */
        EventDetailsFragment.name.setText(person.getFullName());
        EventDetailsFragment.info.setText(event.getInfo());
        Icon fa_gender = person.getGender() == 'm' ? FontAwesomeIcons.fa_male : FontAwesomeIcons.fa_female;
        int color = person.getGender() == 'm' ? R.color.Blue : R.color.Pink;
        EventDetailsFragment.icon.setImageDrawable(
                new IconDrawable(getActivity(), fa_gender)
                        .sizeDp(40)
                        .colorRes(color)
        );
        currentPerson = person;
    }

    /**
     * Gets the color for the lines that we're going to draw
     * @param color name of the color we're looking for
     * @return ID for the color we want
     */
    private int getColor(String color) {
        int colorId;
        Map<String, Integer> type = new HashMap<>();
        Context context = getContext();
        type.put("Blue", ContextCompat.getColor(context, R.color.Blue));
        type.put("Red", ContextCompat.getColor(context, R.color.Red));
        type.put("Yellow", ContextCompat.getColor(context, R.color.Yellow));
        type.put("Orange", ContextCompat.getColor(context, R.color.Orange));
        type.put("Pink", ContextCompat.getColor(context, R.color.Pink));
        type.put("Purple", ContextCompat.getColor(context, R.color.Purple));
        type.put("Green", ContextCompat.getColor(context, R.color.Green));
        colorId = type.get(color);
        return colorId;
    }

    /**
     * Sets a ton of colors to be used for event types
     * @return returns a vector full of IDs for different colors
     */
    private Vector<Integer> setColors() {
        Vector<Integer> color = new Vector<>();
        color.add(R.color.Blue);
        color.add(R.color.Red);
        color.add(R.color.Yellow);
        color.add(R.color.Orange);
        color.add(R.color.Pink);
        color.add(R.color.Purple);
        color.add(R.color.a);
        color.add(R.color.b);
        color.add(R.color.c);
        color.add(R.color.d);
        color.add(R.color.e);
        color.add(R.color.f);
        color.add(R.color.g);
        color.add(R.color.h);
        color.add(R.color.i);
        color.add(R.color.j);
        color.add(R.color.k);
        color.add(R.color.l);
        color.add(R.color.m);
        color.add(R.color.n);
        color.add(R.color.o);
        color.add(R.color.p);
        return color;
    }

    /**
     * Assigns a color to every event type
     * @param colorList The big list of colors and IDs that we can work with. Should be a vector of integers
     */
    private void mapColors(Vector<Integer> colorList) {
        HashMap<String, EventModel> events = SharedData.model.getEvents();
        String[] eventIds = events.keySet().toArray(new String[events.keySet().size()]);
        for (int i = 0; i < SharedData.model.getEvents().size(); i++) {
            HashMap<String, Integer> colors;
            colors = SharedData.model.getColors();
            colors.put(events.get(eventIds[i]).getType().toLowerCase(), colorList.get(i));
        }
    }

    /**
     * Gets a color to be used for an event marker based on that event's type
     * @param eventType Type for which we want a color
     * @return Returns an int indicating the color ID
     */
    private int markerColor(String eventType) {
        int colorId;
        HashMap<String, Integer> colorSet = SharedData.model.getColors();
        if (colorSet.containsKey(eventType.toLowerCase())) {
            colorId = colorSet.get(eventType.toLowerCase());
            return colorId;
        } else {
            colorId = R.color.Blue;
            return colorId;
        }
    }

    /**
     * Assigns a map style to the map based on settings.
     */
    private void mapType() {
        Map<String, Integer> type = new HashMap<>();
        type.put("Normal", GoogleMap.MAP_TYPE_NORMAL);
        type.put("Hybrid", GoogleMap.MAP_TYPE_HYBRID);
        type.put("Satellite", GoogleMap.MAP_TYPE_SATELLITE);
        type.put("Terrain", GoogleMap.MAP_TYPE_TERRAIN);
        String typeValue = SharedData.model.getSettings().get("map_type").getValue();
        curMap.setMapType(type.get(typeValue));
    }
}
