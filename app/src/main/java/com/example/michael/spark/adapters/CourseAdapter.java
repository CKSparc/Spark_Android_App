package com.example.michael.spark.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.michael.spark.R;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Michael on 5/29/2015.
 */

/**
 * This is a custom adapter that takes in a display a list view
 */
public class CourseAdapter extends ArrayAdapter<String> {
    protected Context mContext;
    protected List<ParseObject> mMessages;
    protected List<String> mList;
    protected List<String> pList;
    protected ListAdapter mAdapter;

    /**
     * this is the constructor for my adapter:
     *
     *  The context is for
     *
     *
     * @param context
     * @param ids
     * @param names
     */
    public CourseAdapter(Context context, List<String> ids, List<String> names) {
        super(context, R.layout.course_item, ids);
        mContext = context;
        mList = ids;
        pList = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.course_item, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            holder.contDesc = (TextView) convertView.findViewById(R.id.contDesc);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

       // ParseObject message = mMessages.get(position);

        holder.nameLabel.setText(mList.get(position));
        try {
            holder.contDesc.setText(pList.get(position));
        }catch (Exception e){
            Log.e("Course Adapter", "Error "+ e.toString());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView nameLabel;
        TextView contDesc;
    }

    public void refill(List<String> messages) {
        mList.clear();
        mList.addAll(messages);
        notifyDataSetChanged();
    }


}
