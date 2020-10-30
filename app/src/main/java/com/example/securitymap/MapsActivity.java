package com.example.securitymap;



import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.InputStream;
import java.util.ArrayList;


public class MapsActivity<UOTTAWA> extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener//, ActivityCompat.OnRequestPermissionsResultCallback
{
    private GoogleMap mMap;

    //45.425490, -75.689445, 45.418436, -75.675062
    private LatLngBounds UOTTAWA = new LatLngBounds(new LatLng(45.418436, -75.689445), new LatLng(45.425490, -75.675062));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private Marker cbyMarker;
    private Marker steMarker;
    private Marker lprMarker;
    private Marker hsMarker;
    private Marker isMarker;
    private Marker cpMarker;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mMap.MAP_TYPE_HYBRID);

        // 45.425490, -75.689445, 45.418436, -75.675062
        LatLng cby = new LatLng(45.419754, -75.679601);
        LatLng ste = new LatLng(45.419308, -75.678701);
        LatLng lpr = new LatLng(45.421250, -75.680353);
        LatLng hs = new LatLng(45.421755, -75.679601);
        LatLng is = new LatLng(45.424537, -75.686460);
        LatLng cp = new LatLng(45.421764, -75.680541);

        cbyMarker = mMap.addMarker(new MarkerOptions().position(cby).title("Colonel By Hall (CBY)"));
        steMarker = mMap.addMarker(new MarkerOptions().position(ste).title("SITE (STE)"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(cby));
        lprMarker = mMap.addMarker(new MarkerOptions().position(lpr).title("Protection Services (LPR)"));
        hsMarker = mMap.addMarker(new MarkerOptions().position(hs).title("Health Services"));
        isMarker = mMap.addMarker(new MarkerOptions().position(is).title("Info Service"));
        cpMarker = mMap.addMarker(new MarkerOptions().position(cp).title("Campus Pharmacy"));


        mMap.setLatLngBoundsForCameraTarget(UOTTAWA);
        mMap.setMinZoomPreference(15);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.421963, -75.682235) , 16) );
        // Instantiates a new Polyline object and adds points to define a rectangle
        /*PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(45.42, -75.68))
                .add(new LatLng(45.4201, -75.68))  // North of the previous point, but at the same longitude
                .add(new LatLng(45.4201, -75.681))  // Same latitude, and 30km to the west
                .add(new LatLng(45.42, -75.681))  // Same longitude, and 16km to the south
                .add(new LatLng(45.4202, -75.68)); // Closes the polyline.

// Get back the mutable Polyline
        Polyline polyline = mMap.addPolyline(polylineOptions);
        */
        googleMap.setOnMarkerClickListener(this);
    }

        @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(cbyMarker)) {
            /*InputStream inputStream = getResources().openRawResource(R.raw.nodesdata);
            CSVFile csvFile = new CSVFile();
            csvFile.inputStream = inputStream;
            ArrayList<Node> nodesList = new ArrayList<Node>();
            nodesList = csvFile.read();

            ArrayList<Integer> shortest = new ArrayList<Integer>();
            Dijkstra calculator = new Dijkstra();
            shortest = calculator.calculatePath(nodesList, (9), (28));
            Log.d("tag1", "\nPath:\n");
            for(int node: shortest){
                Log.d("tag1", String.valueOf((int)(node+1))+", ");
            }*/
            Log.d("wtf", "yo");
            startActivity(new Intent(this, cby.class));
        }
        if(marker.equals(steMarker)) {
            startActivity(new Intent(this, ste.class));
        }
        return false;

    }
}