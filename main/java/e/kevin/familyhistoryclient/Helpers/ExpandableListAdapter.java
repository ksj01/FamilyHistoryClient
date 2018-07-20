package e.kevin.familyhistoryclient.Helpers;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import e.kevin.familyhistoryclient.Models.SharedData;
import e.kevin.familyhistoryclient.R;

/**
 * Pretty basic ExpandableListAdapter. Handles the logic for displaying expandable lists
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;

    /**
     * Constructor for the adapter
     * @param context context for the Activity displaying the expander
     * @param listDataHeader Header info for the expanders
     * @param listChildData All the children to be displayed in the expanders
     */
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    /**
     * Bulk of the unique logic for displaying objects in the adapter
     * @param groupPosition Position of the group we're expanding
     * @param childPosition Position of the child object that we're going to be creating
     * @param isLastChild Boolean specifying if this is the last child in the group
     * @param convertView Current view
     * @param parent Parent group for the expander
     * @return Returns the newly inflated view
     */
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        /*
        Gets the text to be displayed on the generated object
         */
        final String childText = (String) getChild(groupPosition, childPosition);

        /*
        Create and inflate the new view
         */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.person_item, null);
        }

        /*
        Get and set the object text
         */
        TextView txtListChild = convertView
                .findViewById(R.id.item);
        txtListChild.setText(childText);

        /*
        Build the icon for whatever object we're making (gender or event marker)
         */
        int color;
        if (childText.contains("female")) {
            color = ContextCompat.getColor(convertView.getContext(), R.color.Pink);
        } else if (childText.contains("male")) {
            color = ContextCompat.getColor(convertView.getContext(), R.color.Blue);
        } else {
            int start = childText.indexOf('}');
            int end = childText.indexOf(':');
            String testText = childText.substring(start + 2, end);
            if (SharedData.model.getColors().containsKey(testText.toLowerCase())) {
                color = SharedData.model.getColors().get(testText.toLowerCase());
            } else {
                color = R.color.Blue;
            }
        }

        /*
        Set the appropriate icon color
         */
        txtListChild.setTextColor(color);

        return convertView;
    }

    /**
     * This is the only other unique method in here, and is used to determine how many adapters we need and what the they should be titled as
     * @param groupPosition Group position of the adapter we're currently building
     * @param isExpanded Boolean for whether the adapter is expanded and if we should make children objects
     * @param convertView Current view
     * @param parent Parent group for the expander
     * @return
     */
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String title = (String) getGroup(groupPosition);
        /*
        Create and inflate view
         */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.expander_group, null);
        }

        /*
        Set the header text
         */
        TextView header = convertView
                .findViewById(R.id.expander_header);
        header.setText(title);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
}