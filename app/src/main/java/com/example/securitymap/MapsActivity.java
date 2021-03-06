package com.example.securitymap;


import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

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
    private ImageButton pin;
    private TextView dropPin;
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
    static boolean choosingStart;
    static boolean startpath;
    static boolean rating;
    static String mode;
    private Marker pinMarker;
    private Button pinDirections;
    private Marker startMarker;
    private BitmapDescriptor startIcon;
    private Marker endMarker;
    private BitmapDescriptor endIcon;
    static Bitmap startMarkerBitmap;
    static Bitmap endMarkerBitmap;
    static Bitmap arrow;
    static boolean warmest;

    private ImageButton searchBtn; //this is the button to pop open the search and list window

    // Search and List layout and contents
    private ConstraintLayout listAndSearchConstraint; //the layout containing the whole search window
    private ImageButton backBtnSearch; //the back btn in the search window
    private SearchView srchbar; //the search bar to type in and filter results
    private CheckBox bldChk; //check box to see only buildings
    private CheckBox plcChk; //check box to see only specific places/destinations
    private ListView lst; //this is the VIEW ONLY that is gonna DISPLAY THE LIST
    private ArrayList<Attribute> attributes;
    public static ArrayList<ListItem> itemsList = new ArrayList<ListItem>(); //this is the actual list containing ListItems
    private static final LatLng ltlgInside = new LatLng(0, 0);

    // Rating layout and contents
    private ConstraintLayout rateLyt;
    private RatingBar rateBr;
    private EditText cmtTxt;
    private Button subBtn;

    private ListItem cby;
    private ListItem ste;
    private ListItem stm;
    private ListItem mrn;
    private ListItem hml;
    private ListItem crx;
    private ListItem van;
    private ListItem ftx;
    private ListItem dir;
    private ListItem tbt;
    private ListItem lpr;
    private ListItem is;
    private ListItem hs;
    private ListItem cp;


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

    public int getClosestNode(LatLng pos){
        int n=0;
        Node tempNode;
        double minDist=1000000000;
        double dist;

        Enumeration<Integer> keys = nodesList.keys();
            while(keys.hasMoreElements()){
                tempNode = nodesList.get(keys.nextElement());
                if(tempNode.building==Build.OUT){
                    dist = Math.pow(EARTH_RADIUS*Math.toRadians(Math.toDegrees(tempNode.y/EARTH_RADIUS)+origin.latitude-pos.latitude), 2);
                    dist += Math.pow((EARTH_RADIUS*Math.cos(Math.toRadians(origin.latitude)))*Math.toRadians(Math.toDegrees(tempNode.x/(EARTH_RADIUS*Math.cos(Math.toRadians(origin.latitude))))+origin.longitude-pos.longitude), 2);
                    if(dist<minDist){
                        n = tempNode.n;
                        minDist = dist;
                    }
                }
        }
        return n;
    }



    public void setView() {
        if (mode.equals("path")) {
            constraint.setVisibility(View.INVISIBLE);
            rateLyt.setVisibility(View.INVISIBLE);
            dropPin.setVisibility(View.INVISIBLE);
            setStart.setVisibility(View.INVISIBLE);
            cancelStart.setVisibility(View.INVISIBLE);
            pin.setVisibility(View.INVISIBLE);

            pathProgress = Dijkstra.pathProgress;
            int counter = 0;
            for (int i = 0; i <= pathProgress; i++) {
                if (Dijkstra.pathBuildings.get(i) == Build.OUT && (i == 0 || Dijkstra.pathBuildings.get(i - 1) != Build.OUT)) {
                    counter++;
                }
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyline.get(counter-1).getPoints().get(0), 20));
            if (pathProgress != 0) {
                backBuilding = Dijkstra.pathBuildings.get(pathProgress - 1);
                backFloor = Dijkstra.pathFloors.get(pathProgress - 1);
                backProgress = pathProgress - 1;
            } else {
                backBuilding = Build.NUL;
                backFloor = -1;
            }
            if (pathProgress != Dijkstra.pathBuildings.size() - 1) {
                nextBuilding = Dijkstra.pathBuildings.get(pathProgress + 1);
                nextFloor = Dijkstra.pathFloors.get(pathProgress + 1);
                nextProgress = pathProgress + 1;
            } else {
                nextBuilding = Build.NUL;
                nextFloor = -1;
            }

            back.setVisibility(View.VISIBLE);
            if (backBuilding != Build.NUL) {
                backText.setVisibility(View.VISIBLE);
                back.setEnabled(true);
                if (backBuilding != Build.OUT)
                    backText.setText(backBuilding +" "+ buildings.get(backBuilding).floors.get(backFloor).name);
                else
                    backText.setText("Exterior");
            } else {
                backText.setVisibility(View.INVISIBLE);
                back.setEnabled(false);
            }

            next.setVisibility(View.VISIBLE);
            if (nextBuilding != Build.NUL) {
                nextText.setVisibility(View.VISIBLE);
                next.setText("Next");
                next.setEnabled(true);
                if (nextBuilding != Build.OUT)
                    nextText.setText(nextBuilding +" "+ buildings.get(nextBuilding).floors.get(nextFloor).name);
                else
                    nextText.setText("Exterior");
            } else {
                nextText.setVisibility(View.INVISIBLE);
                next.setText("End");
                next.setEnabled(true);
            }

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rateLyt.getVisibility() == View.VISIBLE) {
                        rateLyt.setVisibility(View.INVISIBLE);
                    } else {
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
                            intent.putExtra("floor", nextFloor);
                            startActivity(intent);
                        }
                    } else {
                        launchRating();
                    }
                }
            });
        } else {
            if(choosingStart) {
                setStart.setVisibility(View.VISIBLE);
                cancelStart.setVisibility(View.VISIBLE);
                backBtn.performClick();
            }
            if(dropPin.getText().equals("Remove Pin"))
                pinDirections.setVisibility(View.VISIBLE);
            else
                pin.setEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
            rateLyt.setVisibility(View.INVISIBLE);
            constraint.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            backText.setVisibility(View.INVISIBLE);
            nextText.setVisibility(View.INVISIBLE);
            dropPin.setVisibility(View.VISIBLE);
            pin.setVisibility(View.VISIBLE);
            if(!polyline.isEmpty()){
                for (Polyline poly:polyline) {
                    poly.remove();
                }
            }
            if(nodesList.get(startNode).building==Build.OUT){
                startMarker.remove();
            }
            if(nodesList.get(endNode).building==Build.OUT){
                endMarker.remove();
            }
        }

    }

    public void startPath(){
        mode = "path";
        if(dropPin.getText().equals("Remove Pin")){
            pinMarker.remove();
        }
        Dijkstra.calculatePath(nodesList, startNode, endNode, attributes);
        path = Dijkstra.path;
        Dijkstra.pathProgress = 0;
        pinDirections.setVisibility(View.INVISIBLE);
        next.setVisibility(View.VISIBLE);
        nextText.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        backText.setVisibility(View.VISIBLE);
        polyline = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions();
        for (int i=0; i<path.size(); i++) {
            if(i<path.size()-1&&(nodesList.get(path.get(i)).building==Build.OUT&&nodesList.get(path.get(i+1)).building==Build.OUT)){
                if(i == 0 || nodesList.get(path.get(i - 1)).building != Build.OUT) {
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(20);
                    polylineOptions.startCap((new RoundCap()));
                    polylineOptions.endCap((new RoundCap()));
                    polylineOptions.add(new LatLng((Math.toDegrees(nodesList.get(path.get(i)).y/EARTH_RADIUS))+origin.latitude, (Math.toDegrees(nodesList.get(path.get(i)).x)/(EARTH_RADIUS*Math.cos(Math.toRadians(origin.latitude))))+origin.longitude));
                    if(i==0)
                        startMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Math.toDegrees(nodesList.get(startNode).y/EARTH_RADIUS)+origin.latitude, Math.toDegrees(nodesList.get(startNode).x/(Math.cos(Math.toRadians(origin.latitude))*EARTH_RADIUS))+origin.longitude)).icon(startIcon));
                    else
                        polylineOptions.startCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrowstart), 1000));
                }
                polylineOptions.add(new LatLng((Math.toDegrees(nodesList.get(path.get(i+1)).y/EARTH_RADIUS))+origin.latitude, (Math.toDegrees(nodesList.get(path.get(i+1)).x)/(EARTH_RADIUS*Math.cos(Math.toRadians(origin.latitude))))+origin.longitude));
            } else if(i!=path.size()-1&&i>1&&nodesList.get(path.get(i)).building==Build.OUT&&nodesList.get(path.get(i-1)).building==Build.OUT){
                polylineOptions.endCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow), 1000));
                polyline.add(mMap.addPolyline(polylineOptions));
            }
            if(i==path.size()-1&&i>1&&nodesList.get(path.get(i)).building==Build.OUT){
                if(nodesList.get(path.get(i-1)).building!=Build.OUT)
                    polylineOptions.add(new LatLng((Math.toDegrees(nodesList.get(path.get(i)).y/EARTH_RADIUS))+origin.latitude, (Math.toDegrees(nodesList.get(path.get(i)).x)/(EARTH_RADIUS*Math.cos(Math.toRadians(origin.latitude))))+origin.longitude));
                endMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Math.toDegrees(nodesList.get(endNode).y/EARTH_RADIUS)+origin.latitude, Math.toDegrees(nodesList.get(endNode).x/(Math.cos(Math.toRadians(origin.latitude))*EARTH_RADIUS))+origin.longitude)).icon(endIcon));
                polyline.add(mMap.addPolyline(polylineOptions));
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

    public void launchPreview(){

        constraint.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        clickMessage.setVisibility(View.INVISIBLE);
        startMap.setVisibility(View.INVISIBLE);
        startLocation.setVisibility(View.INVISIBLE);
        direction.setVisibility(View.VISIBLE);
        pinDirections.setVisibility(View.INVISIBLE);
        inside.setVisibility(View.VISIBLE);
        next.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);
        nextText.setVisibility(View.INVISIBLE);
        backText.setVisibility(View.INVISIBLE);
        buildingName.setText(intent.getStringExtra("name"));
        setStart.setEnabled(true);
        if(intent.getStringExtra("name").equals("Custom Marker")) {
            cancel.setVisibility(View.VISIBLE);
            startMap.setVisibility(View.VISIBLE);
            startLocation.setVisibility(View.VISIBLE);
            direction.setVisibility(View.INVISIBLE);
            inside.setVisibility(View.INVISIBLE);
            setStart.setVisibility(View.INVISIBLE);
            cancelStart.setVisibility(View.INVISIBLE);
            pinDirections.setVisibility(View.INVISIBLE);
            pin.setEnabled(false);
        } else {
            if(dropPin.getText().equals("Remove Pin")) {
                pin.performClick();
                setStart.setEnabled(true);
            }
            pin.setEnabled(false);
            setStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int node1 = intent.getIntExtra("node", -1);
                    if(node1==-1)
                        startNode = buildings.get(Build.valueOf(intent.getStringExtra("building"))).center;
                    else
                        startNode = node1;
                    startPath();
                }
            });
        }

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStart.setVisibility(View.INVISIBLE);
                cancelStart.setVisibility(View.INVISIBLE);
                clickMessage.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                startMap.setVisibility(View.VISIBLE);
                startLocation.setVisibility(View.VISIBLE);
                if (locationPermissionGranted)
                    startLocation.setEnabled(true);
                else
                    startLocation.setEnabled(false);


            }
        });

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
                /*
                setStart.setVisibility(View.INVISIBLE);
                cancelStart.setVisibility(View.INVISIBLE);
                int node1 = intent.getIntExtra("node", -1);
                if(node1==-1)
                    endNode = buildings.get(Build.valueOf(intent.getStringExtra("building"))).center;
                else
                    endNode = node1;
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
                }*/
            }
        });
        startMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStart.setVisibility(View.INVISIBLE);
                cancelStart.setVisibility(View.INVISIBLE);
                if (intent.getStringExtra("name").equals("Custom Marker")){
                    pin.performClick();
                    setStart.setVisibility(View.VISIBLE);
                    setStart.setEnabled(false);
                    cancelStart.setVisibility(View.VISIBLE);
                } else {
                    int node1 = intent.getIntExtra("node", -1);
                    if (node1 == -1)
                        endNode = buildings.get(Build.valueOf(intent.getStringExtra("building"))).center;
                    else
                        endNode = node1;
                }
                choosingStart=true;
                pin.setEnabled(true);
                constraint.setVisibility(View.INVISIBLE);
                setStart.setVisibility(View.VISIBLE);
                setStart.setEnabled(false);
                cancelStart.setVisibility(View.VISIBLE);
                cancelStart.setEnabled(true);
                cancelStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosingStart = false;
                        constraint.setVisibility(View.VISIBLE);
                        setStart.setVisibility(View.INVISIBLE);
                        cancelStart.setVisibility(View.INVISIBLE);
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

        boo = false;
        rating=false;
        mode = "browse";
        attributes = new ArrayList<>();
    }

    //first set of markers
    private Marker cbyMarker;
    private Marker steMarker;
    private Marker stmMarker;
    private Marker lprMarker;
    private Marker hsMarker;
    private Marker isMarker;
    private Marker cpMarker;
    //2nd set of markers below
    private Marker mrnMarker; // Marion
    private Marker crxMarker; // CRX, duh lol
    private Marker vanMarker; // Vanier Hall
    private Marker ftxMarker; //Fauteux
    private Marker dirMarker; //D'Iorio
    private Marker tbtMarker; //Tabaret Hall
    private Marker hmlMarker; // Hamelin Hall

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() { //https://stackoverflow.com/questions/14829195/google-maps-error-markers-position-is-not-updated-after-drag
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });

        mMap.setMapType(mMap.MAP_TYPE_HYBRID);

        RelativeLayout layout = findViewById(R.id.relative);
        width = layout.getWidth();
        height = layout.getHeight();
        polyline = new ArrayList<>();

        constraint = findViewById(R.id.constraint1);
        constraint.setVisibility(View.INVISIBLE);
        pinDirections = findViewById(R.id.button17);
        pinDirections.setVisibility(View.INVISIBLE);
        backBtn =  findViewById(R.id.imageView3);
        buildingName = findViewById(R.id.textView5);
        direction = findViewById(R.id.button6);
        inside = findViewById(R.id.button5);
        cancel = findViewById(R.id.textView14);
        startMap =  findViewById(R.id.button12);
        startLocation =  findViewById(R.id.button13);
        clickMessage = findViewById(R.id.textView19);
        setStart = findViewById(R.id.button14);
        setStart.setVisibility(View.INVISIBLE);
        cancelStart = findViewById(R.id.button16);
        cancelStart.setVisibility(View.INVISIBLE);
        next = findViewById(R.id.button9);
        next.setVisibility(View.INVISIBLE);
        back = findViewById(R.id.button8);
        back.setVisibility(View.INVISIBLE);
        dropPin = findViewById(R.id.textView16);
        dropPin.setText("Drop Pin");
        pin = findViewById(R.id.imageButton7);
        pin.setEnabled(true);
        backText =  findViewById(R.id.textView11);
        backText.setVisibility(View.INVISIBLE);
        nextText =  findViewById(R.id.textView12);
        nextText.setVisibility(View.INVISIBLE);
        emergency =  findViewById(R.id.imageButton3);
        emergency.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:6135625499"));
                startActivity(callIntent);
            }

        });
        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dropPin.getText().equals("Drop Pin")){
                    dropPin.setText("Remove Pin");
                    pinDirections.setVisibility(View.VISIBLE);
                    if(choosingStart) {
                        setStart.setVisibility(View.VISIBLE);
                        setStart.setEnabled(true);
                        cancelStart.setVisibility(View.VISIBLE);
                    }
                    pinMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(mMap.getCameraPosition().target)
                                    .draggable(true));
                    pinDirections.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent.putExtra("name", "Custom Marker");
                            endNode = getClosestNode(pinMarker.getPosition());
                            launchPreview();
                        }
                    });
                    setStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startNode = getClosestNode(pinMarker.getPosition());
                            startPath();
                        }
                    });
                }
                else if(dropPin.getText().equals("Remove Pin")){
                    dropPin.setText("Drop Pin");
                    pinDirections.setVisibility(View.INVISIBLE);
                    pinMarker.remove();
                    setStart.setEnabled(false);
                }
            }
        });

        menuOptionsButton =  findViewById(R.id.menuBtn);
        final PopupMenu dropDownMenu = new PopupMenu(this, menuOptionsButton);

        final Menu menu = dropDownMenu.getMenu();

        menu.add(0, 0, 0, "Reduced Mobility");
        menu.add(0, 1, 0, "Warmest");

        menu.setGroupCheckable(0, true, false);

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                switch (item.getItemId()) {
                    case 0:
                        if(item.isChecked()) {
                            if (!attributes.contains(Attribute.STAIR))
                                attributes.add(Attribute.STAIR);
                        } else
                            attributes.remove(Attribute.STAIR);
                        return true;
                    case 1:
                        warmest = item.isChecked();
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

        Bitmap a = BitmapFactory.decodeResource(getResources(), R.drawable.star_icon);
        startMarkerBitmap = Bitmap.createScaledBitmap(a, 126, 180, false);
        startIcon = BitmapDescriptorFactory.fromBitmap(startMarkerBitmap);

        Bitmap q = BitmapFactory.decodeResource(getResources(), R.drawable.end_marker_1);
        endMarkerBitmap = Bitmap.createScaledBitmap(q, 137, 201, false);
        endIcon = BitmapDescriptorFactory.fromBitmap(endMarkerBitmap);

        Bitmap z = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        arrow = Bitmap.createScaledBitmap(z, 201, 201, false);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.building_icon_2);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 103, 150, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        Bitmap c = BitmapFactory.decodeResource(getResources(), R.drawable.health_service_icon);
        Bitmap healthMarker = Bitmap.createScaledBitmap(c, 103, 150, false);
        BitmapDescriptor healthMarkerIcon = BitmapDescriptorFactory.fromBitmap(healthMarker);

        Bitmap d = BitmapFactory.decodeResource(getResources(), R.drawable.info_icon);
        Bitmap infoMarker = Bitmap.createScaledBitmap(d, 103, 150, false);
        BitmapDescriptor infoMarkerIcon = BitmapDescriptorFactory.fromBitmap(infoMarker);

        Bitmap e = BitmapFactory.decodeResource(getResources(), R.drawable.check_icon);
        Bitmap checkMarker = Bitmap.createScaledBitmap(e, 103, 150, false);
        BitmapDescriptor checkMarkerIcon = BitmapDescriptorFactory.fromBitmap(checkMarker);

        LatLng cby = new LatLng(45.419754, -75.679601);
        LatLng ste = new LatLng(45.419308, -75.678701);
        LatLng stm = new LatLng(45.420319, -75.680508);
        LatLng mrn = new LatLng(45.42052646805507, -75.681055736891);
        LatLng hml = new LatLng(45.42387081051715, -75.68587101939168);
        LatLng crx = new LatLng(45.42202898302483, -75.68181090791289);
        LatLng van = new LatLng(45.42148723256023, -75.68340224794174);
        LatLng ftx = new LatLng(45.42374905438684, -75.6825786648393);
        LatLng dir = new LatLng(45.42095375422315, -75.68130497413767);
        LatLng tbt = new LatLng(45.424540235601334, -75.68633147106046);

        LatLng lpr = new LatLng(45.421250, -75.680353);
        LatLng hs = new LatLng(45.421762, -75.680357);
        LatLng is = new LatLng(45.424537, -75.686460);
        LatLng cp = new LatLng(45.421764, -75.680541);

        cbyMarker = mMap.addMarker(new MarkerOptions().position(cby).title("Colonel By Hall (CBY)").icon(smallMarkerIcon));
        steMarker = mMap.addMarker(new MarkerOptions().position(ste).title("SITE (STE)").icon(smallMarkerIcon));
        stmMarker = mMap.addMarker(new MarkerOptions().position(stm).title("STEM (STM)").icon(smallMarkerIcon));
        mrnMarker = mMap.addMarker(new MarkerOptions().position(mrn).title("Marion Hall (MRN)").icon(smallMarkerIcon));
        crxMarker = mMap.addMarker(new MarkerOptions().position(crx).title("Learning Crossroads (CRX)").icon(smallMarkerIcon));
        hmlMarker = mMap.addMarker(new MarkerOptions().position(hml).title("Hamelin Hall (HML)").icon(smallMarkerIcon));
        ftxMarker = mMap.addMarker(new MarkerOptions().position(ftx).title("Fauteux Hall (FTX)").icon(smallMarkerIcon));
        vanMarker = mMap.addMarker(new MarkerOptions().position(van).title("Vanier Hall (VNR)").icon(smallMarkerIcon));
        dirMarker = mMap.addMarker(new MarkerOptions().position(dir).title("D'Iorio Hall (DRO)").icon(smallMarkerIcon));
        tbtMarker = mMap.addMarker(new MarkerOptions().position(tbt).title("Tabaret Hall (TBT)").icon(smallMarkerIcon));

        lprMarker = mMap.addMarker(new MarkerOptions().position(lpr).title("Protection Services (LPR)").icon(checkMarkerIcon));
        hsMarker = mMap.addMarker(new MarkerOptions().position(hs).title("Health Services").icon(healthMarkerIcon));
        isMarker = mMap.addMarker(new MarkerOptions().position(is).title("Info Service").icon(infoMarkerIcon));
        cpMarker = mMap.addMarker(new MarkerOptions().position(cp).title("Campus Pharmacy").icon(healthMarkerIcon));


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

        rateLyt = findViewById(R.id.ratingLayout);
        rateLyt.setVisibility(View.INVISIBLE);
        rateBr = findViewById(R.id.ratingBar);
        cmtTxt = findViewById(R.id.commentBox);
        subBtn = findViewById(R.id.submitBtn);


        mMap.setLatLngBoundsForCameraTarget(UOTTAWA);
        mMap.setMinZoomPreference(15);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));


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
                pin.setEnabled(true);
            }
        });
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        googleMap.setOnMarkerClickListener(this);

        //srcSetupData();
        setView();
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
        if(!mode.equals("path")) {
            intent.putExtra("type", "browse");
            intent.putExtra("building", "");
            ListItem selectedItem = cby;
            if (marker.equals(cbyMarker)) {
                selectedItem = cby;
            }

            if (marker.equals(steMarker)) {
                selectedItem = ste;
            }

            if (marker.equals(stmMarker)) {
                selectedItem = stm;
            }

            if (marker.equals(mrnMarker)) {
                selectedItem = mrn;
            }

            if (marker.equals(hmlMarker)) {
                selectedItem = hml;
            }

            if (marker.equals(crxMarker)) {
                selectedItem = crx;
            }

            if (marker.equals(vanMarker)) {
                selectedItem = van;
            }

            if (marker.equals(ftxMarker)) {
                selectedItem = ftx;
            }

            if (marker.equals(dirMarker)) {
                selectedItem = dir;
            }

            if (marker.equals(tbtMarker)) {
                selectedItem = tbt;
            }

            if (marker.equals(lprMarker)) {
                selectedItem = lpr;
            }

            if (marker.equals(isMarker)) {
                selectedItem = is;
            }

            if (marker.equals(hsMarker)) {
                selectedItem = hs;
            }

            if (marker.equals(cpMarker)) {
                selectedItem = cp;
            }

            String name = selectedItem.getName();
            if (!selectedItem.getBuilding().equals("OUT"))
                name = name+" ("+selectedItem.getBuilding().toUpperCase()+")";
            intent.putExtra("name", name);
            intent.putExtra("building", selectedItem.getBuilding().toUpperCase());
            intent.putExtra("floor", selectedItem.getFloor());
            intent.putExtra("node", selectedItem.getNode());
            launchPreview();
        }
        return false;

    } // end of the OnMarkerClick {}

    //This is in the main class thingy lol

    public void setupData(){

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
        LatLng hsLL = new LatLng(45.421755, -75.680357);
        LatLng isLL = new LatLng(45.424537, -75.686460);
        LatLng cpLL = new LatLng(45.421764, -75.680541);

        //Buildings
        cby = new ListItem("building","Colonel By","cby",1,0,-1, cbyLL);
        ste = new ListItem("building","SITE","ste",1,1,-1, steLL);
        stm = new ListItem("building","STEM","stm",2,2,-1, stmLL);
        mrn = new ListItem("building","Marion","mrn",1,3,5024, mrnLL);
        hml = new ListItem("building","Hamelin","hml",1,4,5087, hmlLL);
        crx = new ListItem("building","Learning Crossroads","crx",-1,5,5091, crxLL);
        van = new ListItem("building","Vanier Hall","vnr",1,6,-1, vanLL);
        ftx = new ListItem("building","Fauteux","ftx",1,7,-1, ftxLL);
        dir = new ListItem("building","D'Iorio","dir",1,8,-1, dirLL);
        tbt = new ListItem("building", "Tabaret Hall", "TBT", 1, 12, 5090, tbtLL);

        //Places
        lpr = new ListItem("place", "Protection Services (LPR)", "OUT", 1,9, 5074, lprLL);
        hs = new ListItem("place", "Health Services", "OUT", 1, 10, 5076, hsLL);
        is = new ListItem("place", "Information Services", "TBT", 1,11,5090, isLL);
        cp = new ListItem("place", "Campus Pharmacy", "OUT", 1, 13, 5076, cpLL);

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

        lst = findViewById(R.id.theList);

        ListAdapter adapter = new ListAdapter(getApplicationContext(),0, itemsList);
        lst.setAdapter(adapter);

    }

    public void setupListOnClickListener(){

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListItem selectedItem = (ListItem) (lst.getItemAtPosition(position));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedItem.getCoords(), 17));
                intent.putExtra("type", "browse");
                String name = selectedItem.getName();
                if (!selectedItem.getBuilding().equals("OUT"))
                    name = name+" ("+selectedItem.getBuilding().toUpperCase()+")";
                intent.putExtra("name", name);
                intent.putExtra("building", selectedItem.getBuilding().toUpperCase());
                intent.putExtra("floor", selectedItem.getFloor());
                intent.putExtra("node", selectedItem.getNode());
                launchPreview();

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
            ArrayList<ListItem> filteredItems = new ArrayList<>();
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
            ArrayList<ListItem> filteredItems = new ArrayList<>();
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

    public void launchRating(){
        // Comment and rating menu stuff
        rateLyt.setVisibility(View.VISIBLE);
        setStart.setVisibility(View.INVISIBLE);
        cancelStart.setVisibility(View.INVISIBLE);
        constraint.setVisibility(View.INVISIBLE);
        next.setText("End");
        nextText.setVisibility(View.INVISIBLE);
        next.setEnabled(false);
        if(Dijkstra.pathBuildings.size()>0){
            back.setEnabled(true);
            backText.setVisibility(View.VISIBLE);
            if(Dijkstra.pathBuildings.get(Dijkstra.pathBuildings.size()-1)!=Build.OUT)
                backText.setText(Dijkstra.pathBuildings.get(Dijkstra.pathBuildings.size()-1) +" "+ buildings.get(Dijkstra.pathBuildings.get(Dijkstra.pathBuildings.size()-1)).floors.get(Dijkstra.pathFloors.get(Dijkstra.pathFloors.size()-1)).name);
            else
                backText.setText("Exterior");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dijkstra.pathProgress = Dijkstra.pathBuildings.size()-1;
                if (Dijkstra.pathBuildings.get(Dijkstra.pathBuildings.size()-1) != Build.OUT) {
                    intent.putExtra("type", "path");
                    intent.putExtra("building", String.valueOf(nodesList.get(path.get(Dijkstra.path.size()-1)).building));
                    intent.putExtra("floor", nodesList.get(path.get(Dijkstra.path.size()-1)).floor);
                    startActivity(intent);
                } else {
                    mode = "path";
                    pathProgress = Dijkstra.pathProgress;
                    next.setEnabled(true);
                    setView();
                }
            }
        });
        // what happens when the user clicks the SUBMIT button after giving feedback
        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numStr;
                String cmt;
                String feedback;
                numStr = (int) rateBr.getRating();
                mode = "browse";
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
                boo = false;
                choosingStart = false;
                startpath = false;
                rating = false;
                pin.setEnabled(true);
                cmt = cmtTxt.getText().toString();
                if (dropPin.getText().equals("Remove Pin"))
                    pin.performClick();

                // for now the review only exists in the LogCat
                feedback = "RATING: "+numStr+" STARS, COMMENT: "+cmt+" , For PATH from NODE "+startNode+" TO NODE "+endNode;
                Log.d("rating", feedback);

                cmtTxt.getText().clear();
                rateBr.setRating(0);
                rateLyt.setVisibility(View.INVISIBLE);
                setView();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mode.equals("exit")){
            mode = "browse";
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
            boo = false;
            choosingStart = false;
            startpath = false;
            setView();
        } else
        if(rating){
            mode = "path";
            setView();
            launchRating();
        }
        else if(boo)
            setView();
        else if(startpath)
            startPath();
        else if(choosingStart){
            pin.setEnabled(true);
            setStart.setVisibility(View.VISIBLE);
            setStart.setEnabled(false);
            cancelStart.setVisibility(View.VISIBLE);
            constraint.setVisibility(View.INVISIBLE);
        }
    }
}