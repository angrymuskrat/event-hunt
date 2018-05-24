package com.eventhunt;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment {
    private static final String TAG = FilterFragment.class.getSimpleName();


    public FilterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        final Spinner typeEvent = view.findViewById(R.id.type_event);
        Spinner typeGenre = view.findViewById(R.id.type_genre);
        Log.w(TAG, "fragment is created");
        ArrayAdapter<CharSequence> adapterEvent = ArrayAdapter.createFromResource(this.getContext(), R.array.type_event_array, android.R.layout.simple_spinner_item);
        adapterEvent.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        typeEvent.setAdapter(adapterEvent);
        typeEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = typeEvent.getSelectedItem().toString();
                Log.w(TAG, selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> adapterGenre = ArrayAdapter.createFromResource(this.getContext(), R.array.type_genre_array, android.R.layout.simple_spinner_item);
        adapterGenre.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        typeGenre.setAdapter(adapterGenre);
        typeEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = typeEvent.getSelectedItem().toString();
                Log.w(TAG, selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

}
