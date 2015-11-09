package com.example.michael.spark;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.michael.spark.adapters.CourseAdapter;
import com.example.michael.spark.utils.FileHelper;
import com.example.michael.spark.utils.ParseConstants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Michael on 6/4/2015.
 */

/**
 * This activity lists all of the course the user is currently pointed too
 */
public class CourseToPostActivity extends ListActivity {
    public static final String TAG = CourseToPostActivity.class.getSimpleName();

    protected List<ParseObject> mMyCourses;
    protected ParseUser mCurrentUser;
    protected ListView mListView;
    protected List<String> mList;
    protected List<String> pList;
    protected List<String> objectId;


    protected Uri mMediaUri;
    protected String mFileType;
    protected String mMessage;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_course);
        ButterKnife.inject(this);

        toolbar.setTitle("Post In Course"); // setting the toolbars title

        /**
         * These next 3 lines are retrieving important information about the image or video trying to be posted
         */
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        mMessage = getIntent().getExtras().getString(ParseConstants.KEY_DATA_MESSAGE);

        // SETTING THE COLOR OF THE SERVICE BAR
        Window window = CourseToPostActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(CourseToPostActivity.this.getResources().getColor(R.color.colorPrimaryDark));

        mList = new ArrayList<>();
        objectId = new ArrayList<>();
        pList = new ArrayList<>();

        mListView = (ListView) findViewById(android.R.id.list);

        /**
         * mMyCourses is a list of Parse Objects. Courses are stored as Parse Objects.
         * so I get all the course objects associated with the current user.
         */
        mMyCourses = ParseUser.getCurrentUser().getList(ParseConstants.KEY_COURSE_USER);
        if (mMyCourses != null) { // TAKES CARE OF NULL POINTER EXCEPTIONS
            for (ParseObject obj : mMyCourses) {
                /**
                 * I then use a for each loop to get each course objects *[Object ID] and store then in an arraylist
                 */
                objectId.add(obj.getObjectId());
            }
        }

        for (String ids : objectId) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_UMD_COURSES);
            /**
             * Then I create a query to fetch data from the object
             */
            try {
                ParseObject obj = query.get(ids);
                /**
                 * Using the *[Object ID's] I retrieved earlier, I use the query to fetch that object from the database.
                 * Then I grab the course id and name from each object stored in the users column in Parse *[ My Courses ].
                 * Then add them to an arraylist.
                 */
                String id = obj.getString(ParseConstants.KEY_COURSE_ID);
                String name = obj.getString(ParseConstants.KEY_COURSE_NAME);

                mList.add(id);
                pList.add(name);
            } catch (com.parse.ParseException e) { // I USE A TRY BLOCK IN CASE OF A QUERY ERROR
                e.printStackTrace();
            }


        }

        /**
         * Then I set the view of the List view in the activity with the id and name of all the course the user is taking
         */
        mListView.setAdapter(new CourseAdapter(this, mList, pList));

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser(); // MAKING SURE WE HAVE THE CURRENT USER LOGGED IN WHEN ON THIS ACTIVITY - OPTIONAL
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        /**
         * When a course is selected, we create a data object which is the image or video with an optional comment.
         */
        ParseObject data = createData();
        /**
         * each data object gets tagged with the id of the course it was sent to.
         */
        data.put(ParseConstants.KEY_SEND_TO_COURSE, objectId.get(position));

        if (!data.isDataAvailable()){
            // error
            /**
             * If the data object failed to create successfully an error message is displayed
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_selecting_file).setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            /**
             * if data object was created successfully then we call the send method which post the data in the correct course
             * then we finish.
             * Since this activity is a child of main activity when we finish() it returns back to main activity
             */
            send(data);
            finish();
        }
    }

    /**
     * This method creates image/ video object the user is trying to post and returns a completed object
     * @return
     */
    protected ParseObject createData() {
        ParseObject data = new ParseObject(ParseConstants.CLASS_DATA);
        data.put(ParseConstants.KEY_SENDER_ID, mCurrentUser.getObjectId());
        data.put(ParseConstants.KEY_SENDER_NAME, mCurrentUser.getUsername());
        data.put(ParseConstants.KEY_FILE_TYPE, mFileType);
        data.put(ParseConstants.KEY_DATA_MESSAGE, mMessage);
        data.put(ParseConstants.KEY_LIKE_COUNT, 0);
        data.put(ParseConstants.KEY_COMMENT_COUNT, 0);
        data.put(ParseConstants.KEY_COMMENTS_FOR_DATA, new ArrayList<ParseObject>());
        /**
         * After we create data - Parse object, we give it all the default qualities along with the image / video we're posting
         */
        /**
         * The FileHelper API helps us reduce image size if user loads an image to large
         */
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if (fileBytes == null) {
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            data.put(ParseConstants.KEY_FILE, file);
            return data;
        }
    }

    /**
     * This method [send] posts the data in the correct course, well really is just saves it in the
     * background so we can view it later in the course listview
     * @param data
     */
    protected void send(ParseObject data){
        data.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    //success
                    Toast.makeText(CourseToPostActivity.this, R.string.success_message,
                            Toast.LENGTH_LONG).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CourseToPostActivity.this);
                    builder.setMessage(R.string.error_sending_file).setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
