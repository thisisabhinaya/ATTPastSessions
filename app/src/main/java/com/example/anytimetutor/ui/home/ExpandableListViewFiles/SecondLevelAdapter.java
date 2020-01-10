package com.example.anytimetutor.ui.home.ExpandableListViewFiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anytimetutor.R;
import com.example.anytimetutor.SearchTutor;

import java.util.List;
import java.util.Map;

public class SecondLevelAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<String> mListDataHeader;
    private final Map<String, List<String>> mListDataChild;
    String parentNode;
    String subject;

    public SecondLevelAdapter(Context mContext, List<String> mListDataHeader, Map<String, List<String>> mListDataChild, String parentNode) {
        this.mContext = mContext;
        this.mListDataHeader = mListDataHeader;
        this.mListDataChild = mListDataChild;
        this.parentNode = parentNode;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_item, parent, false);
        }
        final String subject = (String) getGroup(groupPosition);
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        txtListChild.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        txtListChild.setText(childText);
        txtListChild.setTextColor(Color.BLACK);
        txtListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Category: "+parentNode+"\nSubject: "+subject+"\nTopic:"+childText, Toast.LENGTH_SHORT).show();
                Intent in= new Intent(mContext, SearchTutor.class);
                in.putExtra("Category", parentNode);
                in.putExtra("Subject", subject);
                in.putExtra("Topic", childText);
                mContext.startActivity(in);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            return this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_group_second, parent, false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        lblListHeader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        lblListHeader.setTextColor(Color.parseColor("#3F51B5"));
        subject = headerTitle;
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
