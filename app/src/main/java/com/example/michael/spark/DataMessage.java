package com.example.michael.spark;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.michael.spark.utils.ParseConstants;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Data message is the Activity the user goes to after selecting image / video to post
 * This activity adds a comment to a newly created post object
 */
public class DataMessage extends ActionBarActivity {

    protected EditText message;
    protected Button mButton;
    protected TextView mCharCount;

    protected Uri mMediaUri;
    protected String mFileType;


    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_message);
        ButterKnife.inject(this);

        toolbar.setTitle("Content Description");

        Window window = DataMessage.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(DataMessage.this.getResources().getColor(R.color.colorPrimaryDark));

        /**
         *  ^ everything above is the normal toolbar and service bar set up and design ^
         */

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        /**
         * These above 2 lines ^ are grabbing the file name and file, so we know where we are attaching the text too.
         */
        mCharCount = (TextView) findViewById(R.id.charCount);

        message = (EditText) findViewById(R.id.messageField);
        message.addTextChangedListener(mTextEditorWatcher);
        mButton = (Button) findViewById(R.id.post_button);

        /**
         * This button adds the text to the post the user wants to post
         */
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mMessageCheck = message.getText().toString().trim();
                /**
                 * First we check to make sure, the text is 140 character or less, if greater we show an error message
                 *
                 * Else we add it, and call the finish() to return to the parent activity. Which is the Main Activity
                 */
                if (mMessageCheck.length() > 140) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DataMessage.this);
                    builder.setTitle(R.string.error_title);
                    builder.setMessage("Description is too long!").setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Intent intent = new Intent(DataMessage.this, CourseToPostActivity.class);
                    intent.setData(mMediaUri);
                    intent.putExtra(ParseConstants.KEY_FILE_TYPE, mFileType);
                    intent.putExtra(ParseConstants.KEY_DATA_MESSAGE, mMessageCheck);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }


    protected final TextWatcher mTextEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        /**
         * This textWatcher is only used to change the color of the text once it reaches passed 140 characters
         * @param s
         * @param start
         * @param before
         * @param count
         */
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 140) {
                mCharCount.setTextColor(getResources().getColor(R.color.toolbar));
            }else {
                mCharCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
            mCharCount.setText(String.valueOf((140 - s.length())));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
