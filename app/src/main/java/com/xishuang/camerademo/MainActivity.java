package com.xishuang.camerademo;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    CameraGLSurfaceView glSurfaceView;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new CameraGLSurfaceView(this);
        setContentView(R.layout.activity_main);
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        TextView content = (TextView) findViewById(R.id.content);
        imageView = (ImageView) findViewById(R.id.image);
        container.addView(glSurfaceView);
//        setContentView(glSurfaceView);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "大佬", Toast.LENGTH_SHORT).show();
                glSurfaceView.readPixe(imageView);
            }
        });
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        glSurfaceView.onPause();
    }
}
