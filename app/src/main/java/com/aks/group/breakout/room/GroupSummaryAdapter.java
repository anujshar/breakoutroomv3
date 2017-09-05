package com.aks.group.breakout.room;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class GroupSummaryAdapter extends ArrayAdapter<GroupSummary> {
    public GroupSummaryAdapter(Context context, int resource, List<GroupSummary> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_group_summary, parent, false);
        }

        TextView groupNameTextView = (TextView) convertView.findViewById(R.id.groupNameTextView);
        TextView ownerNameTextView = (TextView) convertView.findViewById(R.id.ownerNameTextView);

        GroupSummary group = getItem(position);

        groupNameTextView.setVisibility(View.VISIBLE);
        groupNameTextView.setText(group.getGroupName());
        ownerNameTextView.setText(group.getOwnerName());

        return convertView;
    }
}
