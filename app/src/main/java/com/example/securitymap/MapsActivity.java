package com.example.securitymap;


import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

public class MapsActivity<UOTTAWA> extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener//, ActivityCompat.OnRequestPermissionsResultCallback
{
    private GoogleMap mMap;
    public int width;
    public int height;
    public Hashtable<Integer, Node> nodesList;


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
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative);
        width = layout.getWidth();
        height = layout.getHeight();

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

        InputStream inputStream = getResources().openRawResource(R.raw.nodesdata);
        CSVFile csvFile = new CSVFile();
        csvFile.inputStream = inputStream;
        csvFile.read();
        nodesList = CSVFile.getNodes();

        Hashtable<Build, Building> buildings;
        Hashtable<Integer, Node> nodes;
        buildings = CSVFile.getBuildings();
        nodes = CSVFile.getNodes();
        ArrayList<Integer> path;

        /*Enumeration<Integer> keys = nodes.keys();
        int k;
        while(keys.hasMoreElements()) {
            k=keys.nextElement();
            Log.d("taggg", nodes.get(k).toStr());

        }*/

        Dijkstra.calculatePath(nodes, 5, 55);
        path = Dijkstra.path;
        Dijkstra.pathProgress = 0;

        Log.d("tag1", "\nPath:\n");
        for(int i=0; i<path.size(); i++){
            Log.d("tag1", String.valueOf(path.get(i)));
        }
        if(nodes.get(path.get(0)).building!=Build.OUT){
            Intent intent = new Intent(this, Indoor.class);
            intent.putExtra("type", "path");
            intent.putExtra("building", String.valueOf(nodes.get(path.get(0)).building));
            intent.putExtra("floor", nodes.get(path.get(0)).floor);
            startActivity(intent);
        } else {
            //draw Polyline
            //show next/back buttons
        }



        googleMap.setOnMarkerClickListener(this);
    }

        @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, Indoor.class);
        intent.putExtra("type", "browse");

        if(marker.equals(cbyMarker)) {
            intent.putExtra("building", "CBY");
            intent.putExtra("floor", 1);
        }

        if(marker.equals(steMarker)) {
            intent.putExtra("building", "STE");
            intent.putExtra("floor", 1);
        }


        startActivity(intent);
        return false;

    }

}