package com.example.securitymap;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MapsActivity<UOTTAWA> extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener//, ActivityCompat.OnRequestPermissionsResultCallback
{
    private GoogleMap mMap;

    private final LatLng defaultLocation = new LatLng(45.421963, -75.682235);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static Location lastKnownLocation;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ConstraintLayout constraint;
    private ImageButton backBtn;
    private TextView buildingName;
    private Button direction;
    private Button inside;
    private TextView cancel;
    private Button startMap;
    private Button startLocation;
    private TextView clickMessage;
    private Button setStart;
    private Button cancelStart;
    private ImageButton emergency;
    private ImageButton menuOptionsButton;

    private static final double EARTH_RADIUS = 6378100;
    private LatLng origin;


    private Intent intent;
    public int width;
    public int height;
    static int startNode;
    static int endNode;
    public Hashtable<Integer, Node> nodesList;
    public Hashtable<Build, Building> buildings;

    private LatLngBounds UOTTAWA = new LatLngBounds(new LatLng(45.418436, -75.689445), new LatLng(45.425490, -75.675062));

    //public int getClosestNode(double latitude, double longitude){

        //return node
    //}

    public double getX(double longitude){
        double x = EARTH_RADIUS*cos(origin.latitude);
        x = x*(longitude-origin.longitude)*PI/180;
        return x;
    }
    public double getY(double latitude){
        double y = EARTH_RADIUS*(latitude-origin.latitude)*PI/180;
        return y;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
        /* mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style)); */

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative);
        width = layout.getWidth();
        height = layout.getHeight();

        constraint = (ConstraintLayout) findViewById(R.id.constraint1);
        constraint.setVisibility(View.INVISIBLE);
        backBtn = (ImageButton) findViewById(R.id.imageView3);
        buildingName = (TextView) findViewById(R.id.textView5);
        direction = (Button) findViewById(R.id.button6);
        inside = (Button) findViewById(R.id.button5);
        cancel = (TextView)findViewById(R.id.textView14);
        startMap = (Button) findViewById(R.id.button12);
        startLocation = (Button) findViewById(R.id.button13);
        clickMessage = (TextView)findViewById(R.id.textView19);
        setStart = (Button)findViewById(R.id.button14);
        setStart.setVisibility(View.INVISIBLE);
        cancelStart = (Button)findViewById(R.id.button16);
        cancelStart.setVisibility(View.INVISIBLE);
        emergency = (ImageButton) findViewById(R.id.imageButton3);
        emergency.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:6135625499"));
                startActivity(callIntent);
            }

        });

        menuOptionsButton = (ImageButton) findViewById(R.id.imageButton4);
        final PopupMenu dropDownMenu = new PopupMenu(this, menuOptionsButton);

        final Menu menu = dropDownMenu.getMenu();

        menu.add(0, 0, 0, "Visibilité réduite");
        menu.add(0, 1, 0, "Chemin le plus chaud");
        menu.add(0, 2, 0, "Déplacement libre");

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        // item ID 0 was clicked
                        return true;
                    case 1:
                        // item ID 1 was clicked
                        return true;
                    case 2:
                        return true;
                }
                return false;
            }
        });

        menuOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });

        origin = new LatLng(45.419513, -75.678796);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.building_icon);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        LatLng cby = new LatLng(45.419754, -75.679601);
        LatLng ste = new LatLng(45.419308, -75.678701);
        LatLng lpr = new LatLng(45.421250, -75.680353);
        LatLng hs = new LatLng(45.421755, -75.679601);
        LatLng is = new LatLng(45.424537, -75.686460);
        LatLng cp = new LatLng(45.421764, -75.680541);

        cbyMarker = mMap.addMarker(new MarkerOptions().position(cby).title("Colonel By Hall (CBY)").icon(smallMarkerIcon));
        steMarker = mMap.addMarker(new MarkerOptions().position(ste).title("SITE (STE)"));
        lprMarker = mMap.addMarker(new MarkerOptions().position(lpr).title("Protection Services (LPR)"));
        hsMarker = mMap.addMarker(new MarkerOptions().position(hs).title("Health Services"));
        isMarker = mMap.addMarker(new MarkerOptions().position(is).title("Info Service"));
        cpMarker = mMap.addMarker(new MarkerOptions().position(cp).title("Campus Pharmacy"));


        //mMap.setLatLngBoundsForCameraTarget(UOTTAWA);
        //mMap.setMinZoomPreference(15);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
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
        buildings = CSVFile.getBuildings();

        intent = new Intent(this, Indoor.class);

        /*Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText start = (EditText) findViewById(R.id.editTextTextPersonName2);
                EditText end = (EditText) findViewById(R.id.editTextTextPersonName);
                Dijkstra.calculatePath(nodesList, Integer.parseInt(String.valueOf(start.getText())), Integer.parseInt(String.valueOf(end.getText())));
                ArrayList<Integer> path = Dijkstra.path;
                Dijkstra.pathProgress = 0;


                if (nodesList.get(path.get(0)).building != Build.OUT) {
                    intent.putExtra("type", "path");
                    intent.putExtra("building", String.valueOf(nodesList.get(path.get(0)).building));
                    intent.putExtra("floor", nodesList.get(path.get(0)).floor);
                    startActivity(intent);
                } else {
                    //draw Polyline
                    //show next/back buttons
                }
            }
        });*/

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constraint.setVisibility(View.INVISIBLE);
            }
        });
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        googleMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        //new LatLng(lastKnownLocation.getLatitude(),
                                                //lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d("TAG", "Current location is null. Using defaults.");
                            Log.e("TAG", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

        @Override
    public boolean onMarkerClick(Marker marker) {
        intent.putExtra("type", "browse");
        intent.putExtra("building", "");
        if(marker.equals(cbyMarker)) {
            intent.putExtra("building", "CBY");
            intent.putExtra("floor", 1);
        }

        if(marker.equals(steMarker)) {
            intent.putExtra("building", "STE");
            intent.putExtra("floor", 1);
        }

        if(marker.equals(lprMarker)) {
            intent.putExtra("building", "STM");
            intent.putExtra("floor", 2);
        }
        constraint.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        clickMessage.setVisibility(View.INVISIBLE);
        startMap.setVisibility(View.INVISIBLE);
        startLocation.setVisibility(View.INVISIBLE);
        buildingName.setText(intent.getStringExtra("building"));
        setStart.setEnabled(true);
        setStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNode = buildings.get(Build.valueOf(intent.getStringExtra("building"))).center;

                Dijkstra.calculatePath(nodesList, startNode, endNode);
                ArrayList<Integer> path = Dijkstra.path;
                Dijkstra.pathProgress = 0;
                if (nodesList.get(path.get(0)).building != Build.OUT) {
                    intent.putExtra("type", "path");
                    intent.putExtra("building", String.valueOf(nodesList.get(path.get(0)).building));
                    intent.putExtra("floor", nodesList.get(path.get(0)).floor);
                    startActivity(intent);
                } else {
                    //draw Polyline
                    //show next/back buttons
                }
            }
        });

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStart.setVisibility(View.INVISIBLE);
                cancelStart.setVisibility(View.INVISIBLE);
                endNode = buildings.get(Build.valueOf(intent.getStringExtra("building"))).center;
                clickMessage.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                startMap.setVisibility(View.VISIBLE);
                startLocation.setVisibility(View.VISIBLE);
                if (locationPermissionGranted)
                    startLocation.setEnabled(true);
                else
                    startLocation.setEnabled(false);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancel.setVisibility(View.INVISIBLE);
                        startMap.setVisibility(View.INVISIBLE);
                        startLocation.setVisibility(View.INVISIBLE);
                        clickMessage.setVisibility(View.INVISIBLE);
                    }
                });
                startLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Building> nearby = new ArrayList<>();
                        double[] pt1 = {getX(lastKnownLocation.getLongitude()), getY(lastKnownLocation.getLatitude())};
                        Enumeration<Build> keys = buildings.keys();
                        Build k;
                        while(keys.hasMoreElements()) {
                            k=keys.nextElement();
                            Building building1 = buildings.get(k);
                            Node node = nodesList.get(building1.center);
                            double[] pt2 = {node.x, node.y};
                            if(sqrt(pow((pt1[0]-pt2[0]),2)+pow((pt1[1]-pt2[1]),2))<65){
                                nearby.add(building1);
                            }
                        }


                    }
                });
                startMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        constraint.setVisibility(View.INVISIBLE);
                        setStart.setVisibility(View.VISIBLE);
                        setStart.setEnabled(false);
                        cancelStart.setVisibility(View.VISIBLE);
                        cancelStart.setEnabled(true);
                        cancelStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                constraint.setVisibility(View.VISIBLE);
                                setStart.setVisibility(View.INVISIBLE);
                                cancelStart.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });


            }
        });


        inside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        return false;

    }

}