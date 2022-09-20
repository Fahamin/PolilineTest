package com.bd.durbin.polilinetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.bd.durbin.polilinetest.interfaceall.ServiceCallbacks;
import com.bd.durbin.polilinetest.model_jsonData.Outlet;
import com.bd.durbin.polilinetest.service.MyService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static com.bd.durbin.polilinetest.EditMarkerActivity.BitmapFromVector;
import static com.bd.durbin.polilinetest.PolylineActivity.getAssetJsonData;

public class MapChangeWithLine extends AppCompatActivity  implements OnMapReadyCallback, ServiceCallbacks {

    public FusedLocationProviderClient mFusedLocationClient;
    public int PERMISSION_ID = 44;

    // Initializing other items
    // from layout file
    GoogleMap map;
    public Location currentLocation;

    private MyService myService;
    private boolean bound = false;
    Outlet modelObject;

    LatLng from; //= new LatLng(location.getLatitude(), location.getLongitude());
    public static int i = 1;

    private PlacesClient placesClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startService(new Intent(this, MyService.class));

        from = new LatLng(23.869005, 90.378252);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), ApiKey.MAPS_API_KEY);
        placesClient = Places.createClient(this);


       // getLastLocation();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        //  getAssetJsonData(this);
        String data = getAssetJsonData(getApplicationContext());
        Type type = new TypeToken<Outlet>() {
        }.getType();
        modelObject = new Gson().fromJson(data, type);
    //    Log.e("obj", modelObject.getDvsmLocation().get(1).getLat());


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
            myService.setCallbacks(MapChangeWithLine.this); // register
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

        i++;

        Double lat = Double.valueOf(modelObject.getDvsmLocation().get(i).getLat());
        Double lon = Double.valueOf(modelObject.getDvsmLocation().get(i).getLng());

        LatLng latLng = new LatLng(lat,lon);

       locationChange(latLng);

    }

}

