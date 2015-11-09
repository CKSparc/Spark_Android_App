package com.example.michael.spark.tabactivities;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import com.example.michael.spark.R;
import com.example.michael.spark.adapters.FeedDataAdapter;
import com.example.michael.spark.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Michael on 6/5/2015.
 */
public class FeedActivity extends ListActivity {

    protected List<ParseObject> mData;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected String objectId;
    protected Uri fileUri;

    protected ProgressDialog mDialog;

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.inject(this);

       String course = getIntent().getStringExtra("Course");
        objectId = getIntent().getStringExtra("ObjectId");
        toolbar.setTitle(course);

        Window window = FeedActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(FeedActivity.this.getResources().getColor(R.color.colorPrimaryDark));

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mDialog = new ProgressDialog(FeedActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
      //  retrieveMessages();

    }



    SwipeRefreshLayout.OnRefreshListener mOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                        retrieveMessages();

                }
            };

    @Override
    public void onResume() {
        super.onResume();
            retrieveMessages();
    }

    private void retrieveMessages() {

        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_DATA);
        query.whereEqualTo(ParseConstants.KEY_SEND_TO_COURSE, objectId);
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null){
                    mDialog.dismiss();
                    mDialog.hide();
                    mData = list;

                    String[] usernames = new String[mData.size()];
                    int i = 0;
                    for (ParseObject data : mData) {
                        usernames[i] = data.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                        String messageType = data.getString(ParseConstants.KEY_FILE_TYPE);
                        ParseFile file = data.getParseFile(ParseConstants.KEY_FILE);
                        fileUri = Uri.parse(file.getUrl());
                    }

                    if (getListView().getAdapter() == null) {
                        FeedDataAdapter adapter = new FeedDataAdapter(getListView().getContext(),
                                mData, fileUri);
                        setListAdapter(adapter);
                    }else {
                        // refill the adapter
                        ((FeedDataAdapter)getListView().getAdapter()).refill(mData);
                    }
                }else {
                    // we didnt find any message - ERROR
                }
            }
        });
    }
}
