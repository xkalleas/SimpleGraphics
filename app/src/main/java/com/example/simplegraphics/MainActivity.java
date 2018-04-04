package com.example.simplegraphics;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.frameLayout);

        ImageButton imagebutton = new ImageButton(MainActivity.this);

        // Add image path from drawable folder.
        imagebutton.setLayoutParams(new FrameLayout.LayoutParams(50, 50));
        imagebutton.setX(500);
        imagebutton.setY(500);
        imagebutton.setBackgroundColor(Color.BLUE);

        frameLayout.addView(imagebutton);


        imagebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "You have got it!", Toast.LENGTH_SHORT).show();
            }
        });

        PanAndZoomListener pzListener = new PanAndZoomListener(frameLayout, imagebutton);

        frameLayout.setOnTouchListener(pzListener);
        imagebutton.setOnTouchListener(pzListener);

    }
}
