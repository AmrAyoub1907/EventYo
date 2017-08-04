package com.amrayoub.eventyo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccountActivity extends AppCompatActivity {
    UserInfo userInfo;
    TextView name,email,phone,birthday,gender,job,overview;
    de.hdodenhof.circleimageview.CircleImageView photo;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.user_Account_toolbar);
        setSupportActionBar(myChildToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.ProgressDialogLoadingMsg));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);


        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra(getString(R.string.UserObject_Intent_Key));

        //initView
        name = (TextView) findViewById(R.id.user_Account_name);
        email = (TextView) findViewById(R.id.user_Account_email);
        phone = (TextView) findViewById(R.id.user_Account_phone);
        birthday = (TextView) findViewById(R.id.user_Account_birthday);
        gender = (TextView) findViewById(R.id.user_Account_gender);
        job = (TextView) findViewById(R.id.user_Account_job);
        overview = (TextView) findViewById(R.id.user_Account_overview);
        photo = (CircleImageView) findViewById(R.id.user_Account_photo);

        name.setText(userInfo.getmName());
        email.setText(userInfo.getmEmail());
        phone.setText(userInfo.getmPhone());
        birthday.setText(userInfo.getmBirthday());
        gender.setText(userInfo.getmGender());
        job.setText(userInfo.getmWorkinsgat());
        overview.setText(userInfo.getmOverview());
        if(userInfo.getmPhoto().equals(""))
            userInfo.mPhoto="//";

        Picasso.with(getBaseContext()).load(userInfo.getmPhoto())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(photo);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
