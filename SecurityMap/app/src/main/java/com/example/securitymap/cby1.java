package com.example.securitymap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class cby1 extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cby1);


        SeekBar floor = (SeekBar) findViewById(R.id.seekBar2);
        final ImageView floorPlan = (ImageView) findViewById(R.id.imageView2);
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
}