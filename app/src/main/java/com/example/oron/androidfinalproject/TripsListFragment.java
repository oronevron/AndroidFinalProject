package com.example.oron.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TripsListFragment extends Fragment {

    List<Trip> tripsList;
    ListView list;
    TripsAdapter adapter;

    public TripsListFragment() {
        // Required empty public constructor
    }

    public ListView getList() {
        return list;
    }

    public TripsAdapter getAdapter() {
        return adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get all trips
        tripsList = Model.getInstance().getAllTrips();

        View view = inflater.inflate(R.layout.fragment_trips_list, container, false);

        // Connect the adapter to the list in order to show the data
        list = (ListView) view.findViewById(R.id.tripsListListView);
        adapter = new TripsAdapter();
        list.setAdapter(adapter);

        // Handle click on a row in the list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TripDetailsActivity.class);
                intent.putExtra("tripIndex", i);
                getActivity().startActivityForResult(intent, 2);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    class TripsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tripsList.size();
        }

        @Override
        public Object getItem(int i) {
            return tripsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = getActivity().getLayoutInflater().inflate(R.layout.fragment_trip_list_row, null);

                // Handle click on the checkbox
//                final CheckBox checkedCb = (CheckBox) view.findViewById(R.id.tripRowCheckBox);
//                checkedCb.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Integer index = (Integer)checkedCb.getTag();
//                        Trip trip = tripsList.get(index);
//                        trip.setChecked(!trip.getChecked());
//                    }
//                });
            }

            Trip trip = tripsList.get(i);
            TextView idTv = (TextView) view.findViewById(R.id.tripRowId);
            idTv.setText(trip.getId());
            TextView nameTv = (TextView) view.findViewById(R.id.tripRowName);
            nameTv.setText(trip.getName());
//            CheckBox checkedCb = (CheckBox) view.findViewById(R.id.tripRowCheckBox);
//            checkedCb.setChecked(trip.getChecked());
//            checkedCb.setTag(new Integer(i));

            return view;
        }
    }

}
