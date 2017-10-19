/*
package com.aks.group.breakout.room;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.drive.DriveScopes;

import com.google.api.services.drive.model.*;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private Button mDocsButton;
    private Button mCalButton;

    private String mUsername;
    private String mUserid;
    private String groupUid;
    private java.io.File mUploadFile;

    private FirebaseDatabase mFireDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int PICKFILE_RESULT_CODE = 1004;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { DriveScopes.DRIVE, "https://www.googleapis.com/auth/contacts", CalendarScopes.CALENDAR};



    public ChatActivity() {
    }
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Intent intent = getIntent();
        groupUid = intent.getStringExtra("groupUid");

        mUsername = ANONYMOUS;

        mFireDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mMessagesDatabaseReference = mFireDatabase.getReference().child("GroupData").child("Group_"+groupUid).child("Messages");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");


        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mDocsButton = (Button) findViewById(R.id.docButton);
        mCalButton = (Button) findViewById(R.id.calButton);

        // Initialize message ListView and its adapter
        List<FriendlyMessage> mMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, mMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fire an intent to show imagepicker
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //    intent.setType("image/jpeg");
                //    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, RC_PHOTO_PICKER);
            }
        });

        mDocsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDocsButton.setEnabled(false);
                // getResultsFromApi();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("**/
/*");
                startActivityForResult(intent,PICKFILE_RESULT_CODE);
                // CreateFolder("breakout room group chat");

                mDocsButton.setEnabled(true);
            }
        });

        mCalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalButton.setEnabled(false);

                Intent intent = new Intent(ChatActivity.this, MeetingsActivity.class);
                intent.putExtra("groupUid", groupUid);
                ChatActivity.this.startActivity(intent);

                mCalButton.setEnabled(true);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null, null, null);
                mMessagesDatabaseReference.push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });

        mUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        attachDatabaseReadListener();



        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener
                    (this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri photoUri = taskSnapshot.getDownloadUrl();
                            FriendlyMessage photoMessage = new FriendlyMessage(null, mUsername, photoUri.toString(), null, null);
                            mMessagesDatabaseReference.push().setValue(photoMessage);
                        }
                    });
        }

        switch(requestCode)
        {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(ChatActivity.this,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.",Toast.LENGTH_SHORT).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
            case PICKFILE_RESULT_CODE:
                if(resultCode == RESULT_OK)
                {
                    String filePath = data.getData().getPath();
                    Uri uri = data.getData();
                    mUploadFile = new java.io.File(getPath(ChatActivity.this, uri));
                    String fileName = mUploadFile.getName();
                    FriendlyMessage docMessage = new FriendlyMessage(null, mUsername, null, filePath, fileName);
                    mMessagesDatabaseReference.push().setValue(docMessage);
                    getResultsFromApi();

                    */
/*File fileMetadata = new File();
                    fileMetadata.setName("new file.pdf");
                    FileContent mediaContent = new FileContent("application/pdf", file);
                    try {
                        File file2 = getDriveService(mCredential).files().create(fileMetadata, mediaContent)
                                .setFields("id, parents")
                                .execute();
                    }
                    catch(IOException e1)
                    {

                    }*//*


                    Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();


                }
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    */
/**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     *//*

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    */
/**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     *//*

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    */
/**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     *//*

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    */
/**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     *//*

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }



    private String getFileName(Uri uri)
    {
        String fileName = null;
        Context context=getApplicationContext();
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            String[] proj = { MediaStore.Video.Media.TITLE };
            Uri contentUri = null;
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                cursor.moveToFirst();
                fileName = cursor.getString(columnIndex);
            }
        }
        return fileName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.sign_out_menu:
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        detachDatabaseReadListener();
        mMessageAdapter.clear();
    }

    @Override
    protected void onResume(){
        super.onResume();
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);

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
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void onSignedOutCleanup()
    {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void detachDatabaseReadListener()
    {
        if(mChildEventListener!=null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    //google drive api code
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(ChatActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        } else {
            new GetFilesListTask(mCredential).execute(false);
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ChatActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private String CreateFolder(String folderName)
    {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        String folderid = null;
        Drive mService = null;
        mService = getDriveService(mCredential);
        try {
            File file = mService.files().create(fileMetadata).setFields("id").execute();
            folderid = file.getId();
        }
        catch (IOException e1)
        {
            e1.getStackTrace();

        }
        return folderid;
    }

    private com.google.api.services.drive.Drive getDriveService(GoogleAccountCredential credential)
    {
        com.google.api.services.drive.Drive mService = null;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Breakout room service")
                .build();
        return mService;

    }

    private class GetFilesListTask extends AsyncTask<Boolean, Void, List<String>> {
        private com.google.api.services.drive.Drive mService = null;
        private Exception mLastError = null;

        GetFilesListTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.drive.Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Drive API Android Quickstart")
                    .build();
        }

        protected List<String> doInBackground(Boolean... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> fileInfo = new ArrayList<String>();
            FileList result = mService.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            if (files != null) {
                for (File file : files) {
                    fileInfo.add(String.format("%s (%s)\n",
                            file.getName(), file.getId()));
                }
            }

            //creating folder part
            */
/*File fileMetadata = new File();
            fileMetadata.setName("break out new");
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            Drive mService = null;
            mService = getDriveService(mCredential);
            try {
                File file = mService.files().create(fileMetadata).setFields("id").execute();
                String folderId = file.getId();
            }
            catch (IOException e1)
            {
                e1.getStackTrace();

            }*//*


            //Upload file part
            String folderId = "0B9Jv5YdikMcLcERsWVJqb2taUWs";
            File fileMetadata = new File();
            fileMetadata.setName("new file break.pdf");
            fileMetadata.setParents(Collections.singletonList(folderId));
            //java.io.File filePath = new java.io.File("files/photo.jpg");
            FileContent mediaContent = new FileContent("application/pdf", mUploadFile);
            File file = mService.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();

            return fileInfo;
        }

        @Override
        protected void onPreExecute() {

            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (output == null || output.size() == 0) {
                Toast.makeText(ChatActivity.this, "No results returned.", Toast.LENGTH_SHORT).show();
            } else {
                output.add(0, "Data retrieved using the Drive API:");
                Toast.makeText(ChatActivity.this, TextUtils.join("\n", output), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ChatActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(ChatActivity.this, "The following error occurred:\n"
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ChatActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
*/
