package com.example.michael.spark;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
* View Image activity - activity used to display images
* */


public class ViewImageActivity extends ActionBarActivity {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.inject(this);

        toolbar.setTitle(R.string.app_name); // set the title for the toolbar

        /**
        * These next four lines are use to change the color of the service bar
        * */
        Window window = ViewImageActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ViewImageActivity.this.getResources().getColor(R.color.colorPrimaryDark));

        ImageView imageView = (ImageView)findViewById(R.id.imageView); // The view that the users image will be placed in

        Uri imageUri = getIntent().getData(); // retrieving th uri from an intent  - the uri from the image we want to view
        Picasso.with(this).load(imageUri.toString()).into(imageView); // using the Picasso api to load and view the image

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            } // This timer closes the image in 10 seconds, this is temporary will be removed
        }, 10 * 1000);
    }
}
