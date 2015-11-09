package com.example.michael.spark.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.michael.spark.LoginActivity;
import com.example.michael.spark.R;
import com.example.michael.spark.draweractivities.FindCourses;
import com.example.michael.spark.utils.MD5Util;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Michael on 6/2/2015.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    protected static final int HEADER_TYPE = 0;
    protected static final int ROW_TYPE = 1;

    protected List<String> rows;
    protected Context context;

    public DrawerAdapter(Context context, List<String> rows) {
        this.rows = rows;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ParseUser user = ParseUser.getCurrentUser();
        if (viewType == HEADER_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.userName);
            ImageView imageView = (ImageView)view.findViewById(R.id.userIcon);

            textView.setText(user.getUsername());

            String email = user.getEmail().toLowerCase();

            if (email.equals("")){
                imageView.setImageResource(R.mipmap.avatar_empty);
            }else {
                String hash = MD5Util.md5Hex(email);
                String gravatarUrl = "http://www.gravatar.com/avatar/" + hash +"?s=204&d=404";

                Picasso.with(context).load(gravatarUrl).placeholder(R.mipmap.avatar_empty)
                        .into(imageView);
            }

            return new ViewHolder(view, viewType);
        } else if (viewType == ROW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false);

            return new ViewHolder(view, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.viewType == ROW_TYPE) {
            String rowText = rows.get(position - 1);
            holder.textView.setText(rowText);
            position = position - 1;
            switch (position) {
                case 0:
                    holder.imageView.setImageResource(R.mipmap.ic_action_view_as_list);
                    break;
                case 1:
                    holder.imageView.setImageResource(R.mipmap.ic_action_search);
                    break;
                case 2:
                    holder.imageView.setImageResource(R.mipmap.ic_action_email);
                    break;
                case 3:
                    holder.imageView.setImageResource(R.mipmap.ic_action_about);
                    break;
                case 4:
                    holder.imageView.setImageResource(R.mipmap.ic_action_settings);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return rows.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        }
        return ROW_TYPE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected int viewType;


        @InjectView(R.id.drawer_row_icon)
        ImageView imageView;
        @InjectView(R.id.drawer_row_text)
        TextView textView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if (viewType == ROW_TYPE) {
                ButterKnife.inject(this, itemView);
                textView.setOnClickListener(this);
                imageView.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {

            Intent intent;

            switch (getPosition()) {
                case 1:
                    break;
                case 2:
                    textView.setTextColor(v.getResources().getColor(R.color.text_color));
                    intent = new Intent(context, FindCourses.class);
                    context.startActivity(intent);
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    textView.setTextColor(v.getResources().getColor(R.color.text_color));
                    ParseUser.logOut();
                    intent = new Intent(context, LoginActivity.class);
                    /**
                     * This intent flag allows us to make sure we can not see the main activity from the
                     * log in screen if we use the back button
                     */
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    break;
            }
        }
    }
}
