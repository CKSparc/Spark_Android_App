package com.example.michael.spark.FragmentActivities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.michael.spark.R;
import com.example.michael.spark.adapters.CourseAdapter;
import com.example.michael.spark.tabactivities.FeedActivity;
import com.example.michael.spark.utils.ParseConstants;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 6/6/2015.
 */
public class CoursesFragment extends ListFragment {

    protected List<ParseObject> mCourses;
    protected ListView listView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected List<String> mList;
    protected List<String> pList;
    protected List<String> objectId;

    protected FloatingActionButton fab;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_courses, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        listView = (ListView)rootView.findViewById(android.R.id.list);

        mList = new ArrayList<>();
        objectId = new ArrayList<>();
        pList = new ArrayList<>();



       mCourses = ParseUser.getCurrentUser().getList(ParseConstants.KEY_COURSE_USER);
        if (mCourses != null) {
            for (ParseObject obj: mCourses){
                objectId.add(obj.getObjectId());
               }
            }

        for (String ids: objectId) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.KEY_UMD_COURSES);
            try {
                ParseObject obj = query.get(ids);

                String id = obj.getString(ParseConstants.KEY_COURSE_ID);
                String name = obj.getString(ParseConstants.KEY_COURSE_NAME);

                mList.add(id);
                pList.add(name);
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }


        }


        listView.setAdapter(new CourseAdapter(getActivity(), mList, pList));

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToListView(listView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Course");
                builder.setItems(R.array.my_courses, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        return rootView;
    }

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case 0:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 8:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
                case 9:
                    Toast.makeText(getActivity(), "Course Deleted!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener mOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // retrieveMessages();

                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    if(mCourses != null) {
                        String[] courses = new String[mList.size()];
                        int i = 0;
                            for (String c : mList) {
                                courses[i] = c;
                                i++;
                            }
                    }

                    listView.setAdapter(new CourseAdapter(getActivity(), mList, pList));


                }
            };

    @Override
    public void onResume() {
        super.onResume();

//        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                mList.remove(position);
//                pList.remove(position);
//
//                // mCourses.remove(objectId.get(position));
//
//                Toast.makeText(getActivity(), "Removed!", Toast.LENGTH_LONG).show();
//
//                return true;
//            }
//        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(),FeedActivity.class);
        intent.putExtra("Course", mList.get(position));
        intent.putExtra("ObjectId", objectId.get(position));
        startActivity(intent);
    }


}
