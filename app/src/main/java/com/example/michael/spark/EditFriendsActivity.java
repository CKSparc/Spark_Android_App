package com.example.michael.spark;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.michael.spark.adapters.UserAdapter;
import com.example.michael.spark.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Edit Friends Activity - This is plane for now, but this is how we manage our friends list
 */
public class EditFriendsActivity extends Activity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_grid);
        ButterKnife.inject(this);

        toolbar.setTitle("Find Friends");

        Window window = EditFriendsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(EditFriendsActivity.this.getResources().getColor(R.color.colorPrimaryDark));

        /**
         *  ^ everything above is the normal toolbar and service bar set up and design ^
         */

        mGridView = (GridView)findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView)findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        /**
         * The edit friends activity is a grid view and we first make it empty until the first user is created
         * then is never null
         */
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        /**
         * On the grid view we place a check over the faces of user's who are already your friends, and if they are not they get a check
         *
         * Also checks can be removed at the same time, removing that friend from the users relation
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);

            /**
             * This if else just handles the where and when to place a check depending if the user decides to
             * add or remove a friend
             */
            if(mGridView.isItemChecked(position)) {
                //add friend
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
            }else {
                //remove friend
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }

            /**
             * Then it is saved in the background
             */
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
        });
        }
    };

    /**
     * This onResume method is used so the user can see the updated checks in real time
     */
    @Override
    protected void onResume() {
        super.onResume();

        /**
         * To do the adding and removing, we first get the user and [their relation] = [friends list]
         */
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        /**
         * This query is used to place all the users in a grid view, and all the ones checked are
         * the users friends.
         * The limit it desplays is 1000 user's at a time in ASCENDING order
         */
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    //success
                    mUsers = list;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    /**
                     * we get the list of all the users and we get all their names and store them in a list
                     */
                    for (ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;
                    }

                    if (mGridView.getAdapter() == null) {
                        /**
                         * if the grid view is null then we use a unique adapter to display the the user's in the grid view
                         */
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        /**
                         * The user adapter has a method the will refill the grid view if it isnt null
                         * with all of the updates
                         */
                        ((UserAdapter) mGridView.getAdapter()).refill(mUsers);
                    }
                    /**
                     * once all the friends are loaded in the view, we add all the checkmarks to the users
                     */
                    addFriendCheckmarks();
                } else {
                    /**
                     * we display an error message if the query goes wrong
                     */
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    /**
     * In this method I check the objectId of all the users friends with all the users in the database,
     * if its a match, we add a check and keep checking until we reached all the friends.
     */
    private void addFriendCheckmarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    // List returned - look for a match
                    for (int i = 0; i < mUsers.size(); i++){
                        ParseUser user = mUsers.get(i);
    // COULD BE MORE efficient
                        for (ParseUser friend: list) {
                            if(friend.getObjectId().equals(user.getObjectId())) {
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
