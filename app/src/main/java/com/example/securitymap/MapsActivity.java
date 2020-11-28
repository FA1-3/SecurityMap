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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.PopupMenu;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Button next;
    private Button back;
    private int pathProgress;
    private int backProgress;
    private int nextProgress;
    private Build backBuilding;
    private Build nextBuilding;
    private int backFloor;
    private int nextFloor;
    private TextView nextText;
    private TextView backText;
    private ImageButton emergency;
    private ImageButton menuOptionsButton;
    private ArrayList<Integer> path;
    private ArrayList<Polyline> polyline;
    static boolean boo;

    private ImageButton searchBtn; //this is the button to pop open the search and list window

    // Search and List layout and contents
    private ConstraintLayout listAndSearchConstraint; //the layout containing the whole search window
    private ImageButton backBtnSearch; //the back btn in the search window
    private SearchView srchbar; //the search bar to type in and filter results
    private CheckBox bldChk; //check box to see only buildings
    private CheckBox plcChk; //check box to see only specific places/destinations
    private ListView lst; //this is the VIEW ONLY that is gonna DISPLAY THE LIST
    public static ArrayList<ListItem> itemsList = new ArrayList<ListItem>(); //this is the actual list containing ListItems
    private static final LatLng ltlgInside = new LatLng(0, 0);

    // Rating layout and contents
    private ConstraintLayout rateLyt;
    private RatingBar rateBr;
    private EditText cmtTxt;
    private Button subBtn;

    /*
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "text";
    private SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
    private SharedPreferences.Editor editor = sharedPreferences.edit();
    */

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
    public void setView(){
        pathProgress = Dijkstra.pathProgress;
        int counter=0;
        for(int i=0; i<=pathProgress; i++){
            if(Dijkstra.pathBuildings.get(i)==Build.OUT&&(i==0||Dijkstra.pathBuildings.get(i-1)!=Build.OUT))
                counter++;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyline.get(counter).getPoints().get(0), 20));
        if(pathProgress!=0){
            backBuilding=Dijkstra.pathBuildings.get(pathProgress-1);
            backFloor=Dijkstra.pathFloors.get(pathProgress-1);
            backProgress=pathProgress-1;
        } else {
            backBuilding = Build.NUL;
            backFloor = -1;
        }
        if(pathProgress!=Dijkstra.pathBuildings.size()-1){
            nextBuilding=Dijkstra.pathBuildings.get(pathProgress+1);
            nextFloor=Dijkstra.pathFloors.get(pathProgress+1);
            nextProgress=pathProgress+1;
        } else {
            nextBuilding = Build.NUL;
            nextFloor = -1;
        }

        back.setVisibility(View.VISIBLE);
        if(backBuilding!=Build.NUL) {
            backText.setVisibility(View.VISIBLE);
            back.setEnabled(true);
            if(backBuilding!=Build.OUT)
                backText.setText(backBuilding+" Floor "+backFloor);
            else
                backText.setText("Exterior");
        } else {
            backText.setVisibility(View.INVISIBLE);
            back.setEnabled(false);
        }

        next.setVisibility(View.VISIBLE);
        if(nextBuilding!=Build.NUL) {
            nextText.setVisibility(View.VISIBLE);
            next.setEnabled(true);
            if(nextBuilding!=Build.OUT)
                nextText.setText(nextBuilding+" Floor "+nextFloor);
            else
                nextText.setText("Exterior");
        } else {
            nextText.setVisibility(View.INVISIBLE);
            next.setEnabled(false);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dijkstra.pathProgress = backProgress;
                if (backBuilding != Build.NUL) {
                    if (Dijkstra.pathBuildings.get(backProgress) == Build.OUT) {
                        setView();
                    } else {
                        intent.putExtra("type", "path");
                        intent.putExtra("building", String.valueOf(backBuilding));
                        intent.putExtra("floor", backFloor);
                        startActivity(intent);
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dijkstra.pathProgress = nextProgress;
                if (nextBuilding != Build.NUL) {
                    if (Dijkstra.pathBuildings.get(nextProgress) == Build.OUT) {
                        setView();
                    } else {
                        intent.putExtra("type", "path");
                        intent.putExtra("building", String.valueOf(nextBuilding));
                        Log.d("outside", String.valueOf(nextBuilding));
                        intent.putExtra("floor", nextFloor);
                        Log.d("outside", String.valueOf(nextFloor));
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void startPath(){
        Dijkstra.calculatePath(nodesList, startNode, endNode);
        path = Dijkstra.path;
        Dijkstra.pathProgress = 0;
        next.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        polyline = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions();
        for (int i=0; i<path.size()-1; i++) {
            if(nodesList.get(path.get(i)).building==Build.OUT&&nodesList.get(path.get(i+1)).building==Build.OUT){
                if(i == 0 || nodesList.get(path.get(i - 1)).building != Build.OUT) {
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(20);
                    Log.d("outside", i+"tout");
                    polylineOptions.add(new LatLng((Math.toDegrees(nodesList.get(path.get(i)).y/EARTH_RADIUS))+origin.latitude, (Math.toDegrees(nodesList.get(path.get(i)).x)/(EARTH_RADIUS*Math.cos(Math.toRadians(origin.latitude))))+origin.longitude));
                }
                Log.d("outside", i+"toute");
                polylineOptions.add(new LatLng((Math.toDegrees(nodesList.get(path.get(i+1)).y/EARTH_RADIUS))+origin.latitude, (Math.toDegrees(nodesList.get(path.get(i+1)).x)/(EARTH_RADIUS*Math.cos(Math.toRadians(origin.latitude))))+origin.longitude));
            } else if(i>1&&nodesList.get(path.get(i-1)).building==Build.OUT&&nodesList.get(path.get(i-2)).building==Build.OUT){
                polyline.add(mMap.addPolyline(polylineOptions));
                Log.d("outside", i+"toutee");
            }
            if(i==path.size()-2&&i>1&&nodesList.get(path.get(i-1)).building==Build.OUT&&(nodesList.get(path.get(i-2)).building==Build.OUT||nodesList.get(path.get(i)).building==Build.OUT)){
                polyline.add(mMap.addPolyline(polylineOptions));
                Log.d("outside", i + "touteee");
            }
        }
        if (nodesList.get(path.get(0)).building != Build.OUT) {
            intent.putExtra("type", "path");
            intent.putExtra("building", String.valueOf(nodesList.get(path.get(0)).building));
            intent.putExtra("floor", nodesList.get(path.get(0)).floor);
            startActivity(intent);
        } else {
            setView();
        }
    }

    public double getX(double longitude){
        double x = EARTH_RADIUS*cos(origin.latitude);
        x = x*(longitude-origin.longitude)*PI/180;
        return x;
    }
    public double getY(double latitude){
        double y = EARTH_RADIUS*(latitude-origin.latitude)*PI/180;
        return y;
    }
    public void launchPreview(){
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
                startPath();
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

        // IMPORTANT NEED TO REMOVE EVENTUALLY
        boo = false;
    }

    private Marker cbyMarker;
    private Marker steMarker;
    private Marker stmMarker;
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
        next = (Button)findViewById(R.id.button9);
        next.setVisibility(View.INVISIBLE);
        back = (Button)findViewById(R.id.button8);
        back.setVisibility(View.INVISIBLE);
        backText = (TextView) findViewById(R.id.textView11);
        backText.setVisibility(View.INVISIBLE);
        nextText = (TextView) findViewById(R.id.textView12);
        nextText.setVisibility(View.INVISIBLE);
        emergency = (ImageButton) findViewById(R.id.imageButton3);
        emergency.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:6135625499"));
                startActivity(callIntent);
            }

        });

        menuOptionsButton = (ImageButton) findViewById(R.id.menuBtn);
        final PopupMenu dropDownMenu = new PopupMenu(this, menuOptionsButton);

        final Menu menu = dropDownMenu.getMenu();

        menu.add(0, 0, 0, "Reduced Mobility");
        menu.add(0, 1, 0, "Warmest");
        menu.add(0, 2, 0, "None");

        menu.setGroupCheckable(0, true, true);

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case 0:
                        // "Mobilité réduite" was selected -> computes shortest path accordingly;
                        return true;
                    case 1:
                        // "Chemin le plus chaud" was selected -> computes shortest path accordingly
                        return true;
                    case 2:
                        // "Déplacement libre" was selected -> computes shortest path accordingly
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
        LatLng stm = new LatLng(45.420319, -75.680508);
        LatLng lpr = new LatLng(45.421250, -75.680353);
        LatLng hs = new LatLng(45.421755, -75.679601);
        LatLng is = new LatLng(45.424537, -75.686460);
        LatLng cp = new LatLng(45.421764, -75.680541);

        cbyMarker = mMap.addMarker(new MarkerOptions().position(cby).title("Colonel By Hall (CBY)").icon(smallMarkerIcon));
        steMarker = mMap.addMarker(new MarkerOptions().position(ste).title("SITE (STE)"));
        stmMarker = mMap.addMarker(new MarkerOptions().position(stm).title("STEM (STM)"));
        lprMarker = mMap.addMarker(new MarkerOptions().position(lpr).title("Protection Services (LPR)"));
        hsMarker = mMap.addMarker(new MarkerOptions().position(hs).title("Health Services"));
        isMarker = mMap.addMarker(new MarkerOptions().position(is).title("Info Service"));
        cpMarker = mMap.addMarker(new MarkerOptions().position(cp).title("Campus Pharmacy"));


        // Stuff for the search bar layout
        listAndSearchConstraint = findViewById(R.id.listAndSearchConstraint);
        listAndSearchConstraint.setVisibility(View.INVISIBLE); //I'm making the whole thing invisible temporarily, to REMOVE!
        backBtnSearch = findViewById(R.id.backBtnSearch);
        srchbar = findViewById(R.id.searchBar);
        bldChk = findViewById(R.id.bldgCheckBox);
        plcChk = findViewById(R.id.placeCheckBox);
        lst = findViewById(R.id.theList);
        searchBtn = findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAndSearchConstraint.setVisibility(View.VISIBLE);
            }
        });

        backBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAndSearchConstraint.setVisibility(View.INVISIBLE);
            }
        });

        bldChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterList();
            }
        });
        plcChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterList();
            }
        });

        setupData();
        setupList();
        setupListOnClickListener();
        initSearching();

        // ******************************************** end of search layout stuff


        // Comment and rating menu stuff
        rateLyt = findViewById(R.id.ratingLayout);
        rateLyt.setVisibility(View.VISIBLE);
        rateBr = findViewById(R.id.ratingBar);
        cmtTxt = findViewById(R.id.commentBox);
        subBtn = findViewById(R.id.submitBtn);

            // what happens when the user clicks the SUBMIT button after giving feedback
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numStr;
                String cmt;
                String feedback;
                numStr = (int) rateBr.getRating();
                //Log.d("ratingTest",""+numStr);
                cmt = cmtTxt.getText().toString();
                //Log.d("ratingTest",cmt);

                // for now the review only exists in the LogCat
                feedback = "RATING: "+numStr+" STARS, COMMENT: "+cmt+" , For PATH from NODE "+startNode+" TO NODE "+endNode;
                Log.d("rating", feedback);
                //saveFeedback(feedback);
                //Log.d("rating",sharedPreferences.getString(TEXT, ""));

                /*
                try {
                    FileOutputStream fileOutputStream = openFileOutput(FILENAME, MODE_PRIVATE);
                    fileOutputStream.write(review.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                cmtTxt.getText().clear();
                rateBr.setRating(0);
                rateLyt.setVisibility(View.INVISIBLE);
            }
        });


        //mMap.setLatLngBoundsForCameraTarget(UOTTAWA);
        //mMap.setMinZoomPreference(15);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));


        InputStream inputStream = getResources().openRawResource(R.raw.nodesdata);
        CSVFile csvFile = new CSVFile();
        csvFile.inputStream = inputStream;
        csvFile.read();
        nodesList = CSVFile.getNodes();
        buildings = CSVFile.getBuildings();

        intent = new Intent(this, Indoor.class);


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

        //srcSetupData();

        startNode = 5001;
        endNode = 5040;
        startPath();
    } // End of the onMapReady

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

        if(marker.equals(stmMarker)) {
            intent.putExtra("building", "STM");
            intent.putExtra("floor", 2);
        }
        launchPreview();
        return false;

    } // end of the OnMarkerClick {}

    //This is in the main class thingy lol

    //public ListItem(String id, String name, int num)

    public void setupData(){

        //Format of ListItem constructor
        //public ListItem(String id, String name, String building, int floor, int num, int node) {

        //Buildings LatLng (LL) values
        LatLng cbyLL = new LatLng(45.419754, -75.679601);
        LatLng steLL = new LatLng(45.419308, -75.678701);
        LatLng stmLL = new LatLng(45.420319, -75.680508);
        LatLng mrnLL = new LatLng(45.42052646805507, -75.681055736891);
        LatLng hmlLL = new LatLng(45.42387081051715, -75.68587101939168);
        LatLng crxLL = new LatLng(45.42202898302483, -75.68181090791289);
        LatLng vanLL = new LatLng(45.42148723256023, -75.68340224794174);
        LatLng ftxLL = new LatLng(45.42374905438684, -75.6825786648393);
        LatLng dirLL = new LatLng(45.42095375422315, -75.68130497413767);
        LatLng tbtLL = new LatLng(45.424540235601334, -75.68633147106046);

        //Places LatLng (LL) values
        LatLng lprLL = new LatLng(45.421250, -75.680353);
        LatLng hsLL = new LatLng(45.421755, -75.679601);
        LatLng isLL = new LatLng(45.424537, -75.686460);
        LatLng cpLL = new LatLng(45.421764, -75.680541);

        //Buildings
        ListItem cby = new ListItem("building","Colonel By","cby",-1,0,-1, cbyLL);
        ListItem ste = new ListItem("building","SITE","ste",-1,1,-1, steLL);
        ListItem stm = new ListItem("building","STEM","stm",-1,2,-1, stmLL);
        ListItem mrn = new ListItem("building","Marion","mrn",-1,3,-1, mrnLL);
        ListItem hml = new ListItem("building","Hamelin","hml",-1,4,-1, hmlLL);
        ListItem crx = new ListItem("building","Learning Crossroads","crx",-1,5,-1, crxLL);
        ListItem van = new ListItem("building","Vanier","van",-1,6,-1, vanLL);
        ListItem ftx = new ListItem("building","Fauteux","ftx",-1,7,-1, ftxLL);
        ListItem dir = new ListItem("building","D'Iorio","dir",-1,8,-1, dirLL);
        ListItem tbt = new ListItem("building", "Tabaret Hall", "TBT", -1, 12, -1, tbtLL);


        //HAVE TO ADD THE NODE NUMBERS TO ALL THE PLACES!!!!!!!!!!!
        //Places
        ListItem lpr = new ListItem("place", "Protection Services (LPR)", "out", -1,9, 5074, lprLL);
        ListItem hs = new ListItem("place", "Health Services", "OUT", -1, 10, 5076, hsLL);
        ListItem is = new ListItem("place", "Information Services", "TBT", 1,11,5090, isLL);
        ListItem cp = new ListItem("place", "Campus Pharmacy", "OUT", -1, 13, 5076, cpLL);

        itemsList.add(cby);
        itemsList.add(ste);
        itemsList.add(stm);
        itemsList.add(mrn);
        itemsList.add(hml);
        itemsList.add(crx);
        itemsList.add(van);
        itemsList.add(ftx);
        itemsList.add(dir);
        itemsList.add(tbt);

        itemsList.add(lpr);
        itemsList.add(hs);
        itemsList.add(is);
        itemsList.add(cp);

    }

    public void setupList(){

        // I believe this is already done ahead of time, but if it doesnt mess anything up, leave it in case
        lst = (ListView) findViewById(R.id.theList);

        ListAdapter adapter = new ListAdapter(getApplicationContext(),0, itemsList);
        lst.setAdapter(adapter);

    }

    public void setupListOnClickListener(){

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // fills this ListItem object with the one that was clicked
                ListItem selectedItem = (ListItem) (lst.getItemAtPosition(position));

                // This is useless and I need to add in a feature for that
                Intent itemWasClicked = new Intent(getApplicationContext(), MapsActivity.class);

            }
        });

    }


    public void initSearching(){

        srchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<ListItem> filteredItems = new ArrayList<ListItem>();

                for (ListItem listItem: itemsList){ //goes through the entire initial list
                    if ((listItem.getName().toLowerCase().contains(newText.toLowerCase()))
                            || (listItem.getBuilding().toLowerCase().contains(newText.toLowerCase()))){
                        filteredItems.add(listItem);
                        // if the searched text is exactly present in the name of the list item,
                        // then make it part of the filtered list of items
                    }
                }

                ListAdapter searchAdapter = new ListAdapter(getApplicationContext(),0, filteredItems);
                lst.setAdapter(searchAdapter);

                return false;
            }
        });

    }

    public void filterList(){

        boolean bldChkChecked = bldChk.isChecked();
        boolean plcChkChecked = plcChk.isChecked();

        if(!bldChkChecked && !plcChkChecked){
            setupList();
        }

        if(bldChkChecked && !plcChkChecked){
            ArrayList<ListItem> filteredItems = new ArrayList<ListItem>();
            for (ListItem listItem: itemsList){ //goes through the entire initial list
                if (listItem.getId().toLowerCase() == "building"){
                    filteredItems.add(listItem);
                    // if the item is of type building, keep it in the list
                }
            }
            ListAdapter bldChkAdapter = new ListAdapter(getApplicationContext(),0, filteredItems);
            lst.setAdapter(bldChkAdapter);
        }

        if(plcChkChecked && !bldChkChecked){
            ArrayList<ListItem> filteredItems = new ArrayList<ListItem>();
            for (ListItem listItem: itemsList){ //goes through the entire initial list
                if (listItem.getId().toLowerCase() == "place"){
                    filteredItems.add(listItem);
                    // if the item is of type building, keep it in the list
                }
            }
            ListAdapter plcChkAdapter = new ListAdapter(getApplicationContext(),0, filteredItems);
            lst.setAdapter(plcChkAdapter);
        }



    }

    /*
    public void saveFeedback(String feedback){

        if(!sharedPreferences.contains(TEXT)){
            editor.putString(TEXT,"");
        }

        if(sharedPreferences.getString(TEXT, "") != "") {
            feedback = sharedPreferences.getString(TEXT, "")+"\n"+feedback;
        }
        editor.putString(TEXT,feedback);
        editor.commit();
    } */

    @Override
    protected void onResume() {
        super.onResume();
        if(boo)
            setView();
    }
  
} //end of the whole thingy lol