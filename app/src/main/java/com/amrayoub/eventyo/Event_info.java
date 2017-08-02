package com.amrayoub.eventyo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Amr Ayoub on 7/22/2017.
 */

@IgnoreExtraProperties
public class Event_info implements Serializable {
    public String mId;
    public String mUserId;
    public String mTilte;
    public String mCategory;
    public String mDescription;
    public String mLocation;
    public String mDate;
    public String mTime;
    public String mPhotoUrl;


    public Event_info() {
    }

    public Event_info(String mId,String mUserId, String mTilte,String mCategory, String mDescription, String mLocation, String mDate, String mTime,String mPhotoUrl) {
        this.mId = mId;
        this.mUserId = mUserId;
        this.mTilte = mTilte;
        this.mCategory=mCategory;
        this.mDescription = mDescription;
        this.mLocation = mLocation;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mPhotoUrl = mPhotoUrl;

    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmId() {
        return mId;
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmTilte() {
        return mTilte;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }


}