package com.amrayoub.eventyo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Amr Ayoub on 7/22/2017.
 */

@IgnoreExtraProperties
public class User_info implements Serializable {
    public String mId;
    public String mName;
    public String mEmail;
    public String mPhone;
    public String mPhoto;
    public String mGender;
    public String mBirthday;
    public String mOverview;
    public String mWorkinsgat;

    public User_info(){}

    public User_info(String mId, String mName, String mEmail,String mPhone, String mPhoto, String mGender, String mBirthday, String mOverview, String mWorkinsgat) {
        this.mId = mId;
        this.mName = mName;
        this.mEmail = mEmail;
        this.mPhone = mPhone;
        this.mPhoto = mPhoto;
        this.mGender = mGender;
        this.mBirthday = mBirthday;
        this.mOverview = mOverview;
        this.mWorkinsgat = mWorkinsgat;
    }
    public String getmId() {return mId;}
    public String getmName() {return mName;}
    public String getmEmail() {return mEmail;}
    public String getmPhone() {return mPhone;}
    public String getmPhoto() {return mPhoto;}
    public String getmGender() {return mGender;}
    public String getmBirthday() {return mBirthday;}
    public String getmOverview() {return mOverview;}
    public String getmWorkinsgat() {return mWorkinsgat;}


}
