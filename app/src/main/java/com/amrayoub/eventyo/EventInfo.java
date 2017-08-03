package com.amrayoub.eventyo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Amr Ayoub on 7/22/2017.
 */

@IgnoreExtraProperties
public class EventInfo implements Serializable {
    public String mId;
    public String mUserId;
    public String mUserName;
    public String mTilte;
    public String mCategory;
    public String mDescription;
    public String mLocation;
    public String mDate;
    public String mTime;
    public String mPhotoUrl;


    public EventInfo() {
    }

    public EventInfo(String mId, String mUserId, String mUserName, String mTilte, String mCategory, String mDescription, String mLocation, String mDate, String mTime, String mPhotoUrl) {
        this.mId = mId;
        this.mUserId = mUserId;
        this.mUserName = mUserName;
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

    public String getmUserName() {
        return mUserName;
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

    public String getmTitle() {
        return mTilte;
    }

    public String getmTime() {
        return mTime;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }


}