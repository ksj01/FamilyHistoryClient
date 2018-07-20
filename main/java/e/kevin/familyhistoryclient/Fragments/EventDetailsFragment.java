package e.kevin.familyhistoryclient.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import e.kevin.familyhistoryclient.Activities.PersonActivity;
import e.kevin.familyhistoryclient.R;

/**
 * Fragment for the little event popup at the bottom of the map view that displays info for selected event
 */
public class EventDetailsFragment extends Fragment {
    private Bundle extras;
    @SuppressLint("StaticFieldLeak")
    public static ImageView icon;
    @SuppressLint("StaticFieldLeak")
    public static TextView name;
    @SuppressLint("StaticFieldLeak")
    public static TextView info;

    private final Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            extras = getArguments();
        }

        /*
        Gather necessary layouts for the fragment to display correctly.
         */
        View v = inflater.inflate(R.layout.fragment_event_popup, container, false);
        name = v.findViewById(R.id.name);
        icon = v.findViewById(R.id.icon);
        info = v.findViewById(R.id.details);
        GridLayout eventDetails = v.findViewById(R.id.eventDetails);

        /*
        Onclick listener redirects to the Person Activity for the selected event
         */
        eventDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MapFragment.currentPerson != null) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        /*
       Creates fragment for the popup when an event is selected, to be displayed on the map.
         */
        super.onActivityCreated(savedInstanceState);
        runnable = new Runnable() {

            @Override
            public void run() {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment mapFragment = fm.findFragmentById(R.id.event_popup);
                if (mapFragment == null) {
                    mapFragment = new MapFragment();
                    if (extras != null) {
                        mapFragment.setArguments(extras);
                    }
                    fm.beginTransaction().add(R.id.event_popup, mapFragment).commit();
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
