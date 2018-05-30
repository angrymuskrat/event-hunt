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

    AddInfoWindowAdapter(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.info_window_layout, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.w(TAG, "InfoWindow");
        Button addButton = view.findViewById(R.id.btn_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "add button is clicked");
            }
        });
        Button removeButton = view.findViewById(R.id.btn_remove);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "remove button is clicked");
            }
        });
        TextView xPosition = view.findViewById(R.id.tv_x_position);
        TextView yPosition = view.findViewById(R.id.tv_y_position);
        String snippet = marker.getSnippet();
        xPosition.setText(snippet.split(";")[0]);
        yPosition.setText(snippet.split(";")[1]);
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.w(TAG, "InfoContents");
        return null;
    }
}
