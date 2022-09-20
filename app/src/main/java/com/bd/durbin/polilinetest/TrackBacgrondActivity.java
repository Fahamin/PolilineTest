
package com.bd.durbin.polilinetest;

/*This is the initial activity responsible for the login.
 *Few superclass methods have been overridden
 * All the links have been setup in the LinkUtil class
 * Butter Knife has been implemented in all the classes*/

import static android.service.controls.ControlsProviderService.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bd.durbin.polilinetest.service.ForegroundServiceUtils;
import com.bd.durbin.polilinetest.service.LocationMonitoringService;
import com.bd.durbin.polilinetest.service.LocationUpdatesService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;


public class TrackBacgrondActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    final static int REQUEST_LOCATION = 199;
    private static final int MY_PERMISSIONS_REQUEST_PHONE_STATE = 1;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 2;

    int apkVersion = 1;
    boolean gpsisON = false;
    

    LinearLayout linearLayoutProgressPanel;
 
    String _vcLatitude = "0", _vcLongitude = "0", imei = "0";
  

    TelephonyManager telephonyManager;

    // GPSTracker gps;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient googleApiClient;

    public static final int REQUEST_READ_CONTACTS = 79;

    

    AlertDialog.Builder Alertloading;
    AlertDialog Alertloader;

   
    public void login() {
        Log.i("checkPermissions", "checkPermissions" + checkPermissions());
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            mService.requestLocationUpdates();
        }
        getImei();
        checkLocationStatus();
        if (gpsisON && _vcLatitude != null) {

        } else {
            getLocation();
           
        }
    }



    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;


    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(ForegroundServiceUtils.requestingLocationUpdates(this));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(new Intent(this, LocationUpdatesService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(TrackBacgrondActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(TrackBacgrondActivity.this, ForegroundServiceUtils.getLocationText(location), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(ForegroundServiceUtils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(ForegroundServiceUtils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {

        } else {

        }
    }

    /* Location Background service END*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        setContentView(R.layout.activity_track_bacgrond);
        ButterKnife.bind(this);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        Alertloading = new AlertDialog.Builder(this);
        checkLocationStatus();
        getImei();
        getLocation();
        //  GPSTrackerLocationUpdates();

        if (_vcLatitude == "0") {
            gpsisON = false;

        }

        ShowRememberUser();

findViewById(R.id.starServiceBtn).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        login();
    }
});

    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }



    public void showPermission(String permissionFor, final int status) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Permission request")
                .setMessage("A permission is required for your " + permissionFor)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        switch (status) {
                            case MY_PERMISSIONS_REQUEST_PHONE_STATE:
                                requestPhoneState();
                                break;
                            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                                requestLocation();
                                break;

                        }
                    }
                })
                .show();
    }

    /**
     * Method gets the Imei
     */
    public void getImei() {
        telephonyManager = (TelephonyManager) this.getSystemService(Context.
                TELEPHONY_SERVICE);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermission("Imei", MY_PERMISSIONS_REQUEST_PHONE_STATE);
            } else {
                // No explanation needed; request the permission
                requestPhoneState();
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            try {
                assert telephonyManager != null;
                @SuppressLint("HardwareIds")
                String deviceId = telephonyManager.getDeviceId();
                imei = deviceId;
                if (imei == null) {
                    imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                } else if (imei.length() < 4) {
                    imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            } catch (Exception e) {

                imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            }


        }


    }

    /**
     * Method to get the SKU list for orders from the server
     * Check log for the data that has been sent.
     * If the results are successfully received, the entire json is saved in a database.
     * Check the Database for the structure from the device file explorer
     * The result calls getSubroute list sequentially.
     */


    /**
     * Get the value of the lat and long
     */
    public void getLocation() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermission("Location", MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            } else {
                // No explanation needed; request the permission
                requestLocation();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {


            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object\

                                _vcLatitude = location.getLatitude() + "";
                                _vcLongitude = location.getLongitude() + "";
                                gpsisON = true;
                                Log.e("location", "" + _vcLatitude + "," + _vcLongitude);
                                if (_vcLatitude.length() < 3) {
                                    getLocation();
                                    gpsisON = false;
                                }
                            } else {
                                getLocation();
                                //  GPSTrackerLocationUpdates();
                            }
                        }
                    });

        }


    }

    /**
     * Check the gps location
     * gpsisON is a variable that helps to check whether the location has been recorded correctly
     * it controls the parent functionality
     */
    private void checkLocationStatus() {

        final LocationManager manager = (LocationManager) TrackBacgrondActivity.this.getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(TrackBacgrondActivity.this)) {
            gpsisON = true;
            getLocation();
        }
        // Todo Location Already on  ... end

        if (!hasGPSDevice(TrackBacgrondActivity.this)) {
            Toast.makeText(TrackBacgrondActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(TrackBacgrondActivity.this)) {

            gpsisON = false;
            Toast.makeText(TrackBacgrondActivity.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            googleApiClient = null;
            enableLoc();
        } else {
            getLocation();

            gpsisON = true;

        }
    }

    /**
     * Checks whether the device supports GPS
     * prevents error
     */
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    /**
     * IMPORTANT
     * This enables location like the latest google maps if the location from the device is turned off.
     * Status logs are disabled please check them if debugging is needed.
     * Check Fused location documentation for further understanding.
     */
    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(TrackBacgrondActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            if (ActivityCompat.checkSelfPermission(TrackBacgrondActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackBacgrondActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            Location mLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


                            if (mLocation != null) {
                                Log.e("gpss", "onSuccess new inner: reading the location");
                                _vcLatitude = mLocation.getLatitude() + "";
                                _vcLongitude = mLocation.getLongitude() + "";
                                gpsisON = true;

                            } else {
                                Toast.makeText(TrackBacgrondActivity.this, "Location not Detected", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // LocationCallback mLocationCallback = new LocationCallback();

            //  LocationServices.getFusedLocationProviderClient(TrackBacgrondActivity.this).requestLocationUpdates(locationRequest, mLocationCallback, null);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(TrackBacgrondActivity.this, REQUEST_LOCATION);

                                Log.e("GPS test", "onResult: gps test");
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    /**
     * Returning params are loaded here after relevant promt for location.
     * This has been implemented only for location request.
     * If there are additional request add to the cases.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Toast.makeText(this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        getLocation();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        gpsisON = false;
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    /**
     * Required for permissions
     */
    private void requestPhoneState() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                MY_PERMISSIONS_REQUEST_PHONE_STATE);
    }

    /**
     * Required for permissions
     */
    private void requestLocation() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
    }


    /**
     * Results of the permissions promted is returned here.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getImei();
                    getLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object\
                                        _vcLatitude = location.getLatitude() + "";
                                        _vcLongitude = location.getLongitude() + "";
                                    }
                                }
                            });
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }


            if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
                if (grantResults.length <= 0) {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    mService.requestLocationUpdates();
                } else {
                    // Permission denied.
                    setButtonsState(false);

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }




    public void SetuseridToLocationService(String userId) {

        SharedPreferences mPrefs = getSharedPreferences("IDvalue", 0);

        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString("userId", userId);


        editor.commit();
        initialize();
    }

    public void RememberUser(String username, String password) {

        SharedPreferences mPrefs = getSharedPreferences("Durbin", 0);

        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString("username", username);
        editor.putString("password", password);

        editor.commit();
    }

    public void RememberUserid(String userId, String role) {

        SharedPreferences mPrefs = getSharedPreferences("DurbinUser", 0);
        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString("userId", userId);
        editor.putString("role", role);
        editor.commit();
    }

    public void ShowRememberUser() {

        SharedPreferences mPrefs = getSharedPreferences("Durbin", 0);
        String user = mPrefs.getString("username", "");
        String pass = mPrefs.getString("password", "");

    }

    private void initialize() {
        try {
            startService(new Intent(this, LocationMonitoringService.class));
            startService(new Intent(this, LocationUpdatesService.class));
        } catch (Exception e) {

        }
    }


    public void SetEmpidToMUserTrackerService(String userId) {

        SharedPreferences mPrefs = getSharedPreferences("IDvalue", 0);

        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString("userId", userId);


        editor.commit();
        // Muserinitialize();
    }



    /*private void GPSTrackerLocationUpdates() {
        try {
            // create class object
            gps = new GPSTracker(TrackBacgrondActivity.this);

            // check if GPS enabled
            if (gps.canGetLocation()) {
                _vcLatitude = gps.getLatitude() + "";
                _vcLongitude = gps.getLongitude() + "";

            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
        } catch (Exception e) {

        }

    }*/


}