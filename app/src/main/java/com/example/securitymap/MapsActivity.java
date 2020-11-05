package com.example.securitymap;



import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
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
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity<UOTTAWA> extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, TaskLoadedCallback//, ActivityCompat.OnRequestPermissionsResultCallback
{
    private GoogleMap mMap;

    static int width;
    static int height;

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
    private Marker cbyEntrance;
    private Marker hmlEntrance;

    private Polyline outsidePath;


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
        LatLng cbyEnt = new LatLng(45.419968, -75.679397);
        LatLng hmlEnt = new LatLng(45.423655, -75.685811);

        cbyEntrance = mMap.addMarker(new MarkerOptions().position(cbyEnt).title("start point"));
        hmlEntrance = mMap.addMarker(new MarkerOptions().position(hmlEnt).title("end point"));

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
            InputStream inputStream = getResources().openRawResource(R.raw.nodesdata);
            CSVFile csvFile = new CSVFile();
            csvFile.inputStream = inputStream;
            ArrayList<Node> nodesList = new ArrayList<Node>();
            nodesList = csvFile.read();

            ArrayList<Integer> shortest = new ArrayList<Integer>();
            Dijkstra calculator = new Dijkstra();
            shortest = calculator.calculatePath(nodesList, (9), (28));
            Log.d("tag1", "\nPath:\n");
            for(int node: shortest){
                Log.d("tag1", (node+1)+", ");
            }
            startActivity(new Intent(this, cby.class));
        }
        if(marker.equals(steMarker)) {
            startActivity(new Intent(this, ste.class));
        }

        if(marker.equals(cbyEntrance)){
            String url = getUrl();
            Log.d("Steps","about to FetchURL");
            new FetchURL(MapsActivity.this).execute(url,"walking");

        }

        return false;

    }

    private String getUrl(){

        Log.d("Steps","in getUrl");

        // IN THE FUTURE SHOULDN'T HAVE THIS STUFF HARDCODED, WILL USE MARKERS AND VALUES

        String urlStr = "http://maps.googleapis.com/maps/api/directions/json?";

        urlStr = urlStr+"origin="+"45.419968"+","+"-75.679397" //set the origin point with coords
                +"&destination="+"45.423655"+","+"-75.685811" //set the destination point with coords
                +"&key="+(R.string.google_maps_key) //specify the API key for our app (NOTE: normally this would NEVER be in the app, but rather on a server, in order to protect the key)
                +"&mode=walking"; //specify the transportation mode, in our case it will always be walking

        Log.d("Directions", "url is (without locomotion mode)"+urlStr);

        return urlStr;

    }

    @Override
    public void onTaskDone(Object... values) {
        if (outsidePath!=null){
            outsidePath.remove();
        }
        outsidePath = mMap.addPolyline((PolylineOptions) values[0]);
        Log.d("Steps","end of onTaskDone");
    }
}