package com.bd.durbin.polilinetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bd.durbin.polilinetest.adpter.InfoWindowAdapter;
import com.bd.durbin.polilinetest.model_jsonData.Outlet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class EditMarkerActivity  extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {

    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);


    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 6;

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    Outlet modelObject;
    LatLng from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);


        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapFragment.getMapAsync( googleMap -> {
            InfoWindowAdapter markerWindowView = new InfoWindowAdapter(this);
            googleMap.setInfoWindowAdapter(markerWindowView);

        });

        //  getAssetJsonData(this);
        String data = getAssetJsonData(getApplicationContext());
        Type type = new TypeToken<Outlet>() {
        }.getType();
        modelObject = new Gson().fromJson(data, type);
        Log.e("obj", modelObject.getDvsmLocation().get(1).getLat());

        Double lat = Double.valueOf(modelObject.getDvsmLocation().get(0).getLat());
        Double lon = Double.valueOf(modelObject.getDvsmLocation().get(0).getLng());

        from = new LatLng(lat,lon);

    }


    public static String getAssetJsonData(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("maplocation.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        Log.e("data", json);
        return json;

    }


    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.addMarker(new MarkerOptions().position(from)
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.g_flag)));

        Double l = Double.valueOf(modelObject.getDvsmLocation().get(modelObject.getDvsmLocation().size()-1).getLat());
        Double lo = Double.valueOf(modelObject.getDvsmLocation().get(modelObject.getDvsmLocation().size()-1).getLng());

        googleMap.addMarker(new MarkerOptions().position(new LatLng(l,lo))
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.r_flag)));


        for (int i = 1; i < modelObject.getDvsmLocation().size(); i++) {
            Double lat = Double.valueOf(modelObject.getDvsmLocation().get(i).getLat());
            Double lon = Double.valueOf(modelObject.getDvsmLocation().get(i).getLng());

            to = new LatLng(lat, lon);
            lineOutlets(googleMap, from, to);
            from = to;

        }

        for (int i = 0; i <modelObject.getOutletLoaction().getFeatures().size() ; i++) {
            if(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getVisitType() == 1)
            {
                Double lat = Double.valueOf(modelObject.getOutletLoaction().getFeatures().get(i).getGeometry().getCoordinates().get(1));
                Double lon = Double.valueOf(modelObject.getOutletLoaction().getFeatures().get(i).getGeometry().getCoordinates().get(0));

                to = new LatLng(lat, lon);
                googleMap.addMarker(new MarkerOptions().position(to)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .snippet(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getOutletCode().toString())
                        .title(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getOutletName()))
                        .showInfoWindow();

            }

            if(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getVisitType() == 0)
            {
                Double lat = Double.valueOf(modelObject.getOutletLoaction().getFeatures().get(i).getGeometry().getCoordinates().get(1));
                Double lon = Double.valueOf(modelObject.getOutletLoaction().getFeatures().get(i).getGeometry().getCoordinates().get(0));

                to = new LatLng(lat, lon);
                googleMap.addMarker(new MarkerOptions().position(to)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .snippet(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getOutletCode().toString())
                        .title(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getOutletName()))
                        .showInfoWindow();
            }
            if(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getVisitType() == 2)
            {
                Double lat = Double.valueOf(modelObject.getOutletLoaction().getFeatures().get(i).getGeometry().getCoordinates().get(1));
                Double lon = Double.valueOf(modelObject.getOutletLoaction().getFeatures().get(i).getGeometry().getCoordinates().get(0));

                to = new LatLng(lat, lon);
                googleMap.addMarker(new MarkerOptions().position(to)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .snippet(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getOutletCode().toString())
                        .title(modelObject.getOutletLoaction().getFeatures().get(i).getProperties().getOutletName()))
                        .showInfoWindow();
            }
        }

      /*  // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // Style the polyline.
        stylePolyline(polyline1);*/

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(from, 14));

       /* for (int i = 0; i < 20; i++) {
            i++;
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(-23.684 + i, 133.903 + i))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title("Marker in Sydney"));
        }*/

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(from));
        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
    }

    private void lineOutlets(GoogleMap googleMap, LatLng from, LatLng to) {
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(from, to));
        polyline1.setTag("A");
        stylePolyline(polyline1);
    }


    public static BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Styles the polyline, based on type.
     *
     * @param polyline The polyline object that needs styling.
     */

    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ar), 20));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

    /**
     * Listens for clicks on a polyline.
     *
     * @param polyline The polyline object that the user has clicked.
     */

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(GAP))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Listens for clicks on a polygon.
     *
     * @param polygon The polygon object that the user has clicked.
     */

    @Override
    public void onPolygonClick(Polygon polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);

        Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }
}

