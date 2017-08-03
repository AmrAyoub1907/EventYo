package com.amrayoub.eventyo;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyAccountActivity extends AppCompatActivity {
    de.hdodenhof.circleimageview.CircleImageView circleImageView, editCircleImageView;
    RadioGroup radioGroup;
    TextView name,email,phone,birthday,gender,job,overview;
    EditText editName, editEmail, editPhone, editJob, editOverview;
    TextView editBirthday;
    ImageButton pickDate;
    RelativeLayout edit, noEdit;
    String editGender;
    UserInfo userInfo;
    Boolean editMode =false;
    FloatingActionButton fab;
    private DatabaseReference mDatabase;
    StorageReference storage;
    Uri uri = null;
    private static final int REQUEST_CODE = 1;
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.myAccount_toolbar);
        setSupportActionBar(myChildToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userInfo = UserInfoHolder.getInput();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.ProgressDialogProfileUpdatingMsg));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);


        init();
    }

    private void init() {
        circleImageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.myAccount_photo);
        fab = (FloatingActionButton) findViewById(R.id.myAccount_fab);
        name = (TextView) findViewById(R.id.myAccount_name);
        email = (TextView) findViewById(R.id.myAccount_email);
        phone = (TextView) findViewById(R.id.myAccount_phone);
        birthday = (TextView) findViewById(R.id.myAccount_birthday);
        gender = (TextView) findViewById(R.id.myAccount_gender);
        job = (TextView) findViewById(R.id.myAccount_job);
        overview = (TextView) findViewById(R.id.myAccount_overview);
        edit= (RelativeLayout) findViewById(R.id.relative_edit);

        noEdit = (RelativeLayout) findViewById(R.id.relative_no_edit);
        editCircleImageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.edit_myAccount_photo);
        editName = (EditText) findViewById(R.id.edit_myAccount_name);
        editEmail = (EditText) findViewById(R.id.edit_myAccount_email);
        editPhone = (EditText) findViewById(R.id.edit_myAccount_phone);
        editBirthday = (TextView) findViewById(R.id.edit_myAccount_birthday);
        editJob = (EditText) findViewById(R.id.edit_myAccount_job);
        editOverview = (EditText) findViewById(R.id.edit_myAccount_overview);
        pickDate = (ImageButton) findViewById(R.id.pick_birthday);

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
                .into(circleImageView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    editGender = rb.getText().toString();
                }

            }
        });
    }

    public void editmyAccount(View view) {
        //editting account fab
        if(editMode){
            fab.setImageResource(R.drawable.ic_edit);
            editMode =true;
            noEdit.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            name.setText(userInfo.getmName());
            email.setText(userInfo.getmEmail());
            phone.setText(userInfo.getmPhone());
            birthday.setText(userInfo.getmBirthday());
            gender.setText(userInfo.getmGender());
            job.setText(userInfo.getmWorkinsgat());
            overview.setText(userInfo.getmOverview());
            Picasso.with(getBaseContext()).load(userInfo.getmPhoto()).into(circleImageView);

        }else{
            fab.setImageResource(R.drawable.ic_done_24dp);
            editMode =false;
            edit.setVisibility(View.VISIBLE);
            noEdit.setVisibility(View.GONE);
            if (chechallfeilds()) {
                mProgressDialog.show();
                //push to firebase;
                if (uri != null) {
                    StorageReference path = storage.child(getString(R.string.Firebase_Photos_Path)).child(userInfo.getmId());
                    path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storage.child(getString(R.string.Firebase_Photos_Path)+"/"+ userInfo.getmId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserInfo user = new UserInfo(
                                            userInfo.getmId(),
                                            editName.getText().toString(),
                                            editEmail.getText().toString(),
                                            editPhone.getText().toString(),
                                            String.valueOf(uri),
                                            editGender,
                                            editBirthday.getText().toString(),
                                            editOverview.getText().toString(),
                                            editJob.getText().toString()
                                    );
                                    mDatabase.child(getString(R.string.Firebase_database_user_path)).child(userInfo.getmId()).setValue(user);
                                    UserInfoHolder.setInput(user);
                                    mProgressDialog.dismiss();
                                    Toast.makeText(MyAccountActivity.this, getString(R.string.User_Profile_Updated_msg), Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    });

                }else{
                    UserInfo user = new UserInfo(
                            userInfo.getmId(),
                            editName.getText().toString(),
                            editEmail.getText().toString(),
                            editPhone.getText().toString(),
                            userInfo.getmPhoto(),
                            editGender,
                            editBirthday.getText().toString(),
                            editOverview.getText().toString(),
                            editJob.getText().toString()
                    );
                    mDatabase.child(getString(R.string.Firebase_database_user_path)).child(userInfo.getmId()).setValue(user);
                    UserInfoHolder.setInput(user);
                    mProgressDialog.dismiss();
                    Toast.makeText(MyAccountActivity.this,  getString(R.string.User_Profile_Updated_msg), Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                Toast.makeText(MyAccountActivity.this, getString(R.string.Fill_Feilds_msg), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void pick_birthday(View view) {
        //pick birthday
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MyAccountActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                editBirthday.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //Toast.makeText(getBaseContext(), dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, Toast.LENGTH_SHORT).show();0
                            }
                        }, 1970, 1, 1);
                datePickerDialog.show();
            }
        });
    }
    private Boolean chechallfeilds() {
        String test1 = editName.getText().toString(),
                test2 = editEmail.getText().toString(),
                test3 = editPhone.getText().toString(),
                test4 = editBirthday.getText().toString(),
                test5 = editJob.getText().toString(),
                test6 = editOverview.getText().toString();
        if (test1.equals("")
                || test2.equals("")
                || test3.equals("")
                || test4.equals("")
                || test5.equals("")
                || test6.equals("")) {
            return false;
        }
        return true;
    }

    public void upload_profile_photo(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();

            //String filepathstr=uri.toString();
            File file = new File(uri.toString());
            float length = file.length() / 1048576; // Size in Mbyte

            if (length < 10) {
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(uri);
                    //ImageView imageView = (ImageView) findViewById(R.id.myAccount_photo);
                    editCircleImageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                } catch (FileNotFoundException e) {
                    // Handle the error
                } finally {
                    if (imageStream != null) {
                        try {
                            imageStream.close();
                        } catch (IOException e) {
                            // Ignore the exception
                        }
                    }
                }
            }
        }
    }
}
