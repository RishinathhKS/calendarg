package com.example.calendarg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.calendarg.adapter.EventListAdapter;
import com.example.calendarg.dialog.EventDialog;
import com.example.calendarg.model.ScheduledEvents;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private ImageButton mCallApiButton;
    private ImageButton scheduleMeeting;
    ProgressDialog mProgress;
    private List<ScheduledEvents> scheduledEventsList = new ArrayList<ScheduledEvents>();
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR };
    private ListView eventListView;
    private EventListAdapter eventListAdapter;
    static boolean flag=false;
    static boolean flag1=false;
    final int MYREQUEST = 11;
    Boolean issync=false;
    String sd;
    String ed;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
            View view =getSupportActionBar().getCustomView();

            if (savedInstanceState != null) {
                flag = savedInstanceState.getBoolean("flag");
            }

            SharedPreferences sp = getSharedPreferences("mycredentials",
                    Context.MODE_PRIVATE);
            issync = sp.getBoolean("sync",false);
            LinearLayout activityLayout = (LinearLayout) findViewById(R.id.calenderLayout);

            eventListView = (ListView)findViewById(R.id.eventList);
            checkFilePermissions();
            ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            ImageButton back = (ImageButton) findViewById(R.id.backToMain);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            mCallApiButton = (ImageButton) findViewById(R.id.syncEvents);
            mCallApiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallApiButton.setEnabled(false);
                    mOutputText.setText("");
                    getResultsFromApi();
                    mCallApiButton.setEnabled(true);

                }
            });

            scheduleMeeting = (ImageButton)findViewById(R.id.scheduleMeeting);
            scheduleMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventDialog eventDialog = new EventDialog();
                    eventDialog.show(getFragmentManager(), "dialog");

               /* Dialog dialog=new Dialog(CalendarActivity.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.create_event_layout);
                dialog.show();*/
                }
            });

            mOutputText = new TextView(this);
            mOutputText.setLayoutParams(tlp);
            mOutputText.setPadding(16, 16, 16, 16);
            mOutputText.setVerticalScrollBarEnabled(true);
            mOutputText.setMovementMethod(new ScrollingMovementMethod());
            // mOutputText.setText(
            //       "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
            activityLayout.addView(mOutputText);

            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Syncing with calendar..");

            setContentView(activityLayout);

            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            getResultsFromApi();
            if(issync) {
                sd = sp.getString("startdate","NA");
                ed = sp.getString("enddate","NA");
                adddevents(sd, ed);
            }
        }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving i value in bundle parameter(outState) obtained.
        outState.putBoolean("flag", flag);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            //retrieving i value from bundle parameter(savedInstanceState) obtained.
            flag = savedInstanceState.getBoolean("flag");
        }}

            @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate
                (R.menu.menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.ackcal:
                Intent inte = new Intent(this,
                        upload.class);
                startActivity(inte);break;

            case R.id.parsed_doc:

                Intent i = new Intent(this,
                        parsed_calendar.class);
                startActivityForResult(i,MYREQUEST);
        }
        return true;
    }

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
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
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
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
//            case MYREQUEST:
//
//                    if(resultCode == RESULT_OK) {
//                     flag1=true;
//                     Log.d("mainactivity",""+"flag"+flag+"flag1"+flag1);
//                     adddevents();
//                    }
//
//                break;
    }}


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

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
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

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

//    @Override
//    public void onClick(View view) {
//
//    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private com.google.api.services.calendar.Calendar mService = null;
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private Exception mLastError = null;
        private boolean FLAG = true;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;
                cancel(true);
                return null;
            }
            return null;
        }


        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private void getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(15)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            ScheduledEvents scheduledEvents;
            scheduledEventsList.clear();
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                scheduledEvents = new ScheduledEvents();
                scheduledEvents.setEventId(event.getId());
                scheduledEvents.setDescription(event.getDescription());
                scheduledEvents.setEventSummery(event.getSummary());
                scheduledEvents.setLocation(event.getLocation());
                scheduledEvents.setStartDate(start.toString());
                scheduledEvents.setEndDate("");
                StringBuffer stringBuffer = new StringBuffer();
                if(event.getAttendees()!=null) {
                    for (EventAttendee eventAttendee : event.getAttendees()) {
                        if(eventAttendee.getEmail()!=null)
                            stringBuffer.append(eventAttendee.getEmail() + "       ");
                    }
                    scheduledEvents.setAttendees(stringBuffer.toString());
                }
                else{
                    scheduledEvents.setAttendees("");
                }
                scheduledEventsList.add(scheduledEvents);
                System.out.println("-----"+event.getDescription()+", "+event.getId()+", "+event.getLocation());
                System.out.println(event.getAttendees());
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            System.out.println("--------------------"+scheduledEventsList.size());
            if (scheduledEventsList.size()<=0) {
                mOutputText.setText("No results returned.");
            } else {
                eventListAdapter = new EventListAdapter(MainActivity.this, scheduledEventsList);
                eventListView.setAdapter(eventListAdapter);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
    public void createEventAsync(final String summary, final String location, final String des, final DateTime startDate, final DateTime endDate) {

        new AsyncTask<Void, Void, String>() {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;
            private boolean FLAG = false;


            @Override
            protected String doInBackground (Void...voids){
                try {

                    insertEvent(summary, location, des, startDate, endDate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute (String s){
                super.onPostExecute(s);
                getResultsFromApi();
            }
        }.execute();
    }
    void insertEvent(String summary, String location, String des, DateTime startDate, DateTime endDate) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setDescription(des);
//        Event event = new Event()
//                .setSummary("Google I/O 2015")
//                .setDescription("A chance to hear more about Google's developer products.");


        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);




//        DateTime startData = new DateTime("2020-02-23");
//        EventDateTime st = new EventDateTime()
//                .setDate(startDat)
//                .setTimeZone("America/Los_Angeles");
//        //event1.setStart(startDat);

        //DateTime endDateTime = new DateTime("2020-02-22T17:00:00-07:00");

        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone("America/Los_Angeles");
       // event.setEndTimeUnspecified(false);
        event.setEnd(end);
        //event.setEndTimeUnspecified(Boolean.FALSE);

        String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));


        //event.setAttendees(Arrays.asList(eventAttendees));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        //event.send
        if(mService!=null)
            mService.events().insert(calendarId, event).setSendNotifications(true).execute();
       // mService.events().insert(calendarId, event1).setSendNotifications(true).execute();

    }
    public void adddevents(String stdate,String endate){
        String[] heading={"Date : ","Day: ","","(Hr.Sem UG & PG)","(IUG)","(IINTEGRATED)","(IPG)","(SPECIFICATIONS)"};
        try {
            //Log.d("mainactivity",""+"flag"+flag+"flag1"+flag1);
            //if(flag==false && flag1==true) {
                SQLiteDatabase db;
                db=openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);
                Cursor c = db.rawQuery("SELECT * FROM partable WHERE dat BETWEEN '"+stdate+"' AND '"+endate+"'" , null);
                while (c.moveToNext() && c.getString(0)!=null)
                {
                    if(c.getString(0)==null){
                        break;
                    }
                    if(isdate(c.getString(0))){
                        String startdate=c.getString(0)+"T10:00:00-07:00";
                        String enddate=c.getString(0)+"T17:00:00-07:00";
                        DateTime start = new DateTime(startdate);
                        DateTime end = new DateTime(enddate);
                        String des="";
                        for(int i=2;i<=7;i++) {
                            if (c.getString(i).trim() == "")
                                des = c.getString(i);
                            else
                                des = c.getString(i) + heading[i];

                            createEventAsync(des, "fas", des, start, end);
                        }
                        //flag=true;
                        //break;
                    }
                }
            SharedPreferences sp = getSharedPreferences
                    ("mycredentials", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("startdate","0000-00-00");
            edit.putString("enddate","0000-00-00");
            edit.putBoolean("sync",false);
            edit.commit();
            issync=false;

            //}
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isdate(String string) {
        try {
            if (string.length() == 0)
                return false;

            for (int i = 0; i < string.length(); i++) {
                int ascii = string.charAt(i);
                if (!((ascii >= 48 && ascii <= 58) || ascii == 45)) {
                    return false;
                }


            }
            String m = string.substring(5, 7);
            if (!(Integer.parseInt(m) > 0 && Integer.parseInt(m) < 13)) {
                return false;
            }
            String d = string.substring(8, 10);
            if (!(Integer.parseInt(d) > 0 && Integer.parseInt(d) < 32))
                return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d("tag", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
