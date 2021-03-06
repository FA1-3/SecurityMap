package com.example.securitymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class Indoor extends AppCompatActivity {
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private ScaleGestureDetector mScaleDetector;
    private ImageView floorPlan;
    private ImageView pathImage;
    private float mScaleFactor=1.0f;
    private String build;
    private int floor;
    private String type;
    private Building building;
    private Hashtable<Build, Building> buildings;
    private Hashtable<Integer, Node> nodes;
    private ArrayList<Bitmap> floorBitmaps = new ArrayList<>();
    private ArrayList<Integer> path = new ArrayList<>();
    private SeekBar floorBar;
    private TextView floorText;
    private TextView buildingText;
    private Button back;
    private TextView backText;
    private Button next;
    private TextView nextText;
    private int pathProgress;
    private int nextProgress;
    private int backProgress;
    private Build nextBuilding;
    private int nextFloor;
    private Build backBuilding;
    private int backFloor;
    private Button directions;
    private ImageView pin;
    private ImageButton pinBtn;
    private TextView dropPin;
    private ConstraintLayout constraint;
    private float initialY;
    private Button setStart;
    private Button cancelStart;
    private Button startMap;
    private Button startLocation;

    public void createBitmaps(){
        Paint myPaint = new Paint();
        myPaint.setColor(Color.RED);
        myPaint.setAntiAlias(true);
        myPaint.setStrokeWidth(20);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);

        path = Dijkstra.path;
        for (int i = 0; i < building.floors.size(); i++) {
            Floor tempFloor = building.floors.get(i);
            double ratio = 1500 / tempFloor.width;
            Bitmap tempBitmap = Bitmap.createBitmap(1500, (int) (ratio * tempFloor.height), Bitmap.Config.ARGB_8888);
            tempBitmap.eraseColor(Color.TRANSPARENT);
            tempBitmap.setHasAlpha(true);
            Canvas tempCanvas = new Canvas(tempBitmap);
            for (int k = 0; k < path.size() - 1; k++) {
                if (nodes.get(path.get(k)).building == building.name && nodes.get(path.get(k)).building == nodes.get(path.get(k + 1)).building) {
                    if (nodes.get(path.get(k)).floor == nodes.get(path.get(k + 1)).floor && nodes.get(path.get(k)).floor == i) {
                        tempCanvas.drawLine((int) (ratio * (nodes.get(path.get(k)).x + tempFloor.ox)), (int) (ratio * (tempFloor.height - nodes.get(path.get(k)).y - tempFloor.oy)), (int) (ratio * (nodes.get(path.get(k + 1)).x + tempFloor.ox)), (int) (ratio * (tempFloor.height - nodes.get(path.get(k + 1)).y - tempFloor.oy)), myPaint);
                        if(k==0)
                            tempCanvas.drawBitmap(MapsActivity.startMarkerBitmap, new Rect(0,0, 126, 180), new Rect((int)(ratio * (nodes.get(path.get(k)).x + tempFloor.ox))-50, (int) (ratio * (tempFloor.height - nodes.get(path.get(k)).y - tempFloor.oy))-143, (int)(ratio * (nodes.get(path.get(k)).x + tempFloor.ox))+50, (int) (ratio * (tempFloor.height - nodes.get(path.get(k)).y - tempFloor.oy))), null);
                        if(k==path.size() - 2)
                            tempCanvas.drawBitmap(MapsActivity.endMarkerBitmap, new Rect(0,0, 137, 201), new Rect((int)(ratio * (nodes.get(path.get(k+1)).x + tempFloor.ox))-50, (int) (ratio * (tempFloor.height - nodes.get(path.get(k+1)).y - tempFloor.oy))-145, (int)(ratio * (nodes.get(path.get(k+1)).x + tempFloor.ox))+50, (int) (ratio * (tempFloor.height - nodes.get(path.get(k+1)).y - tempFloor.oy))), null);
                        if(k<path.size() - 2 && (nodes.get(path.get(k)).building != nodes.get(path.get(k + 2)).building||nodes.get(path.get(k + 2)).floor!=i)){
                            tempCanvas.save();
                            float x = (float)(ratio * (nodes.get(path.get(k + 1)).x + tempFloor.ox));
                            float y = (float) (ratio * (tempFloor.height - nodes.get(path.get(k + 1)).y - tempFloor.oy));
                            double anglee = Math.atan2((tempFloor.height - nodes.get(path.get(k + 1)).y - tempFloor.oy)-(tempFloor.height - nodes.get(path.get(k)).y - tempFloor.oy), (nodes.get(path.get(k + 1)).x + tempFloor.ox)-(nodes.get(path.get(k)).x + tempFloor.ox));
                            tempCanvas.rotate((float)(Math.toDegrees(anglee)-270.0), x, y);
                            double height = 70;
                            tempCanvas.drawBitmap(MapsActivity.arrow, new Rect(0,0,201,201), new Rect((int)(x-(height/2)), (int)(y-(height/2)), (int)(x+(height/2)), (int)(y+(height/2))), null);
                            tempCanvas.restore();
                        }
                        if(k>0 && (nodes.get(path.get(k)).building != nodes.get(path.get(k - 1)).building||nodes.get(path.get(k - 1)).floor!=i)){
                            tempCanvas.save();
                            float x = (float)(ratio * (nodes.get(path.get(k)).x + tempFloor.ox));
                            float y = (float) (ratio * (tempFloor.height - nodes.get(path.get(k)).y - tempFloor.oy));
                            double angles = Math.atan2((tempFloor.height - nodes.get(path.get(k + 1)).y - tempFloor.oy)-(tempFloor.height - nodes.get(path.get(k)).y - tempFloor.oy), (nodes.get(path.get(k + 1)).x + tempFloor.ox)-(nodes.get(path.get(k)).x + tempFloor.ox));
                            tempCanvas.rotate((float)(Math.toDegrees(angles)-270.0), x, y);
                            double height = 70;
                            tempCanvas.drawBitmap(MapsActivity.arrow, new Rect(0,0,201,201), new Rect((int)(x-(height/2)), (int)(y-(height/2)), (int)(x+(height/2)), (int)(y+(height/2))), null);
                            tempCanvas.restore();
                        }
                    }
                } if(k>0&&nodes.get(path.get(k)).building == building.name && nodes.get(path.get(k)).floor == i &&(nodes.get(path.get(k-1)).building != building.name || nodes.get(path.get(k-1)).floor != i)&&(nodes.get(path.get(k+1)).building != building.name || nodes.get(path.get(k+1)).floor != i)){
                    tempCanvas.drawCircle((int) (ratio * (nodes.get(path.get(k)).x + tempFloor.ox)), (int) (ratio * (tempFloor.height - nodes.get(path.get(k)).y - tempFloor.oy)), 10, myPaint);
                }
            }
            if(nodes.get(path.get(path.size()-1)).building == building.name && nodes.get(path.get(path.size()-1)).floor == i &&(nodes.get(path.get(path.size()-2)).building != building.name || nodes.get(path.get(path.size()-2)).floor != i)) {
                tempCanvas.drawBitmap(MapsActivity.endMarkerBitmap, new Rect(0,0, 137, 201), new Rect((int)(ratio * (nodes.get(path.get(path.size()-1)).x + tempFloor.ox))-50, (int) (ratio * (tempFloor.height - nodes.get(path.get(path.size()-1)).y - tempFloor.oy))-145, (int)(ratio * (nodes.get(path.get(path.size()-1)).x + tempFloor.ox))+50, (int) (ratio * (tempFloor.height - nodes.get(path.get(path.size()-1)).y - tempFloor.oy))), null);
            }
            floorBitmaps.add(tempBitmap);
        }
    }

    public void setView(Building building1, int floorNum){
        if(building!=building1&&type.equals("path")) {
            building = building1;
            floorBitmaps.clear();
            createBitmaps();
        }

        floorBar.setMax(building.floors.size()-1);
        floorBar.setProgress(floorNum);
        floorText.setText(building.floors.get(floorNum).name);
        buildingText.setText(String.valueOf(building.name));
        String imageName = String.valueOf(building.name).toLowerCase()+floorNum;
        floorPlan.setImageResource(getResources().getIdentifier(imageName, "drawable",  getPackageName()));
        if(MapsActivity.choosingStart){
            setStart.setVisibility(View.VISIBLE);
            setStart.setEnabled(false);
            cancelStart.setVisibility(View.VISIBLE);
        }
        if(pin.getVisibility()==View.VISIBLE)
            setStart.setVisibility(View.VISIBLE);


        if(type.equals("path")) {
            pathProgress = Dijkstra.pathProgress;
            if(pathProgress!=0){
                backBuilding=Dijkstra.pathBuildings.get(pathProgress-1);
                backFloor=Dijkstra.pathFloors.get(pathProgress-1);
                backProgress=pathProgress-1;
            } else {
                backBuilding = Build.NUL;
                backFloor = -1;
            }
            if(pathProgress<Dijkstra.pathBuildings.size()-1){
                nextBuilding=Dijkstra.pathBuildings.get(pathProgress+1);
                nextFloor=Dijkstra.pathFloors.get(pathProgress+1);
                nextProgress=pathProgress+1;
            } else {
                nextBuilding = Build.NUL;
                nextFloor = -1;
            }

            pathImage.setVisibility(View.VISIBLE);
            pathImage.setImageDrawable(new BitmapDrawable(getResources(), floorBitmaps.get(floorNum)));
            back.setVisibility(View.VISIBLE);
            directions.setVisibility(View.INVISIBLE);
            if(backBuilding!=Build.NUL) {
                backText.setVisibility(View.VISIBLE);
                back.setEnabled(true);
                if(backBuilding!=Build.OUT)
                    backText.setText(backBuilding +" "+ buildings.get(backBuilding).floors.get(backFloor).name);
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
                    nextText.setText(nextBuilding +" "+ buildings.get(nextBuilding).floors.get(nextFloor).name);
                else
                    nextText.setText("Exterior");
            } else {
                nextText.setVisibility(View.INVISIBLE);
                next.setEnabled(true);
                next.setText("End");
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapsActivity.rating = true;
                        Dijkstra.pathProgress = Dijkstra.pathBuildings.size()-1;
                        finish();
                    }
                });
            }
            setStart.setVisibility(View.INVISIBLE);
            cancelStart.setVisibility(View.INVISIBLE);

        } else {
            pathImage.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            backText.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            nextText.setVisibility(View.INVISIBLE);
            directions.setVisibility(View.VISIBLE);
            if(dropPin.getText()=="Remove Pin"){
                pin.setVisibility(View.VISIBLE);
                directions.setEnabled(true);
            } else {
                pin.setVisibility(View.INVISIBLE);
                directions.setEnabled(false);
            }
            if(MapsActivity.choosingStart&&dropPin.getText()=="Remove Pin"){
                setStart.setVisibility(View.VISIBLE);
                cancelStart.setVisibility(View.VISIBLE);
                setStart.setEnabled(true);
            } else if(MapsActivity.choosingStart){
                setStart.setVisibility(View.VISIBLE);
                cancelStart.setVisibility(View.VISIBLE);
                setStart.setEnabled(false);
            } else {
                setStart.setVisibility(View.INVISIBLE);
                cancelStart.setVisibility(View.INVISIBLE);
                setStart.setEnabled(false);
            }
        }
    }

    public int getClosestNode(Build build, int floor){
        Enumeration<Integer> keys = nodes.keys();
        double distance2 = 1000000000;
        Node node;
        int n=0;
        int i;
        double xNode;
        double yNode;
        double x = buildings.get(build).floors.get(floor).width/2;
        x -= (double)mPosX*buildings.get(build).floors.get(floor).width/floorPlan.getMeasuredWidth();
        double y = buildings.get(build).floors.get(floor).height*(0.5+(initialY/floorPlan.getMeasuredHeight()));
        y -= (double)mPosY*buildings.get(build).floors.get(floor).height/floorPlan.getMeasuredHeight();
        while (keys.hasMoreElements()){
            i = keys.nextElement();
            node = nodes.get(i);
            if(node.building==build&&node.floor==floor){
                xNode = (nodes.get(i).x + buildings.get(build).floors.get(floor).ox);
                yNode = (buildings.get(build).floors.get(floor).height - nodes.get(i).y - buildings.get(build).floors.get(floor).oy);
                double dist = Math.pow(x - xNode, 2) + Math.pow(y - yNode, 2);
                if (dist < distance2) {
                    distance2  = dist;
                    n = node.n;
                }
            }
        }
        return n;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);
        build = getIntent().getStringExtra("building");
        floor = getIntent().getIntExtra("floor", -1);
        type = getIntent().getStringExtra("type");

        mScaleDetector = new ScaleGestureDetector(this, new Indoor.ScaleListener());
        floorPlan = findViewById(R.id.imageView2);
        pathImage = findViewById(R.id.imageView);
        buildings = CSVFile.getBuildings();
        nodes = CSVFile.getNodes();
        building = buildings.get(Build.valueOf(build));
        floorBar = findViewById(R.id.seekBar2);
        floorText = findViewById(R.id.textView4);
        buildingText = findViewById(R.id.textView2);
        back = findViewById(R.id.button3);
        backText = findViewById(R.id.textView3);
        next = findViewById(R.id.button);
        nextText = findViewById(R.id.textView);
        directions = findViewById(R.id.button7);
        directions.setEnabled(false);
        pin = findViewById(R.id.imageView4);
        pin.setVisibility(View.INVISIBLE);
        pinBtn = findViewById(R.id.imageButton7);
        dropPin = findViewById(R.id.textView7);
        dropPin.setText("Drop Pin");
        constraint = findViewById(R.id.constraint1);
        constraint.setVisibility(View.INVISIBLE);
        setStart = findViewById(R.id.button4);
        setStart.setVisibility(View.INVISIBLE);
        cancelStart = findViewById(R.id.button2);
        cancelStart.setVisibility(View.INVISIBLE);
        startMap = findViewById(R.id.button11);
        startMap.setVisibility(View.INVISIBLE);
        startLocation = findViewById(R.id.button10);
        startLocation.setVisibility(View.INVISIBLE);

        if(type.equals("path")) {
            createBitmaps();
            pinBtn.setVisibility(View.INVISIBLE);
            dropPin.setVisibility(View.INVISIBLE);
        }
        setView(building, floor);


        mPosX = (float) 6969696969.0;
        mPosY = (float) 6969696969.0;

        floorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar floor, int progress, boolean fromUser) {
                setView(building, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageButton goBack = (ImageButton) findViewById(R.id.imageButton);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.mode = "exit";
                finish();
            }
        });

        pinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dropPin.getText()=="Drop Pin"){
                    pin.setVisibility(View.VISIBLE);
                    setStart.setEnabled(true);
                    setStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MapsActivity.choosingStart = false;
                            MapsActivity.startNode = getClosestNode(building.name, floorBar.getProgress());
                            MapsActivity.startpath = true;
                            finish();
                        }
                    });
                    dropPin.setText("Remove Pin");
                    directions.setEnabled(true);
                    directions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MapsActivity.endNode = getClosestNode(building.name, floorBar.getProgress());
                            MapsActivity.choosingStart=true;
                            directions.setVisibility(View.INVISIBLE);
                            startMap.setVisibility(View.VISIBLE);
                            startLocation.setVisibility(View.VISIBLE);
                            startMap.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                            startLocation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //finish();
                                }
                            });
                        }
                    });
                } else {
                    pin.setVisibility(View.INVISIBLE);
                    dropPin.setText("Drop Pin");
                    directions.setEnabled(false);
                    setStart.setEnabled(false);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dijkstra.pathProgress = backProgress;
                if(backBuilding!=Build.OUT) {
                    setView(buildings.get(backBuilding), backFloor);
                    next.setText("Next");
                }else {
                    MapsActivity.boo = true;
                    finish();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dijkstra.pathProgress = nextProgress;
                if(next.getText()=="End"){
                    MapsActivity.rating = true;
                    finish();
                }else {
                    if (nextBuilding != Build.OUT)
                        setView(buildings.get(nextBuilding), nextFloor);
                    else {
                        MapsActivity.boo = true;
                        finish();
                    }
                }
            }
        });
    }



    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));

            floorPlan.setScaleX(mScaleFactor);
            floorPlan.setScaleY(mScaleFactor);
            pathImage.setScaleX(mScaleFactor);
            pathImage.setScaleY(mScaleFactor);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mPosX==(float)6969696969.0){
            mPosX = floorPlan.getX();
            mPosY = floorPlan.getY();
            initialY = mPosY;
        }

        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = ev.getActionIndex();
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        ev.findPointerIndex(mActivePointerId);

                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;
                //mPosX = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));
                //mPosY = Math.max(1.0f, Math.min(mScaleFactor, 5.0f));
                floorPlan.setX(mPosX);
                floorPlan.setY(mPosY);
                pathImage.setX(mPosX);
                pathImage.setY(mPosY);

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }
}