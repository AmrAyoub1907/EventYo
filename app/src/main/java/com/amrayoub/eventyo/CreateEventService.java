package com.amrayoub.eventyo;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by Amr Ayoub on 8/3/2017.
 */

public class CreateEventService extends IntentService {
    private DatabaseReference mDatabase;
    StorageReference storage;
    public CreateEventService() {
        super("CreateEventService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();

        final EventInfo event = (EventInfo) workIntent.getSerializableExtra(getString(R.string.EventObject_Intent_Key));

        Uri urii = Uri.parse(event.getmPhotoUrl());
        StorageReference path = storage.child(getString(R.string.Firebase_Photos_Path)).child(event.getmId());
        path.putFile(urii).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storage.child(getString(R.string.Firebase_Photos_Path)+"/"+event.getmId())
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        event.mPhotoUrl = String.valueOf(uri);
                        mDatabase.child(getString(R.string.Firebase_database_event_path))
                                .child(event.getmCategory()).child(event.getmId())
                                .setValue(event);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });
    }
}