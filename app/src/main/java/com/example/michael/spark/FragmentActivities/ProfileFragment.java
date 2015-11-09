package com.example.michael.spark.FragmentActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.michael.spark.EditFriendsActivity;
import com.example.michael.spark.R;
import com.example.michael.spark.adapters.UserAdapter;
import com.example.michael.spark.utils.MD5Util;
import com.example.michael.spark.utils.ParseConstants;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Michael on 6/6/2015.
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected ImageView mImageView;
    protected TextView mUsername;
    protected TextView mEmail;
    protected TextView mNumberOfPosts;
    protected GridView mGridView;

    protected ProgressBar mProgressBar;
    protected RatingBar mRatingBar;

    protected FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_profile, container, false);

        mUsername = (TextView) rootView.findViewById(R.id.usernameField);
        mEmail = (TextView) rootView.findViewById(R.id.emailField);
        mNumberOfPosts = (TextView) rootView.findViewById(R.id.postNumber);
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        mRatingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        mImageView = (ImageView)rootView.findViewById(R.id.profilePic);

        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);
        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToListView(mGridView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditFriendsActivity.class));
            }
        });

        ParseUser userPic = ParseUser.getCurrentUser();
        String email = userPic.getEmail().toLowerCase();

        if (email.equals("")){
            mImageView.setImageResource(R.mipmap.avatar_empty);
        }else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash +"?s=204&d=404";

            Picasso.with(getActivity()).load(gravatarUrl).placeholder(R.mipmap.avatar_empty)
                    .into(mImageView);
        }


        ParseUser user = ParseUser.getCurrentUser();
        if(user != null) {
            mUsername.setText(user.getUsername());
            mEmail.setText(user.getEmail());
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    mFriends = list;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        //
                        ((UserAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
