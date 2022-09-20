package com.bd.durbin.polilinetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.bd.durbin.polilinetest.interfaceall.ServiceCallbacks;
import com.bd.durbin.polilinetest.service.LocationUpdatesService;
import com.bd.durbin.polilinetest.service.MyService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import static com.bd.durbin.polilinetest.EditMarkerActivity.BitmapFromVector;

public class LocationTracker extends AppCompatActivity implements OnMapReadyCallback, ServiceCallbacks {

    public FusedLocationProviderClient mFusedLocationClient;
    public int PERMISSION_ID = 44;

    // Initializing other items
    // from layout file
    GoogleMap map;
    public Location currentLocation;

    private MyService myService;
    private boolean bound = false;

    LatLng from; //= new LatLng(location.getLatitude(), location.getLongitude());

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startService(new Intent(this, LocationUpdatesService.class));

        from = new LatLng(23.869005, 90.378252);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), ApiKey.MAPS_API_KEY);
        placesClient = Places.createClient(this);


        getLastLocation();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        // method to get the location
    }

    //connect with service class
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(LocationTracker.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // bind to Service
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from service
        if (bound) {
            myService.setCallbacks(null); // unregister
            unbindService(serviceConnection);
            bound = false;
        }
    }


    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                requestNewLocationData();
                Places.initialize(getApplicationContext(), ApiKey.MAPS_API_KEY);
                placesClient = Places.createClient(this);
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                // getting last
                // location from
                // FusedLocationClient
                // object

                Task<Location> task = mFusedLocationClient.getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            locationChange(latLng);
                            Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                            Log.e("location", "" + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            currentLocation = locationResult.getLastLocation();
            Log.e("loc", currentLocation.getLatitude() + "");

        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }


    public void locationChange(LatLng latLng) {
        map.clear();
        map.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.mapicon)).title("here me"))
                .showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        this.map = googleMap;
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(from)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title("here me"))
                .showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from, 9));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(from));
    }

    @Override
    public void getLocation() {
        getLastLocation();
    }

}



