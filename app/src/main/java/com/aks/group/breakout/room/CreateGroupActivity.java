package com.aks.group.breakout.room;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aks.group.breakout.room.dummy.DummyContent;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.people.v1.model.Name;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.Query;
import com.google.gdata.client.Service;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.NoLongerAvailableException;
import com.google.gdata.util.ServiceException;

import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

public class CreateGroupActivity extends AppCompatActivity implements ContactListFragment.OnListFragmentInteractionListener {

    private Button mCancelButton;
    private Button mDoneButton;
    private EditText mGroupNameEditText;

    private String mUserName;
    private String mUserid;
    private FirebaseDatabase mFireDatabase;
    private DatabaseReference mGroupDatabaseReference;

    private URL feedUrl;
    private ContactsService mContactService;
    GoogleAccountCredential mCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mCancelButton = (Button)findViewById(R.id.cancelButton);
        mDoneButton = (Button) findViewById(R.id.doneButton);
        mGroupNameEditText = (EditText) findViewById(R.id.createGroupName);
        mUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mFireDatabase = FirebaseDatabase.getInstance();
        mGroupDatabaseReference = mFireDatabase.getReference().child("GroupData");

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new GetFilesListTask(mCredential).execute();


                //CreateGroupActivity.super.onBackPressed();
            }
        });

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup(mGroupNameEditText.getText().toString());
                CreateGroupActivity.super.onBackPressed();
            }
        });

        // Enable Send button when there's text to send
        mGroupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mDoneButton.setEnabled(true);
                } else {
                    mDoneButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ArrayList <String> SCOPES = new ArrayList<String>();
        SCOPES.add("https://www.googleapis.com/auth/contacts");
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), (List<String>)SCOPES)
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    private void createGroup(String groupName)
    {
        String guid = UUID.randomUUID().toString();
        GroupsData groupData = new GroupsData(guid, groupName, mUserName, mUserid);
        mGroupDatabaseReference.child("Group_" + guid).setValue(groupData);
    }

    private void getContacts2()
    {
        String url = "https://www.google.com/m8/feeds/contacts/default/full";
        mContactService = new ContactsService("Breakout room v1");




        try{
            URL feedUrl = new URL(url);
            Query myQuery = new Query(feedUrl);
            myQuery.setMaxResults(1000);
            ContactFeed resultFeed = mContactService.query(myQuery, ContactFeed.class);
            List<ContactEntry> contactList = resultFeed.getEntries();
        }
        catch(IOException | ServiceException e)
        {

        }
    }

    private class GetFilesListTask extends AsyncTask<Void, Void, Void>
    {
        private PeopleService mService = null;
        private Exception mLastError = null;

        GetFilesListTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new PeopleService.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Drive API Android Quickstart")
                    .build();
        }

        protected Void doInBackground(Void... params)
        {
            try {
                getContacts();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {


        }



        @Override
        protected void onCancelled() {

        }


    }

    private void getContacts() throws IOException
    {
        PeopleService mService = getPeopleService(mCredential);
        ListConnectionsResponse response = mService.people().connections()
                .list("people/me")
                .setPageSize(10)
                .setPersonFields("names,emailAddresses")
                .execute();

        List<Person> connections = response.getConnections();
        if (connections != null && connections.size() > 0) {
            for (Person person : connections) {
                List<Name> names = person.getNames();
                if (names != null && names.size() > 0) {
                    System.out.println("Name: " + person.getNames().get(0)
                            .getDisplayName());
                } else {
                    System.out.println("No names available for connection.");
                }
            }
        } else {
            System.out.println("No connections found.");
        }
    }

    private PeopleService getPeopleService(GoogleAccountCredential credential)
    {
        PeopleService mService = null;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new PeopleService.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Breakout room service")
                .build();
        return mService;

    }



    //interface to interact with fragment
    public void onListFragmentInteraction(ContactInfo item)
    {

    }
}
