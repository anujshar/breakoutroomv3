package com.aks.group.breakout.room;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MeetingsActivity extends AppCompatActivity {

    private Button mAddMeetingButton;
    private String groupUid;
    private FirebaseDatabase mFireDatabase;
    private DatabaseReference mEventsDatabaseReference;
    private ChildEventListener mChildEventListener;
    private EventsListAdapter mEventListAdapter;
    private ListView mEventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings);
        Intent intent = getIntent();
        groupUid = intent.getStringExtra("groupUid");
        mAddMeetingButton = (Button) findViewById(R.id.addMeetingButton);
        mAddMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetingsActivity.this, CreateMeetingActivity.class);
                intent.putExtra("groupUid", groupUid);
                MeetingsActivity.this.startActivity(intent);
            }
        });
        mEventListView = (ListView) findViewById(R.id.eventListView);
        mFireDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseReference = mFireDatabase.getReference().child("GroupData").child("Group_"+groupUid).child("Events");
        List<EventsData> allEvents = new ArrayList<>();
        mEventListAdapter = new EventsListAdapter(this, R.layout.item_events_list, allEvents);
        mEventListView.setAdapter(mEventListAdapter);
        attachDatabaseReadListener();
    }
    private void attachDatabaseReadListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    EventsData allEvents = dataSnapshot.getValue(EventsData.class);
                    mEventListAdapter.add(allEvents);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mEventsDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener()
    {
        if(mChildEventListener!=null) {
            mEventsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;

        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause(){
        super.onPause();
        detachDatabaseReadListener();
    }
}
