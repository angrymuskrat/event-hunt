package com.eventhunt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Shiplayer on 30.05.18.
 */

public class AddInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = AddInfoWindowAdapter.class.getSimpleName();

    private final View view;
    private final View view2;

    AddInfoWindowAdapter(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.info_window_layout, null);
        view2 = LayoutInflater.from(context).inflate(R.layout.info_window_added_event_layout, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if((int) marker.getTag() == 0) {
            Log.w(TAG, "InfoWindow");
            TextView xPosition = view.findViewById(R.id.tv_x_position);
            TextView yPosition = view.findViewById(R.id.tv_y_position);
            String snippet = marker.getSnippet();
            xPosition.setText(snippet.split(";")[0]);
            yPosition.setText(snippet.split(";")[1]);
            return view;
        } else {
            TextView title = view2.findViewById(R.id.iw_title_event);
            title.setText(marker.getTitle());
            TextView type = view2.findViewById(R.id.iw_type_event);
            type.setText(marker.getSnippet());
            return view2;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.w(TAG, "InfoContents");
        return null;
    }
}
