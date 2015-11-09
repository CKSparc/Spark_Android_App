package com.example.michael.spark;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.michael.spark.adapters.mPagerTabStripAdapter;
import com.example.michael.spark.draweractivities.FindCourses;
import com.example.michael.spark.utils.ParseConstants;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Michael on 6/2/2015.
 */

/**
 * The Main Activity is the Main Parent of the APP - The Brains and Heart of SPARK !!!
 */
public class MainActivity extends ActionBarActivity  {
    public static final String TAG = MainActivity.class.getSimpleName();

    protected TabHost tabHost;
    protected ParseUser currentUser;

   @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar) Toolbar toolbar;
     @InjectView(R.id.drawer_recyclerView) RecyclerView drawerRecyclerView;

    private ViewGroup mContainerToolbar;
    private ViewPager mPager;
    protected PagerTabStrip mTabStrip;
    private ActionBarDrawerToggle mDrawerToggle;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024*1024*10; // THIS EQUALS 10 MB

    /**
     * The above declerations and initializations are needed to keep track of certian things, which will be made clear later down
     */

    protected Uri mMediaUri;

    public static List<String> course;
    public static List<String> course_name;

    /**
     * This dialog interface is used to control the function of a drop down menu.
     * The Add a post button creates a drop down toggle and each case in the switch correlates to a specific
     * action about posting.
     */
    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:
                    // This case if for choosing to [Take a Picture]
                    Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if (mMediaUri == null) {
                        Toast.makeText(MainActivity.this, R.string.error_external_storage,
                                Toast.LENGTH_LONG).show();
                    }else {
                        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        startActivityForResult(takePicIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;
                case 1:
                    // This case if for choosing to [Take a Video]
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if (mMediaUri == null) {
                        //display error
                        Toast.makeText(MainActivity.this,R.string.error_external_storage,
                                Toast.LENGTH_LONG ).show();

                    }else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // a 0 for low quality videos because of storage space on Parse. 1 for high quality
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }
                    break;
                case 2:
                    // This case if for choosing to [Take a Picture from Gallery]
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;
                case 3:
                    // This case if for choosing to [Take a Video from Gallery]
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this,R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;
            }

            /**
             * The StartActivityForResult() method we be brought more to light further down.
             */
        }

        /**
         * This returns a Uri but creates a directory in the users phone, so every post that wasnt from
         * the user's Gallery is stored in a directory I create
         *
         * @param mediaType
         * @return
         */
        private Uri getOutputMediaFileUri(int mediaType) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            if (isExternalStorageAvailable()) {
                // get the URI

                // 1. Get the external storage directory
                String appName = MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir =
                        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                appName);

                // 2. Create our subdirectory
                if(! mediaStorageDir.exists()) {
                    if (! mediaStorageDir.mkdirs()) {
                        Log.e(TAG, "Fail to create directory");
                        return null;
                    }
                }

                // 3. Create a file name
                // 4. Create the file
                File mediaFile;
                Date now = new Date();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                String path = mediaStorageDir.getPath() + File.separator;
                if (mediaType == MEDIA_TYPE_IMAGE) {
                    mediaFile = new File(path + "IMG_" + timestamp + ".jpg" );
                }else if (mediaType == MEDIA_TYPE_VIDEO) {
                    mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                }else {
                    return null;
                }
                // 5. Return the file's URI
                return Uri.fromFile(mediaFile);
            }else {
                return null;
            }
        }

        /**
         * Method name is self-explanitory
         * @return
         */
        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();

            if(state.equals(Environment.MEDIA_MOUNTED)) {
                return true;
            }else {
                return false;
            }
        }
    };


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        ParseAnalytics.trackAppOpened(getIntent()); // Get data from our app

        /**
         * The Main is the First Activity that is loaded when app is opened. In the onCreate we first check if
         * a user is logged in. If not we load the login activity, if so we load the app with the current users data
         */
        currentUser = ParseUser.getCurrentUser();
        ParseSession.getCurrentSessionInBackground(new GetCallback<ParseSession>() {
            @Override
            public void done(ParseSession parseSession, ParseException e) {
                if (e == null) {
                    ParseUser.becomeInBackground(String.valueOf(parseSession), new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null){

                            }
                        }
                    });
                }
            }
        });
        if (currentUser == null) { // After fetching the current user, we check !
            navigateToLogin();
        } else {
            Log.i(TAG, currentUser.getUsername());
            setUpDrawer();

            /**
             * Below 4 line are for the service bar design
             */
            Window window = MainActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(MainActivity.this.getResources().getColor(R.color.colorPrimaryDark));


            List<String> rows = new ArrayList<>();
            rows.add("Dashboard");
            rows.add("Find Course");
            rows.add("Messages");
            rows.add("About");
            rows.add("Logout");

//           // DrawerAdapter drawerAdapter = new DrawerAdapter(this,rows);
//            drawerRecyclerView.setAdapter(drawerAdapter);
//            drawerRecyclerView.setHasFixedSize(true);
//            drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mPager = (ViewPager)findViewById(R.id.pager);
            mTabStrip = (PagerTabStrip) findViewById(R.id.tabstrip);
            mTabStrip.setBackgroundColor(getResources().getColor(R.color.toolbar));
            mTabStrip.setTabIndicatorColor(getResources().getColor(R.color.color_gold));
            mPager.setAdapter(new mPagerTabStripAdapter(this, getSupportFragmentManager()));

            course = new ArrayList<>();
            course_name = new ArrayList<>();

            course = getIntent().getStringArrayListExtra("course");
            course_name = getIntent().getStringArrayListExtra("course_name");
        }
    }

    public void setUpDrawer(){
        setSupportActionBar(toolbar);
    }

    /**
     * This method just takes us to the login activity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        /**
         * This intent flag allows us to make sure we can not see the main activity from the
         * log in screen if we use the back button
         */
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This method controls the toolbar menu buttons
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            /**
             * This first case, displays a list of camera upload choices
             */
            case R.id.new_comment_toolbar:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.search_course:
                /**
                 * This case sends the user to the search for course activity, where they can add a course to their class list
                 */
                Intent intent1 = new Intent(this, FindCourses.class);
                startActivity(intent1);
                break;
            case R.id.action_logout:
                /**
                 * This last option logs the user out of the application
                 */
                ParseQuery.clearAllCachedResults();
                currentUser.logOut();
               Intent intent = new Intent(this, LoginActivity.class);
                /**
                 * This intent flag allows us to make sure we can not see the main activity from the
                 * log in screen if we use the back button
                 */
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  This method handles the request of a picture or video from the user
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // CHECK TO MAKE SURE THE RESULT CODE IS OK TO GO!
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
                /**
                 * The request code tells us if the user is taking a picture or video
                 *
                 * Then we check the data, so if the user took a image or video.
                 */
                if (data == null) {
                    Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
                }else {
                    mMediaUri = data.getData();
                }

                if (requestCode == PICK_PHOTO_REQUEST) {
                    // make sure file size is less than 10MB
                    int fileSize = 0;
                    InputStream inputStream = null;

                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }catch (IOException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                        }
                    }
                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }else {
                /**
                 * This is for if the request code is for a video
                 */
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
            // select friend to send image or video too ...
            Intent recipientIntent = new Intent(this, DataMessage.class);
            recipientIntent.setData(mMediaUri);

            String fileType;
            if(requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
                fileType = ParseConstants.TYPE_IMAGE;
            }else {
                fileType = ParseConstants.TYPE_VIDEO;
            }
            recipientIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientIntent);

        }else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }

    }
}
