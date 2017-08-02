package com.amrayoub.eventyo;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * Created by Amr Ayoub on 8/1/2017.
 */

public class GoingEventsWidgetFactory implements RemoteViewsService.RemoteViewsFactory{
    private Context mContext;
    private Cursor mCursor;
    int mWidgetId;

    public GoingEventsWidgetFactory(Context context, Intent intent) {
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        //construct el cusrsor
        if (mCursor != null) {
            mCursor.close();
        }
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        String selectQuery = "SELECT  * FROM " + databaseHandler.TABLE_CONTACTS;
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        mCursor = db.rawQuery(selectQuery, null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        String title = mCursor.getString(2);
        String date =mCursor.getString(6);
        rv.setTextViewText(R.id.widget_event_title,title);
        rv.setTextViewText(R.id.widget_event_date,date);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("Widget", position);
        rv.setOnClickFillInIntent(R.id.widget_listitem, fillInIntent);
        //implement the cursor
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {return null;}

    @Override
    public int getViewTypeCount() {return 1;}

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public boolean hasStableIds() {return true;}
}
