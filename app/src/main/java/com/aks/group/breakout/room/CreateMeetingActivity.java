package com.aks.group.breakout.room;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class CreateMeetingActivity extends AppCompatActivity  {

    private EditText mFromDateEditText;
    private EditText mFromTimeEditText;
    private EditText mToDateEditText;
    private EditText mToTimeEditText;
    private Button mCancelButton;
    private Button mCreateButton;
    private EditText mEventName;
    private FirebaseDatabase mFireDatabase;
    private DatabaseReference mEventDatabaseReference;
    private String groupUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
        mFromDateEditText = (EditText) findViewById(R.id.fromDateEditText);
        mFromTimeEditText = (EditText) findViewById(R.id.fromTimeEditText);
        mToDateEditText = (EditText) findViewById(R.id.toDateEditText);
        mToTimeEditText = (EditText) findViewById(R.id.toTimeEditText);
        mCancelButton = (Button) findViewById(R.id.cancelButtonCreateMeeting);
        mCreateButton = (Button) findViewById(R.id.doneButtonCreateMeeting);
        mEventName = (EditText) findViewById(R.id.eventName);
        Intent intent = getIntent();
        groupUid = intent.getStringExtra("groupUid");


        mFireDatabase = FirebaseDatabase.getInstance();
        mEventDatabaseReference = mFireDatabase.getReference().child("GroupData").child("Group_"+groupUid).child("Events");


        Calendar cal = Calendar.getInstance();
        final EditTextDatePicker editFromTextDatePicker = new EditTextDatePicker(CreateMeetingActivity.this, mFromDateEditText, cal.getTimeInMillis() - 1000,
                0);
        final EditTextTimePicker editFromTextTimePicker = new EditTextTimePicker(CreateMeetingActivity.this, mFromTimeEditText);
        final EditTextDatePicker editToTextDatePicker = new EditTextDatePicker(CreateMeetingActivity.this, mToDateEditText, cal.getTimeInMillis() - 1000,
                0);
        final EditTextTimePicker editToTextTimePicker = new EditTextTimePicker(CreateMeetingActivity.this, mToTimeEditText);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CreateMeetingActivity.super.onBackPressed();
            }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEventName.getText()== null || mEventName.getText().toString().isEmpty())
                {
                    return;
                }

                createMeeting(mEventName.getText().toString(), editFromTextDatePicker.getDate(), editFromTextTimePicker.getTime(), editToTextDatePicker.getDate(), editToTextTimePicker.getTime());
            }
        });
    }

    private void createMeeting(String meetingName, String startDate, String startTime, String endDate, String endTime)
    {
        String guid = UUID.randomUUID().toString();
        EventsData event = new EventsData(guid, meetingName, startDate, startTime, endDate, endTime);
        mEventDatabaseReference.child("Event_" + guid).setValue(event);
        super.onBackPressed();
    }


}

