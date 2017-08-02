package com.amrayoub.eventyo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccountActivity extends AppCompatActivity {
    User_info user_info;
    TextView name,email,phone,birthday,gender,job,overview;
    de.hdodenhof.circleimageview.CircleImageView photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.user_Account_toolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        user_info = (User_info) intent.getSerializableExtra(getString(R.string.UserObject_Intent_Key));

        //initView
        name = (TextView) findViewById(R.id.user_Account_name);
        email = (TextView) findViewById(R.id.user_Account_email);
        phone = (TextView) findViewById(R.id.user_Account_phone);
        birthday = (TextView) findViewById(R.id.user_Account_birthday);
        gender = (TextView) findViewById(R.id.user_Account_gender);
        job = (TextView) findViewById(R.id.user_Account_job);
        overview = (TextView) findViewById(R.id.user_Account_overview);
        photo = (CircleImageView) findViewById(R.id.user_Account_photo);

        name.setText(user_info.getmName());
        email.setText(user_info.getmEmail());
        phone.setText(user_info.getmPhone());
        birthday.setText(user_info.getmBirthday());
        gender.setText(user_info.getmGender());
        job.setText(user_info.getmWorkinsgat());
        overview.setText(user_info.getmOverview());
        Picasso.with(this).load(user_info.getmPhoto()).into(photo);
    }
}
