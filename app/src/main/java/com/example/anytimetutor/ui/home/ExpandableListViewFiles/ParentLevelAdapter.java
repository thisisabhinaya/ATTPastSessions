package com.example.anytimetutor.ui.home.ExpandableListViewFiles;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anytimetutor.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParentLevelAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<String> mListDataHeader;
    String parentNode;
    private final Map<String, List<String>> mListData_SecondLevel_Map;
    private final Map<String, List<String>> mListData_ThirdLevel_Map;

    public ParentLevelAdapter(Context mContext, List<String> mListDataHeader, HashMap<String, List<String>> coursemap, HashMap<String, List<String>> subcoursemap) {
        this.mContext = mContext;
        this.mListDataHeader = new ArrayList<>();
        this.mListDataHeader.addAll(mListDataHeader);
        // Second LEVEL init
        String[] mItemHeaders;
        mListData_SecondLevel_Map = new HashMap<>();
        mListData_ThirdLevel_Map = new HashMap<>();
        int parentCount = mListDataHeader.size();
        for (int i = 0; i < parentCount; i++) {
            String content = mListDataHeader.get(i);

            //mListData_SecondLevel_Map.put(mListDataHeader.get(i), Arrays.asList(mItemHeaders));
            mListData_SecondLevel_Map.put(mListDataHeader.get(i), coursemap.get(content));
            Log.e("Ekdum new map ", String.valueOf(mListData_SecondLevel_Map));

            String[] mItemChildOfChild = coursemap.get(content).toArray(new String[coursemap.get(content).size()]);
            int childcount = mItemChildOfChild.length;
            for (int j = 0; j < childcount; j++) {
                String cont = mItemChildOfChild[j];
                mListData_ThirdLevel_Map.put(cont, subcoursemap.get(cont));
                Log.e("Ekdum new new new map ", String.valueOf(mListData_ThirdLevel_Map));
            }
        }

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final CustomExpListView secondLevelExpListView = new CustomExpListView(this.mContext);
        parentNode = (String) getGroup(groupPosition);
        Log.e("Parent Node ",parentNode);
        secondLevelExpListView.setAdapter(new SecondLevelAdapter(this.mContext, mListData_SecondLevel_Map.get(parentNode), mListData_ThirdLevel_Map, parentNode));
        secondLevelExpListView.setGroupIndicator(null);
        secondLevelExpListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    secondLevelExpListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
        return secondLevelExpListView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
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
            convertView = layoutInflater.inflate(R.layout.drawer_list_group, parent, false);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setTextColor(Color.parseColor("#3F51B5"));
        lblListHeader.setText(headerTitle);
        /*lblListHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Heading: "+parentNode+"\nTopic: "+headerTitle+"\nSubTopic:", Toast.LENGTH_SHORT).show();
            }
        });*/
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
