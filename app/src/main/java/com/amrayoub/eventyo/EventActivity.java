package com.amrayoub.eventyo;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";

    ImageView event_photo;
    TextView event_category,event_description,event_location,event_date,event_time,userName;
    Button saveEvent;
    Event_info event_info;
    Boolean database=false,hideuser=false;
    CollapsingToolbarLayout collapsingToolbarLayout;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event);
        Intent intent= getIntent();
        event_info = (Event_info) intent.getSerializableExtra(getString(R.string.EventObject_Intent_Key));
        hideuser = intent.getBooleanExtra(getString(R.string.Going_Tab_Identifier_Key),false);
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.details_event_toolbar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button

        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        init();

    }

    private Boolean checkavailable(String id) {
        DatabaseHandler db = new DatabaseHandler(this);
        return db.getEvent(id);
    }

    private void init() {
        event_description = (TextView) findViewById(R.id.details_event_description);
        event_location= (TextView) findViewById(R.id.details_event_location);
        event_date= (TextView) findViewById(R.id.details_event_date);
        event_time= (TextView) findViewById(R.id.details_event_time);
        event_category = (TextView) findViewById(R.id.details_event_category);
        event_photo = (ImageView) findViewById(R.id.details_event_photo);
        userName = (TextView) findViewById(R.id.details_event_user);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.details_event_collapsing_toolbar);
        saveEvent = (Button) findViewById(R.id.details_button_saveEvent);
        Picasso.with(getBaseContext()).load(event_info.getmPhotoUrl()).into(event_photo);
        event_description.setText(event_info.getmDescription());
        event_category.setText(event_info.getmCategory());
        event_date.setText(event_info.getmDate());
        event_time.setText(event_info.getmTime());
        userName.setText(event_info.getmUserName());
        event_location.setText(event_info.getmLocation());
        collapsingToolbarLayout.setTitle(event_info.getmTitle());
        if (hideuser) {
            userName.setClickable(false);
            userName.setTextColor(Color.BLACK);
        }
        database = checkavailable(event_info.getmId());
        if(database == false){
            saveEvent.setText("Going");
        }
        else{
            saveEvent.setText("Not Going");
        }


    }
    public void details_event_going(View view) {
        DatabaseHandler db = new DatabaseHandler(this);
        if(!database){
            saveEvent.setText("Not Going");
            db.addEvent(event_info);
            Toast.makeText(this, getString(R.string.Save_Event_msg), Toast.LENGTH_SHORT).show();
            database = true;

        }else{
            database=false;
            saveEvent.setText("Going");
            db.deleteEvent(event_info);
            Toast.makeText(this, getString(R.string.Remove_Event_msg), Toast.LENGTH_SHORT).show();
        }
        //save event
    }

    public void openuserInfo(View view) {
        //open user activity
        mDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.Firebase_database_user_path));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User_info user = dataSnapshot.child(event_info.getmUserId()).getValue(User_info.class);
                Intent intent = new Intent(EventActivity.this,UserAccountActivity.class);
                intent.putExtra(getString(R.string.UserObject_Intent_Key),user);
                startActivity(intent);
            }
            @Override
            public void onCancelled(DatabaseError databaseError){
                Log.d(TAG, "onCancelled:" + databaseError.getMessage());
            }
        };
        mDatabase.addListenerForSingleValueEvent(valueEventListener);
    }
}
