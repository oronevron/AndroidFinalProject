package com.example.oron.androidfinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oron.androidfinalproject.Model.Model;
import com.example.oron.androidfinalproject.Model.Trip;

import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TripsListFragment extends Fragment {

//    List<Trip> tripsList;
    List<Trip> tripsList = new LinkedList<>();
    ListView list;
    TripsAdapter adapter;

    public TripsListFragment() {
        // Required empty public constructor
    }

    public void setTripsList(List<Trip> trips) {
        tripsList = trips;
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

        View view = inflater.inflate(R.layout.fragment_trips_list, container, false);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.tripsListProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        // Get all trips
//        tripsList = Model.getInstance().getAllTrips();
        Model.getInstance().getAllTripsAsynch(new Model.GetTripsListener() {
            @Override
            public void onResult(List<Trip> trips, List<Trip> tripsToDelete) {
                progressBar.setVisibility(View.GONE);

                tripsList = trips;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {

            }
        });

        // Connect the adapter to the list in order to show the data
        list = (ListView) view.findViewById(R.id.tripsListListView);
        adapter = new TripsAdapter();
        list.setAdapter(adapter);

        // Handle click on a row in the list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TripDetailsActivity.class);
//                intent.putExtra("tripIndex", i);
                intent.putExtra("tripIndex", (((Trip)adapterView.getItemAtPosition(i)).getId()));
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
        public int getViewTypeCount() {

            if (getCount() != 0)
                return getCount();

            return 1;
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = getActivity().getLayoutInflater().inflate(R.layout.fragment_trip_list_row, null);
            }

            final Trip trip = tripsList.get(i);
            final ImageView image = (ImageView) view.findViewById(R.id.tripRowImageView);
            image.setVisibility(View.GONE);
            TextView nameTv = (TextView) view.findViewById(R.id.tripRowName);
            nameTv.setText(trip.getName());

            if (trip.getImageName() != null) {
                final ProgressBar progress = (ProgressBar) view.findViewById(R.id.tripRowImageProgressBar);
                progress.setVisibility(View.VISIBLE);
                Model.getInstance().loadImage(trip.getImageName(), new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap imagebtmp) {
                        image.setImageBitmap(imagebtmp);
                        image.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail() {
                        image.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    }
                });
            } else {
                image.setImageResource(R.drawable.trip);
                image.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }
}
