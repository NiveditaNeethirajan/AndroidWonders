package com.example.medapptest;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.medapptest.common.Constants;
import com.example.medapptest.model.GeoFenceData;
import com.example.medapptest.model.NotificationActionData;
import com.example.medapptest.receivers.GeofenceBroadcastReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import uiutils.GeoFencingListAdapter;
import uiutils.NotificationManager;

public class GeoFencing extends Fragment implements LocationListener {
    private GeofencingClient geofencingClient;
    private List<GeoFenceData> geofenceDataList;
    private List<Geofence> geofenceList = new ArrayList<Geofence>();
    private PendingIntent geofencePendingIntent;
    private RecyclerView recyclerView;
    private GeoFencingListAdapter mAdapter;
    private Activity activity;
    private LocationManager locationManager;
    private Context context;
    private int id;
    private boolean setGeoFenceOptionSelected = false;

    public GeoFencing() {
        // Required empty public constructor
    }

    public GeoFencing(Context context_) {
        context = context_;
        activity = getActivity();
        if (activity == null) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setGeoFence:
                //Get current location and set GeoFencing
                setGeoFenceOptionSelected = true;
                getCurrentLocation();
                return(true);
             case R.id.getGeoFence:
                 setGeoFenceOptionSelected = false;
                 getCurrentLocation();
                 return(true);

        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view =  inflater.inflate(R.layout.fragment_geo_fencing, container, false);;
        recyclerView = view.findViewById(R.id.recycleListView);
        geofenceDataList = new ArrayList<GeoFenceData>();
        mAdapter = new GeoFencingListAdapter(geofenceDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //Initialise Location Manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //Add MedApp office as a geofence
        geofencingClient = LocationServices.getGeofencingClient(context);
        createGeofenceForPlace(Constants.GEOFENCE_KEY,
                Constants.LAT, Constants.LON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        Constants.BACKGROUND_LOCATION_PERMISSIONCODE);
            }
        }
        else {
            addGeoFence();
        }
        // Inflate the layout for this fragment
        return view;
    }

    //region GeoFence
    private void createGeofenceForPlace(String id, double lat, double lon)
    {
        geofenceList.add(new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(lat, lon,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER )
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void addGeoFence() {
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Geofence added for MedApp Office",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add geofence for MedApp Office",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //endregion

    //region Location  & Location Listener
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    Constants.LOCATION_PERMISSIONCODE);
            return ;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                Constants.LOCATION_UPDATE_MINTIME,
                Constants.LOCATION_UPDATE_MINDISTANCE,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        String name = "Location " + id ++;
        GeoFenceData geoData = new GeoFenceData(name, location.getLatitude(),
                location.getLongitude(), location.getAccuracy());
        locationManager.removeUpdates(this);
        if(setGeoFenceOptionSelected) {
            geofenceDataList.add(geoData);
            mAdapter.notifyDataSetChanged();
            createGeofenceForPlace(name, location.getLatitude(), location.getLongitude());
            addGeoFence();
        } else {
            //send Notification
            String title = "You are here - " + name;
            String content = "Lat: " + location.getLatitude() +
                    ", Lon: " + location.getLongitude() +
                    ", Accuracy: " + location.getAccuracy();
            NotificationManager notificationManager = new
                    NotificationManager(title, content, context);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setClass(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(Constants.NOTIFICATION_SHOW_SHOUT);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, 0);
            NotificationActionData actionData = new NotificationActionData(
                    "Start", pendingIntent,
                    R.drawable.ic_launcher_foreground);
            notificationManager.addAction(actionData);

            Intent dismissIntent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constants.DISMISS_APP, true);
            PendingIntent pendingDismissIntent =
                    PendingIntent.getActivity(context, 0, dismissIntent, 0);
            NotificationActionData actionData1 = new NotificationActionData(
                    "Archive", pendingDismissIntent,
                    R.drawable.ic_launcher_foreground);
            notificationManager.addAction(actionData1);
            notificationManager.sendNotification();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }
    //endregion

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == Constants.LOCATION_PERMISSIONCODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getCurrentLocation();
            }
        }
        if (requestCode == Constants.BACKGROUND_LOCATION_PERMISSIONCODE) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGeoFence();
            }
        }
    }
}
