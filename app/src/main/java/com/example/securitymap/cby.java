package com.example.securitymap;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class cby extends AppCompatActivity {
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private ScaleGestureDetector mScaleDetector;
    private ImageView floorPlan;
    private float mScaleFactor=1.0f;
    private String build;
    private Building building;
    private ArrayList<Build> buildNames;
    private ArrayList<Building> buildings;
    private ArrayList<Node> nodes;
    private ArrayList<Bitmap> floorBitmaps = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("draw", "-1");
        setContentView(R.layout.activity_cby);
        build = getIntent().getStringExtra("key");
        Log.v("draw", "0");
        mScaleDetector = new ScaleGestureDetector(this, new cby.ScaleListener());
        floorPlan = (ImageView)findViewById(R.id.imageView2);
        mPosX = floorPlan.getX();
        mPosX = floorPlan.getY();
        CSVFile csvFile = new CSVFile();
        buildNames = csvFile.getBuildNames();
        buildings = csvFile.getBuildings();
        nodes = csvFile.getNodes();
        Log.d("build", "allo");
        Log.d("build", build);
        Build b = Build.valueOf(build);
        building = buildings.get(buildNames.indexOf(b));

        //temporary:
        building.floors = new ArrayList<String>();
        building.floors.add("Basement");
        building.floors.add("Floor 1 (Ground Level)");
        building.floors.add("Floor 2");
        building.floors.add("Floor 3");
        building.floors.add("Floor 4");
        building.floors.add("Floor 5");


        SeekBar floor = (SeekBar) findViewById(R.id.seekBar2);
        floor.setMax(building.floors.size());
        final TextView floorText = (TextView)findViewById(R.id.textView4);
        floorText.setText(building.floors.get(1));
        final TextView buildingText = (TextView)findViewById(R.id.textView2);
        buildingText.setText(build);
        Paint myPaint = new Paint();
        myPaint.setColor(Color.RED);
        myPaint.setAntiAlias(true);
        myPaint.setStrokeWidth(3);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);

        for(int i=0; i<building.floors.size(); i++){
            String imageName = build.toString().toLowerCase()+i;
            int id = getResources().getIdentifier(imageName, "drawable",  getPackageName());
            floorPlan.setImageResource(id);
            Bitmap myBitmap = ((BitmapDrawable)floorPlan.getDrawable()).getBitmap();
            //Bitmap compBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getWidth()*2, myBitmap.getHeight()*2, true);
            Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth()*2, myBitmap.getHeight()*2, Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(myBitmap, null, new Rect(0, 0, myBitmap.getWidth()*2, myBitmap.getHeight()*2), null);
            //tempCanvas.drawLine(x1, y1, x2, y2, myPaint);
            floorBitmaps.add(tempBitmap);
        }


        floor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar floor, int progress, boolean fromUser) {
                floorPlan.setImageDrawable(new BitmapDrawable(getResources(), floorBitmaps.get(progress)));
                floorText.setText(building.floors.get(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageButton back = (ImageButton) findViewById(R.id.imageButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        //https://stackoverflow.com/questions/7501863/android-bitmapfactory-decoderesource-returning-null/32099786
        //https://stackoverflow.com/questions/15255611/how-to-convert-a-drawable-image-from-resources-to-a-bitmap  (alternative for drawable --> bitmap)
        //floorPlan.setImageResource(R.drawable.ste0_1);
        /*Bitmap myBitmap = ((BitmapDrawable)floorPlan.getDrawable()).getBitmap();
        //Bitmap myBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ste0_1);//, options);
        Paint myPaint = new Paint();
        myPaint.setColor(Color.RED);
        myPaint.setAntiAlias(true);
        myPaint.setStrokeWidth(3);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);

        float x1 = 10;
        float y1 = 10;
        float x2 = 60;
        float y2 = 60;
        //https://stackoverflow.com/questions/4918079/android-drawing-a-canvas-to-an-imageview
        //https://developer.android.com/reference/android/graphics/Bitmap
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth()*2, myBitmap.getHeight()*2, Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

        //https://developer.android.com/topic/performance/graphics
//Draw the image bitmap into the canvas
        tempCanvas.drawBitmap(myBitmap, null, new Rect(0, 0, myBitmap.getWidth()*2, myBitmap.getHeight()*2), null);
//Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        tempCanvas.drawLine(x1, y1, x2, y2, myPaint);
//Attach the canvas to the ImageView
        floorPlan.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));*/
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
                floorPlan.setY(mPosY);

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