package com.example.securitymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import static android.view.MotionEvent.INVALID_POINTER_ID;


public class cby extends AppCompatActivity{
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private ScaleGestureDetector mScaleDetector;
    private ImageView floorPlan;
    private float mScaleFactor=1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("draw", "-1");
        setContentView(R.layout.activity_cby);
        Log.v("draw", "0");
        mScaleDetector = new ScaleGestureDetector(this, new cby.ScaleListener());
        floorPlan = (ImageView)findViewById(R.id.imageView2);
        mPosX = floorPlan.getX();
        mPosX = floorPlan.getY();
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        Log.d("draw", "1");
        //https://stackoverflow.com/questions/7501863/android-bitmapfactory-decoderesource-returning-null/32099786
        Bitmap myBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ste0_1);//, options);
        Paint myPaint = new Paint();
        myPaint.setColor(Color.WHITE);
        myPaint.setAntiAlias(true);
        myPaint.setStrokeWidth(1);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);

        float x1 = 10;
        float y1 = 10;
        float x2 = 20;
        float y2 = 20;
        Log.d("draw", "lol");
        //https://stackoverflow.com/questions/4918079/android-drawing-a-canvas-to-an-imageview
        //https://developer.android.com/reference/android/graphics/Bitmap
        Bitmap tempBitmap = Bitmap.createBitmap(392, 569, Bitmap.Config.RGB_565);
        Log.d("draw", "2");
        Log.d("draw", "4");
        Canvas tempCanvas = new Canvas(tempBitmap);
        Log.d("draw", "3");
        //https://developer.android.com/topic/performance/graphics
        Bitmap compBitMap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getScaledWidth(tempCanvas), myBitmap.getScaledHeight(tempCanvas), true);
//Draw the image bitmap into the canvas
        //tempCanvas.drawColor(Color.TRANSPARENT);
        Log.d("draw", "yo");
        tempCanvas.drawBitmap(compBitMap, null, new Rect(0, 0, 392, 569), null);

//Draw everything else you want into the canvas, in this example a rectangle with rounded edges
        tempCanvas.drawLine(x1, y1, x2, y2, myPaint);

//Attach the canvas to the ImageView
        floorPlan.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));






        SeekBar floor = (SeekBar) findViewById(R.id.seekBar2);
        final TextView floorText = (TextView)findViewById(R.id.textView4);
        floorText.setText("Floor 1 (Ground Level)");

        floor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar floor, int progress, boolean fromUser) {
                switch (floor.getProgress()) {
                    case 0:
                        floorPlan.setImageResource(R.drawable.cby0_1);// code block
                        floorText.setText("Basement");
                        break;
                    case 1:
                        floorPlan.setImageResource(R.drawable.cby1_1);// code block
                        floorText.setText("Floor 1 (Ground Level)");
                        break;
                    case 2:
                        floorPlan.setImageResource(R.drawable.cby2_1);// code block
                        floorText.setText("Floor 2");
                        break;
                    case 3:
                        floorPlan.setImageResource(R.drawable.cby3_1);// code block
                        floorText.setText("Floor 3");
                        break;
                    case 4:
                        floorPlan.setImageResource(R.drawable.cby4_1);// code block
                        floorText.setText("Floor 4");
                        break;
                    case 5:
                        floorPlan.setImageResource(R.drawable.cby5_1);// code block
                        floorText.setText("Floor 5");
                        break;
                    case 6:
                        floorPlan.setImageResource(R.drawable.cby6_1);// code block
                        floorText.setText("Floor 6");
                        break;
                    default:
                        // code block
                }
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