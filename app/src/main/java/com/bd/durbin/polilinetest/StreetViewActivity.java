package com.bd.durbin.polilinetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

public class StreetViewActivity extends AppCompatActivity implements OnStreetViewPanoramaReadyCallback {

    private static final Integer PANORAMA_CAMERA_DURATION = 1000;
    public static final String TAG = StreetViewActivity.class.getSimpleName();
    private static final String STREET_VIEW_BUNDLE = "StreetViewBundle";

    private StreetViewPanorama streetViewPanorama;
    private StreetViewPanoramaFragment streetViewPanoramaFragment;

    private StreetViewPanorama.OnStreetViewPanoramaChangeListener streetViewPanoramaChangeListener = streetViewPanoramaLocation -> Log.e(TAG, "Street View Panorama Change Listener");

    private StreetViewPanorama.OnStreetViewPanoramaClickListener streetViewPanoramaClickListener = (orientation -> {
        Point point = streetViewPanorama.orientationToPoint(orientation);
        if (point != null) {
            streetViewPanorama.animateTo(
                    new StreetViewPanoramaCamera.Builder()
                            .orientation(orientation)
                            .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                            .build(), PANORAMA_CAMERA_DURATION);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.streetViewMap);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        Bundle streetViewBundle = null;
        if (savedInstanceState != null)
            streetViewBundle = savedInstanceState.getBundle(STREET_VIEW_BUNDLE);
        streetViewPanoramaFragment.onCreate(streetViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        streetViewPanoramaFragment.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Bundle mStreetViewBundle = outState.getBundle(STREET_VIEW_BUNDLE);
        if (mStreetViewBundle == null) {
            mStreetViewBundle = new Bundle();
            outState.putBundle(STREET_VIEW_BUNDLE, mStreetViewBundle);
        }
        streetViewPanoramaFragment.onSaveInstanceState(mStreetViewBundle);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        this.streetViewPanorama = streetViewPanorama;
        this.streetViewPanorama.setPosition(new LatLng(23.869005, 90.378252));
        this.streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaChangeListener);
        this.streetViewPanorama.setOnStreetViewPanoramaClickListener(streetViewPanoramaClickListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        streetViewPanoramaFragment.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (streetViewPanoramaFragment != null)
            streetViewPanoramaFragment.onDestroy();
        streetViewPanoramaChangeListener = null;
        streetViewPanoramaClickListener = null;
        streetViewPanorama = null;
    }
}