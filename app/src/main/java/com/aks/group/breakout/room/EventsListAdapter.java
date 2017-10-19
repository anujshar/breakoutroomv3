package com.aks.group.breakout.room;

import android.app.Activity;
import android.content.Context;
import android.provider.CalendarContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventsListAdapter extends ArrayAdapter<EventsData> {
    public EventsListAdapter(Context context, int resource, List<EventsData> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_events_list, parent, false);
        }

        TextView eventNameTextView = (TextView) convertView.findViewById(R.id.eventName);
        TextView eventDateTextView = (TextView) convertView.findViewById(R.id.eventDate);
        TextView eventTimeTextView = (TextView) convertView.findViewById(R.id.eventTime);

        EventsData event = getItem(position);

        eventNameTextView.setVisibility(View.VISIBLE);
        eventNameTextView.setText(event.getEventName());
        if(event.getStartDate() == event.getEndDate()) {
            eventDateTextView.setText(event.getStartDate());
        }
        else
        {
            eventDateTextView.setText(event.getStartDate() + " - " + event.getEndDate());
        }

        eventTimeTextView.setText(event.getStartTime() + " - " + event.getEndTime());

        return convertView;
    }
}
