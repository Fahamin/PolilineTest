
package com.bd.durbin.polilinetest.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bd.durbin.polilinetest.OflineLocationDB.OfflineLocationDatabaseHelper;
import com.bd.durbin.polilinetest.OflineLocationDB.model.OfflineLocationModel;
import com.bd.durbin.polilinetest.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class LocationUpdatesService extends Service {

    private static final String PACKAGE_NAME = "com.transcombd.salespromoter";

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME + ".started_from_notification";

    private final IBinder mBinder = new LocalBinder();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 60;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;

    private NotificationManager mNotificationManager;


    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */

    private LocationCallback mLocationCallback;

    private Handler mServiceHandler;

    /**
     * The current location.
     */
    private Location mLocation;

    public LocationUpdatesService() {
    }

    OfflineLocationDatabaseHelper databaseHelper;

    @Override
    public void onCreate() {
        databaseHelper = new OfflineLocationDatabaseHelper(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_NONE);
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);
        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");
        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && ForegroundServiceUtils.requestingLocationUpdates(this)) {
            Log.i(TAG, "Starting foreground service");
            startForeground(NOTIFICATION_ID, getNotification(mLocation));
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {

        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        ForegroundServiceUtils.setRequestingLocationUpdates(this, true);

        startService(new Intent(getApplicationContext(), LocationUpdatesService.class));

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
            Log.e("ss", "oklast");

        } catch (SecurityException unlikely) {
            ForegroundServiceUtils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */

    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            ForegroundServiceUtils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            ForegroundServiceUtils.setRequestingLocationUpdates(this, true);
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification(Location location) {


        try {
            final double[] _vcLatitude = {0};
            final double[] _vcLongitude = {0};
            _vcLatitude[0] = location.getLatitude();
            _vcLongitude[0] = location.getLongitude();
            BatteryManager bm = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            }
            int batLevel = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }

            try {
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationCallback mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            _vcLatitude[0] = location.getLatitude();
                            _vcLongitude[0] = location.getLongitude();
                        }
                    }
                };
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, null);

                sendMessageToServer(_vcLatitude[0], _vcLongitude[0], batLevel);
            } catch (Exception ignored) {
            }


            //sendMessageToServer(location.getLatitude(), location.getLongitude(), batLevel);
        } catch (Exception e) {
            Log.e("error", "" + e.getMessage());
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "now working",
                    NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            CharSequence text = ForegroundServiceUtils.getLocationText(mLocation);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.leftarrow))
                    .setContentText(text)
                    .setContentTitle("is now working")

                    .setOngoing(true)
                    .setVibrate(new long[]{0L})
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
                    .build();

            return notification;
        } else {


            Intent intent = new Intent(this, LocationUpdatesService.class);
            CharSequence text = ForegroundServiceUtils.getLocationText(mLocation);
            // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.leftarrow))
                    .setContentText(text)
                    .setContentTitle(" working")
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_LOW)
                    .setVibrate(new long[]{0L})
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis());
            // Set the Channel ID for Android O.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setChannelId(CHANNEL_ID); // Channel ID
            }
            return builder.build();
        }

    }

    private void getLastLocation() {

        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();
                        } else {
                            Log.w(TAG, "Failed to get location.");
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        //  Log.i(TAG, "New location: "+DateFormat.getDateTimeInstance().format(new Date())+" " + location.getLatitude()+","+location.getLongitude());
        mLocation = location;
        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification(mLocation));
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void sendMessageToServer(double lat, double lng, int battery) {
        Log.e("location", "" + lat);
        sendNotification("" + lat, "" + lng);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", lat);
            jsonObject.put("lng", lng);
            jsonObject.put("battery", battery);
            jsonObject.put("date", getCurrentDateAndTime());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("onLocationChanged", "Location userId" + e.getMessage());
        }
        try {
            OfflineLocationModel locationModel = new OfflineLocationModel(0, jsonObject.toString());
            databaseHelper.insertToOfflineData(locationModel);
            if (isNetworkConnected()) {
                List<OfflineLocationModel> allData = databaseHelper.getAllOflineLocationData();
                for (OfflineLocationModel model : allData) {
                    Log.e("ss", model.getJsondata().toString());
                }
                databaseHelper.deleteAllOfflineLocation();

            }
        } catch (Exception e) {
        }


    }

    private void sendNotification(String title, String details) {


        String channel_id = "userLocation";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, channel_id, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(title);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel_id);
        builder.setContentTitle(title);
        builder.setContentText(details);
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.mipmap.ic_launcher);


        Notification notification = builder.build();

        notificationManager.notify(new Random().nextInt(), notification);


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

}
