package com.example.michael.spark;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.michael.spark.utils.ParseConstants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;

/**
 *  The sign up activity - How users are created
 */
public class SignUpActivity extends ActionBarActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;
    protected Button mCancelButton;

    protected String username;
    protected String password;
    protected String email;
    protected ArrayList<ParseObject> myCourses;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

// Setting the color of the service bar
        Window window = SignUpActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(SignUpActivity.this.getResources().getColor(R.color.colorPrimaryDark));

// initializing my variables
        myCourses = new ArrayList<>();
        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mEmail = (EditText)findViewById(R.id.emailField);

        mCancelButton = (Button)findViewById(R.id.cancel_button);
        /**
         * This cancel button returns users back to the login activity, since the login activity is a parent activity of
         * the sign up activity
         */
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSignUpButton = (Button)findViewById(R.id.signup_button);

        mSignUpButton.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                /**
                 * These next 3 line retrieves information from the user from the respective text fields
                 */
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
                email = mEmail.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();

                if(username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    /**
                     * if any text field is empty, we display a error message
                     */
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else  if(!email.contains("umd.edu")){
                    /**
                     * if the users email address is not UMD affiliated, we display another error message
                     */
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.login_wrong_email)
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    setSupportProgressBarIndeterminateVisibility(true);
                    // create new user
                    ParseUser newUser = new ParseUser();
                    /**
                     * if all requirements are fulfilled correctly then we create a user with the given traits
                     */
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put(ParseConstants.KEY_COURSE_USER, myCourses);
                    newUser.signUpInBackground(new SignUpCallback() {
                        /**
                         * Once the user is created we use a Parse method to sign them up
                         * @param e
                         */
                        @Override
                        public void done(ParseException e) {
                            setSupportProgressBarIndeterminateVisibility(false);

                            if (e == null) {
                                // success!
                                /**
                                 * If successful we load the new users profile onto the main activity with the necessary flags
                                 */
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                /**
                                 * if there is an error signing up new user, we will display an error message.
                                 * Most likely a user error
                                 */
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

    }
}
