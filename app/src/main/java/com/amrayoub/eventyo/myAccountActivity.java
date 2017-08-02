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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class myAccountActivity extends AppCompatActivity {
    de.hdodenhof.circleimageview.CircleImageView circleImageView,edit_circleImageView;
    RadioGroup radioGroup;
    TextView name,email,phone,birthday,gender,job,overview;
    EditText edit_name,edit_email,edit_phone,edit_job,edit_overview;
    TextView edit_birthday;
    ImageButton pickdate;
    RelativeLayout edit,no_edit;
    String edit_gender;
    User_info user_info;
    Boolean editmode=false;
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
        user_info = User_info_holder.getInput();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Updating Profile..");
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

        no_edit= (RelativeLayout) findViewById(R.id.relative_no_edit);
        edit_circleImageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.edit_myAccount_photo);
        edit_name = (EditText) findViewById(R.id.edit_myAccount_name);
        edit_email = (EditText) findViewById(R.id.edit_myAccount_email);
        edit_phone = (EditText) findViewById(R.id.edit_myAccount_phone);
        edit_birthday = (TextView) findViewById(R.id.edit_myAccount_birthday);
        edit_job = (EditText) findViewById(R.id.edit_myAccount_job);
        edit_overview = (EditText) findViewById(R.id.edit_myAccount_overview);
        pickdate = (ImageButton) findViewById(R.id.pick_birthday);

        name.setText(user_info.getmName());
        email.setText(user_info.getmEmail());
        phone.setText(user_info.getmPhone());
        birthday.setText(user_info.getmBirthday());
        gender.setText(user_info.getmGender());
        job.setText(user_info.getmWorkinsgat());
        overview.setText(user_info.getmOverview());
        Picasso.with(getBaseContext()).load(user_info.getmPhoto()).into(circleImageView);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    edit_gender = rb.getText().toString();
                }

            }
        });
    }

    public void editmyAccount(View view) {
        //editting account fab
        if(editmode){
            fab.setImageResource(R.drawable.ic_edit);
            editmode=true;
            no_edit.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            name.setText(user_info.getmName());
            email.setText(user_info.getmEmail());
            phone.setText(user_info.getmPhone());
            birthday.setText(user_info.getmBirthday());
            gender.setText(user_info.getmGender());
            job.setText(user_info.getmWorkinsgat());
            overview.setText(user_info.getmOverview());
            Picasso.with(getBaseContext()).load(user_info.getmPhoto()).into(circleImageView);

        }else{
            fab.setImageResource(R.drawable.ic_done_24dp);
            editmode=false;
            edit.setVisibility(View.VISIBLE);
            no_edit.setVisibility(View.GONE);
            if (chechallfeilds()) {
                mProgressDialog.show();
                //push to firebase;
                if (uri != null) {
                    StorageReference path = storage.child("Photos").child(user_info.getmId());
                    path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storage.child("Photos/"+user_info.getmId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    User_info user = new User_info(
                                            user_info.getmId(),
                                            edit_name.getText().toString(),
                                            edit_email.getText().toString(),
                                            edit_phone.getText().toString(),
                                            String.valueOf(uri),
                                            edit_gender,
                                            edit_birthday.getText().toString(),
                                            edit_overview.getText().toString(),
                                            edit_job.getText().toString()
                                    );
                                    mDatabase.child(getString(R.string.Firebase_database_user_path)).child(user_info.getmId()).setValue(user);
                                    User_info_holder.setInput(user);
                                    mProgressDialog.dismiss();
                                    Toast.makeText(myAccountActivity.this, "User Profile Updated", Toast.LENGTH_SHORT).show();
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
                    User_info user = new User_info(
                            user_info.getmId(),
                            edit_name.getText().toString(),
                            edit_email.getText().toString(),
                            edit_phone.getText().toString(),
                            user_info.getmPhoto(),
                            edit_gender,
                            edit_birthday.getText().toString(),
                            edit_overview.getText().toString(),
                            edit_job.getText().toString()
                    );
                    mDatabase.child(getString(R.string.Firebase_database_user_path)).child(user_info.getmId()).setValue(user);
                    User_info_holder.setInput(user);
                    mProgressDialog.dismiss();
                    Toast.makeText(myAccountActivity.this, "User Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                Toast.makeText(myAccountActivity.this, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void pick_birthday(View view) {
        //pick birthday
        pickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(myAccountActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edit_birthday.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //Toast.makeText(getBaseContext(), dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, Toast.LENGTH_SHORT).show();0
                            }
                        }, 1970, 1, 1);
                datePickerDialog.show();
            }
        });
    }
    private Boolean chechallfeilds() {
        String test1 = edit_name.getText().toString(),
                test2 = edit_email.getText().toString(),
                test3 = edit_phone.getText().toString(),
                test4 = edit_birthday.getText().toString(),
                test5 = edit_job.getText().toString(),
                test6 = edit_overview.getText().toString();
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
                    edit_circleImageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
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
