package com.eventhunt;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eventhunt.entity.Event;

/**
 * Created by Shiplayer on 01.06.18.
 */

public class InfoEventFragment extends Fragment {
    private static final String TAG = InfoEventFragment.class.getSimpleName();
    private Event event;
    private TextView titleView;
    private TextView typeView;
    private TextView startView;
    private TextView costView;
    private Button moreInfoBtn;
    private Button routeBtn;

    public static InfoEventFragment getInstance(Event event){
        InfoEventFragment fragment = new InfoEventFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AddEventActivity.EVENT_KEY, event);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_find, container, false);
        if(getArguments() != null) {
            event = getArguments().getParcelable(AddEventActivity.EVENT_KEY);

        }
        titleView = view.findViewById(R.id.frag_title);

        titleView.setText(event.getTitle());

        typeView = view.findViewById(R.id.frag_type_event);

        if(event.getType() != null)
            typeView.setText(event.getType());
        else
            typeView.setText("тип/жар");

        startView = view.findViewById(R.id.frag_start_event);
        String startEventConst = getResources().getString(R.string.info_box_time);
        startView.setText(startEventConst + " " + Event.convertDateToString(event.getStartEvent().getTime()));
        costView = view.findViewById(R.id.frag_cost);
        String costEventConst = getResources().getString(R.string.info_box_cost);
        costView.setText(costEventConst + " " + String.valueOf(event.getCost()) + " руб.");
        moreInfoBtn = view.findViewById(R.id.frag_btn_more_info);
        moreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "button moreInfo is clicked");
            }
        });
        routeBtn = view.findViewById(R.id.frag_route);
        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "button route is clicked");
            }
        });
        return view;
    }
}
