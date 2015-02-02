package org.onebrick.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.onebrick.android.R;
import org.onebrick.android.helpers.FontsHelper;
import org.onebrick.android.models.Chapter;

import java.util.List;

public class NavigationChapterListAdapter extends ArrayAdapter<Chapter> {
    TextView tvNavChapterName;
    public NavigationChapterListAdapter(Context context, int resource, List<Chapter> objects) {
        super(context, R.layout.drawer_nav_item, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Chapter chp = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_nav_item, parent, false);
        }
        // Lookup view for data population
        tvNavChapterName = (TextView) convertView.findViewById(R.id.tvNavChapter);
        tvNavChapterName.setText(chp.getChapterName());
        tvNavChapterName.setTypeface(FontsHelper.getRobotoRegular());
        // Return the completed view to render on screen
        return convertView;
    }

}
