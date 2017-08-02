package com.amrayoub.eventyo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Amr Ayoub on 7/31/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "GoingEvents";

    // Contacts table name
    public static final String TABLE_CONTACTS = "Events";

    // Contacts Table Columns names
    private static final String KEY_ID = "event_id";
    private static final String KEY_USERID = "host_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_PHOTOURL = "photoUrl";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE "
                + TABLE_CONTACTS + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_USERID + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT,"
                + KEY_PHOTOURL + " TEXT"
                +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addEvent(Event_info event_info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, event_info.getmId()); // Event ID
        values.put(KEY_USERID, event_info.getmUserId()); // Event Host ID
        values.put(KEY_TITLE, event_info.getmTilte()); // Event Title
        values.put(KEY_CATEGORY, event_info.getmCategory()); // Event Category
        values.put(KEY_DESCRIPTION, event_info.getmDescription()); // Event Description
        values.put(KEY_LOCATION, event_info.getmLocation()); // Event Location
        values.put(KEY_DATE, event_info.getmDate()); // Event Date
        values.put(KEY_TIME, event_info.getmTime()); // Event Time
        values.put(KEY_PHOTOURL, event_info.getmPhotoUrl()); // Event PhotoUrl

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    public Event_info getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                        KEY_USERID, KEY_TITLE, KEY_CATEGORY,KEY_DESCRIPTION,KEY_LOCATION,KEY_DATE,KEY_TIME}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Event_info event_info = new Event_info(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8));
        // return event
        return event_info;
    }

    // Getting All Contacts
    public ArrayList<Event_info> getAllEvents() {
        ArrayList<Event_info> eventList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event_info event_info = new Event_info(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                        );
                // Adding contact to list
                eventList.add(event_info);
            } while (cursor.moveToNext());
        }

        // return contact list
        return eventList;
    }

    // Getting contacts Count
    public int getEventsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    // Updating single contact
    public int updateEvent(Event_info event_info) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, event_info.getmId()); // Event ID
        values.put(KEY_USERID, event_info.getmUserId()); // Event Host ID
        values.put(KEY_TITLE, event_info.getmTilte()); // Event Title
        values.put(KEY_CATEGORY, event_info.getmCategory()); // Event Category
        values.put(KEY_DESCRIPTION, event_info.getmDescription()); // Event Description
        values.put(KEY_LOCATION, event_info.getmLocation()); // Event Location
        values.put(KEY_DATE, event_info.getmDate()); // Event Date
        values.put(KEY_TIME, event_info.getmTime()); // Event Time
        values.put(KEY_PHOTOURL, event_info.getmPhotoUrl()); // Event photoUrl
        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(event_info.getmId()) });
    }

    // Deleting single contact
    public void deleteEvent(Event_info event_info) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(event_info.getmId()) });
        db.close();
    }
}
