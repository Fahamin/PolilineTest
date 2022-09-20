package com.bd.durbin.polilinetest.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();


    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mLocationClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mLocationRequest.setInterval(1000*60*5 );
            mLocationRequest.setFastestInterval(1000*60*5);


            int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
            //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


            mLocationRequest.setPriority(priority);
            mLocationClient.connect();

        } catch (Exception e) {

        }
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return Service.START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "== Error On onConnected() Permission not granted");
                //Permission not granted by user so cancel the further execution.

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

            Log.d(TAG, "Connected to Google API");
        } catch (Exception e) {
            Log.d(TAG, "Connected to Google API");
        }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Toast.makeText(getApplicationContext(), "Service Task destroyed", Toast.LENGTH_LONG).show();


            Intent myIntent = new Intent(getApplicationContext(), LocationMonitoringService.class);

            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);

            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(System.currentTimeMillis());

            calendar.add(Calendar.SECOND, 10);

            alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Toast.makeText(getApplicationContext(), "Start Alarm", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        try {

            Intent myIntent = new Intent(getApplicationContext(), LocationMonitoringService.class);

            PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);

            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(System.currentTimeMillis());

            calendar.add(Calendar.SECOND, 10);

            alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Toast.makeText(getApplicationContext(), "Start Alarm", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {

        }

    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {

        try {
            if (location != null) {


                //Send result to activities
//            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                // Log.e("onLocationChanged", "onLocationChanged: Lat=" + location.getLatitude() + "Lat:" + location.getLatitude());

                BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int batLevel = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                }
                sendMessageToUI(location.getLatitude(), location.getLongitude());

            }
        } catch (Exception e) {

        }
    }



    /**
     * Gets the current date and time
     */
    public String getCurrentDateAndTime() {
        try {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return df.format(c);
        } catch (Exception e) {

        }
        return "";
    }

    private void sendMessageToUI(double lat, double lng) {
        try {


            Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            intent.setAction("action");
            sendBroadcast(intent);

        } catch (Exception e) {

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }



}