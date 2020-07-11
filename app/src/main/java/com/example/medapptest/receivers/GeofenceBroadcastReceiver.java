package com.example.medapptest.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.example.medapptest.MainActivity;
import com.example.medapptest.R;
import com.example.medapptest.common.Constants;
import com.example.medapptest.model.NotificationActionData;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import uiutils.NotificationManager;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = null;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            message= getErrorString(geofencingEvent.getErrorCode());
            sendNotification(message, context);
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Location location = geofencingEvent.getTriggeringLocation();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            message ="Lat: " + latitude +", Lon: " + longitude +
                    ", Accuracy: " + location.getAccuracy();
            if(latitude == Constants.LAT &&
            longitude == Constants.LON) {
                message = "Welcome @ Medapp";
            }
        }
        if(message != null)
            sendNotification(message, context);
    }


    // Handle errors
    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    private void sendNotification(String msg, Context context ) {
        //send Notification
        String title = "You entered a geofence " ;
        uiutils.NotificationManager notificationManager = new
                NotificationManager(title, msg, context);
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
