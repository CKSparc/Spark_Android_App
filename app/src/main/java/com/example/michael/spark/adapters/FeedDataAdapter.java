package com.example.michael.spark.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.michael.spark.R;
import com.example.michael.spark.ViewImageActivity;
import com.example.michael.spark.utils.MD5Util;
import com.example.michael.spark.utils.ParseConstants;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by Michael on 6/20/2015.
 */
public class FeedDataAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected List<ParseObject> mData;
    protected Uri mFile;

    protected ParseObject data;
    protected int likeNum;

    public FeedDataAdapter(Context context, List<ParseObject> data, Uri fileUri) {
        super(context, R.layout.feed_data_item, data);
        mContext = context;
        mData = data;
        mFile = fileUri;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.feed_data_item, null);
            holder = new ViewHolder();
            holder.mainDataVideo = (VideoView) convertView.findViewById(R.id.userDataVideo);
            holder.userIcon = (ImageView) convertView.findViewById(R.id.userIcon);
            holder.likeButton = (ImageView) convertView.findViewById(R.id.likeButton);
            holder.mainData = (ImageView) convertView.findViewById(R.id.userData);
            holder.commentButton = (ImageView) convertView.findViewById(R.id.commentButton);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.userName);
            holder.timeLabel = (TextView) convertView.findViewById(R.id.timeStamp);
            holder.likeCount = (TextView) convertView.findViewById(R.id.likeCount);
            holder.commentCount = (TextView) convertView.findViewById(R.id.commentCount);
            holder.dataMessage = (TextView) convertView.findViewById(R.id.dataMessage);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        data = mData.get(position);
try {
        if (data.isDataAvailable()) {
            // ------------------------------------------------------------------------------------
               likeNum = data.getInt(ParseConstants.KEY_LIKE_COUNT);
               final int commentNum = data.getInt(ParseConstants.KEY_COMMENT_COUNT);
                String dataType = data.getString(ParseConstants.KEY_FILE_TYPE);
                String message = data.getString(ParseConstants.KEY_DATA_MESSAGE);

            // ------------------------------------------------------------------------------------
                Date createdAt = data.getCreatedAt();
                long now = new Date().getTime();
                String convertedDate = DateUtils.getRelativeTimeSpanString(createdAt.getTime(),
                        now, DateUtils.SECOND_IN_MILLIS).toString();

                if (!convertedDate.equals("")) {
                    holder.timeLabel.setText(convertedDate);
                }
                holder.commentCount.setText(String.valueOf(commentNum));

                holder.nameLabel.setText(ParseUser.getCurrentUser().getUsername());

                holder.dataMessage.setText(message);

            // ------------------------------------------------------------------------------------
                holder.likeCount.setText(String.valueOf(likeNum));

                holder.likeCount.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        data.increment(ParseConstants.KEY_LIKE_COUNT);
                        data.saveInBackground();
                    }
                });
     // ------------------------------------------------------------------------------------
           if (dataType.equals(ParseConstants.TYPE_VIDEO)){
               holder.mainData.setImageURI(null);
               holder.mainDataVideo.setVideoURI(mFile);

               holder.mainDataVideo.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       // view video
                       Intent intent = new Intent(Intent.ACTION_VIEW, mFile);
                       intent.setDataAndType(mFile, "video/*");
                       mContext.startActivity(intent);
                   }
               });
           }else {
               Picasso.with(mContext).load(mFile.toString()).into(holder.mainData);

               holder.mainData.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent(getContext(), ViewImageActivity.class);
                       intent.setData(mFile);
                       mContext.startActivity(intent);
                   }
               });

           }
        }

        // ------------------------------------------------------------------------------------
            ParseUser userPic = ParseUser.getCurrentUser();
            String email = userPic.getEmail().toLowerCase();

            if (email.equals("")){
                holder.userIcon.setImageResource(R.mipmap.avatar_empty_data);
            }else {
                String hash = MD5Util.md5Hex(email);
                String gravatarUrl = "http://www.gravatar.com/avatar/" + hash +"?s=204&d=404";

                Picasso.with(mContext).load(gravatarUrl).placeholder(R.mipmap.avatar_empty_data)
                        .into(holder.userIcon);
            }

        // ------------------------------------------------------------------------------------

}catch (Exception e){
    // error
}

        return convertView;
    }

    private static class ViewHolder {
        VideoView mainDataVideo;
        ImageView userIcon;
        ImageView likeButton;
        ImageView mainData;
        ImageView commentButton;
        TextView nameLabel;
        TextView timeLabel;
        TextView likeCount;
        TextView commentCount;
        TextView dataMessage;
    }

    public void refill(List<ParseObject> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }
}
