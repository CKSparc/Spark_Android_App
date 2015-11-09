package com.example.michael.spark.draweractivities;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.michael.spark.MainActivity;
import com.example.michael.spark.R;
import com.example.michael.spark.adapters.CourseAdapter;
import com.example.michael.spark.utils.ParseConstants;
import com.example.michael.spark.utils.ServiceHandler;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Michael on 6/7/2015.
 */
public class FindCourses extends ListActivity {

    protected List<ParseObject> mCourseObj;
    protected List<ParseObject> mListOfCourses;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mCourseRelation;
    protected ParseObject umd_courses;

    protected AutoCompleteTextView list_of_courses;
    protected ArrayAdapter adapter;

    protected Map<String,String> course_map;
    protected Map<String,String> object_ids;

    protected List<String> list_of_ids;
    protected List<String> list_of_names;

    protected ListView course_list;

    protected List<String> course;
    protected List<String> course_name;
    protected FloatingActionButton fab;

    protected String pos;
    protected List<ParseObject> myCourses;
    protected List<ParseObject> mOnClickCourse;

    protected ProgressDialog mDialog;

    private static String url = "http://api.umd.io/v0/courses/list";

    JSONArray courses = null;
    JSONObject obj;

    private static final String TAG_COURSE_ID = "course_id";
    private static final String TAG_NAME = "name";
    private static final String TAG_DEPARTMENT = "department";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.find_course);
            ButterKnife.inject(this);

            toolbar.setTitle("Find Course");

            Window window = FindCourses.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(FindCourses.this.getResources().getColor(R.color.colorPrimaryDark));

            mCurrentUser = ParseUser.getCurrentUser();

            list_of_courses = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
            course_list = (ListView) findViewById(android.R.id.list);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.attachToListView(course_list);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FindCourses.this, MainActivity.class);
                    startActivity(intent);
                }

            });

            umd_courses = new ParseObject(ParseConstants.KEY_UMD_COURSES);
            mListOfCourses = new ArrayList<>();


            list_of_ids = new ArrayList<>();
            list_of_names = new ArrayList<>();

            mCourseObj = new ArrayList<>();
            course  = new ArrayList<String>();
            course_name  = new ArrayList<String>();
            myCourses  = new ArrayList<>();
            mOnClickCourse  = new ArrayList<>();

            course_map = new HashMap<>();
            object_ids = new HashMap<>();


            obj = new JSONObject();

            new ActorsAsynTask().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();

    }

    public class ActorsAsynTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(FindCourses.this);
            mDialog.setMessage("Please wait...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ServiceHandler sh = new ServiceHandler();

          //  List<ParseObject> courseList = umd_courses.getList(ParseConstants.KEY_COURSE_LIST);

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {

                    courses = new JSONArray(jsonStr);

                    for (int i = 0; i < courses.length(); i++) {
                         obj = courses.getJSONObject(i);
                        String id = obj.getString(TAG_COURSE_ID);
                        String name = obj.getString(TAG_NAME);
                        list_of_ids.add(id);
                        list_of_names.add(name);
                        course_map.put(id, name);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if (mDialog.isShowing())
                mDialog.dismiss();

            adapter = new ArrayAdapter(FindCourses.this,android.R.layout.simple_dropdown_item_1line, list_of_ids);
            list_of_courses.setThreshold(1);
            list_of_courses.setAdapter(adapter);
            list_of_courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    pos = adapter.getItem(position).toString();

                    course.add(pos);
                    course_name.add(getKeyValue(pos));
                    String[] courses = new String[course.size()];
                    int i = 0;
                    for (String c : course) {
                        courses[i] = c;
                        i++;
                    }
                    String[] course_name1 = new String[course_name.size()];
                    int j = 0;
                    for (String c : course_name) {
                        course_name1[j] = c;
                        j++;
                    }
                    course_list.setAdapter(new CourseAdapter(getListView().getContext(), course, course_name));
                    fab.show();



                    ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_UMD_COURSES);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {
                                String foundObj = "";
                                int check = 0, check1 = 0;
                                final ParseObject course;
                                final List<ParseObject> myCourse = mCurrentUser.getList(ParseConstants.KEY_COURSE_USER);

                                for(ParseObject obj: list){

                                    if(obj.get(ParseConstants.KEY_COURSE_ID).equals(pos)) {
                                        check = 1;
                                        foundObj = pos;
                                        break;
                                    }
                                }

                                if (check == 0) {
                                    course = new ParseObject(ParseConstants.KEY_UMD_COURSES);
                                    course.put(ParseConstants.KEY_COURSE_ID, pos);
                                    course.put(ParseConstants.KEY_COURSE_NAME, getKeyValue(pos));
                                    course.put(ParseConstants.KEY_COURSE_DATA, myCourses);
                                    course.saveInBackground();

                                    myCourse.add(course);
                                    Toast.makeText(FindCourses.this, "Added to DB!", Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(FindCourses.this, "Exits in DB!", Toast.LENGTH_LONG).show();
                                }

                                if (!foundObj.equals("")) {
                                    for (ParseObject obj3 : myCourse) {
                                        if (obj3.getString(ParseConstants.KEY_COURSE_ID).equals(foundObj)) {
                                            check1 = 1;
                                            break;
                                        }
                                    }
                                }


                                if (check1 == 0){
                                    if (check == 0){
                                        mCurrentUser.put(ParseConstants.KEY_COURSE_USER, myCourse);
                                        Toast.makeText(FindCourses.this, "Course Added!", Toast.LENGTH_SHORT).show();
                                        mCurrentUser.saveInBackground();
                                    }else {
                                        /**
                                         * Query to find course to add to the users course section
                                         */
                                        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_DATA);
                                        query.whereEqualTo(ParseConstants.KEY_SEND_TO_COURSE, pos);
                                        query.findInBackground(new FindCallback<ParseObject>() {
                                            //--------------------------------------------------------------------------------------------------
                                            @Override
                                            public void done(List<ParseObject> list, ParseException e) {
                                                if (e == null) {
                                                    for (ParseObject obj: list){
                                                        if (obj.get(ParseConstants.KEY_COURSE_ID).equals(pos)){
                                                            myCourse.add(obj);
                                                            mCurrentUser.put(ParseConstants.KEY_COURSE_USER, myCourse);
                                                            Toast.makeText(FindCourses.this, "Course Added!", Toast.LENGTH_SHORT).show();
                                                            mCurrentUser.saveInBackground();
                                                        }
                                                    }
                                                }else {
                                                    /**
                                                     * ERROR !!
                                                     */
                                                }
                                            }
                                            //--------------------------------------------------------------------------------------------------
                                        });
                                    }

                                }else {
                                    Toast.makeText(FindCourses.this, "Course Exits!", Toast.LENGTH_SHORT).show();
                                }


                            }else {
                                // failure
                                Toast.makeText(FindCourses.this, "Error!!!", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                    query.clearCachedResult();
                }
            });
        }
    }

    public String getKeyValue(String item) {
       String val = "";
        val = course_map.get(item);
        return val;
    }
}

