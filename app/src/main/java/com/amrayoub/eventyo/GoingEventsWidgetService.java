package com.amrayoub.eventyo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViewsService;

public class GoingEventsWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GoingEventsWidgetFactory(getApplicationContext(), intent);
    }
}
