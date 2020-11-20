package com.example.securitymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
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
    private ImageView box;
    private ConstraintLayout constraint;

    public void createBitmaps(){
        Log.d("taggg", "beginning gay loop "+building.name);
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
                        Log.d("taggg", nodes.get(path.get(k)).building+", "+nodes.get(path.get(k)).floor);
                    }
                }
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
        floorBar.setMax(building.floors.size());
        floorBar.setProgress(floorNum);
        floorText.setText(building.floors.get(floorNum).name);
        buildingText.setText(String.valueOf(building.name));
        String imageName = String.valueOf(building.name).toLowerCase()+floorNum;
        floorPlan.setImageResource(getResources().getIdentifier(imageName, "drawable",  getPackageName()));


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
            if(pathProgress!=Dijkstra.pathBuildings.size()-1){
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
                backText.setText(backBuilding+" Floor "+backFloor);
            } else {
                backText.setVisibility(View.INVISIBLE);
                back.setEnabled(false);
            }

            next.setVisibility(View.VISIBLE);
            if(nextBuilding!=Build.NUL) {
                nextText.setVisibility(View.VISIBLE);
                next.setEnabled(true);
                nextText.setText(nextBuilding+" Floor "+nextFloor);
            } else {
                nextText.setVisibility(View.INVISIBLE);
                next.setEnabled(false);
            }

        } else {
            pathImage.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            backText.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            nextText.setVisibility(View.INVISIBLE);
            directions.setVisibility(View.VISIBLE);
        }
    }

    public int getClosestNode(Build build, int floor){
        Enumeration<Integer> keys = nodes.keys();
        double distance;
        Node node;
        int n=0;
        int i;
        //double x =
        while (keys.hasMoreElements()){
            i = keys.nextElement();
            node = nodes.get(i);
            if(node.building==build&&node.floor==floor){

                n = node.n;
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
        floorPlan = (ImageView)findViewById(R.id.imageView2);
        pathImage = (ImageView)findViewById(R.id.imageView);
        mPosX = floorPlan.getX();
        mPosY = floorPlan.getY();
        buildings = CSVFile.getBuildings();
        nodes = CSVFile.getNodes();
        building = buildings.get(Build.valueOf(build));
        floorBar = (SeekBar)findViewById(R.id.seekBar2);
        floorText = (TextView)findViewById(R.id.textView4);
        buildingText = (TextView)findViewById(R.id.textView2);
        back = (Button)findViewById(R.id.button3);
        backText = (TextView)findViewById(R.id.textView3);
        next = (Button)findViewById(R.id.button);
        nextText = (TextView)findViewById(R.id.textView);
        directions = (Button)findViewById(R.id.button7);
        directions.setEnabled(false);
        pin = (ImageView)findViewById(R.id.imageView4);
        pin.setVisibility(View.INVISIBLE);
        pinBtn = (ImageButton)findViewById(R.id.imageButton7);
        dropPin = (TextView) findViewById(R.id.textView7);
        dropPin.setText("Drop Pin");
        box = (ImageView) findViewById(R.id.imageView6);
        constraint = (ConstraintLayout) findViewById(R.id.constraint1);
        constraint.setVisibility(View.INVISIBLE);

        if(type.equals("path")) {
            createBitmaps();
            pinBtn.setVisibility(View.INVISIBLE);
            dropPin.setVisibility(View.INVISIBLE);
        }
        setView(building, floor);

        //Canvas canvas = ne
        //floorBitmaps[1]

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
                finish();
            }
        });

        pinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dropPin.getText()=="Drop Pin"){
                    pin.setVisibility(View.VISIBLE);
                    dropPin.setText("Remove Pin");
                    directions.setEnabled(true);
                    directions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else {
                    pin.setVisibility(View.INVISIBLE);
                    dropPin.setText("Drop Pin");
                    directions.setEnabled(false);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dijkstra.pathProgress=backProgress;
                setView(buildings.get(backBuilding), backFloor);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dijkstra.pathProgress = nextProgress;
                setView(buildings.get(nextBuilding), nextFloor);
            }
        });
    }



    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            Log.d("scale", String.valueOf(mScaleFactor));

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
                box.setX(mPosX);
                box.setY(mPosY);
                Log.d("mouvement", mPosX+", "+mPosY);
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