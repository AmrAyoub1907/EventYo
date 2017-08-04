package com.amrayoub.eventyo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";
    private static final int RC_SIGN_IN=9001;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private String email,password, name ="", photoUrl ="", birthday ="", gender ="";
    private LoginButton loginButton;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgressDialog;
    UserInfo user_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication);
        setupWindowAnimations();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.ProgressDialogAuthMsg));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //facebook login
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btn_facebook_login);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mProgressDialog.show();
                Log.d(TAG, getString(R.string.FacebookLogOnSuccess) + loginResult);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                // Application code
                                try {
                                    String id = object.getString("id");
                                    URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                                    photoUrl = profile_pic.toString();
                                    name = object.getString("name");
                                    email = object.getString("email");
                                    gender = object.getString("gender");
                                    birthday = object.getString("birthday");// 01/31/1980 format
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, getString(R.string.FacebookLogOnCancel));
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, getString(R.string.FacebokkLogOnError), error);
                // ...
            }
        });

        //Google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.btn_google_login);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this,  getString(R.string.Google_SignIn_Failed), Toast.LENGTH_SHORT).show();
            }
            return;
        }

        //Facebook callback
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            checkFirebaseDatabase();
            startActivity(new Intent(AuthActivity.this,MainActivity.class));
            finish();
        }
    }
    public void signin(View view) {
        //Email & Password login

        EditText Email = (EditText) findViewById(R.id.email);
        email = Email.getText().toString();
        EditText Password = (EditText) findViewById(R.id.password);
        password = Password.getText().toString();
        if(email.length() == 0 || password.length() == 0){
            Toast.makeText(AuthActivity.this,getString(R.string.Fill_Email_Password), Toast.LENGTH_SHORT).show();
        }else{
            mProgressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, getString(R.string.SignInWithEmailLogOnComplete) + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Toast.makeText(AuthActivity.this,getString(R.string.FirebaseAuthFailed), Toast.LENGTH_SHORT).show();
                        }else{
                            checkFirebaseDatabase();
                        }
                    }
                });
        }
    }
    public void create_account_view(View view) {
        LinearLayout signinview= (LinearLayout) findViewById(R.id.signin_view);
        signinview.setVisibility(View.GONE);
        LinearLayout createAccountview= (LinearLayout) findViewById(R.id.create_account_view);
        createAccountview.setVisibility(View.VISIBLE);

    }
    public void cancelcreatingnewAccount(View view) {
        LinearLayout signinview= (LinearLayout) findViewById(R.id.signin_view);
        signinview.setVisibility(View.VISIBLE);
        LinearLayout createAccountview= (LinearLayout) findViewById(R.id.create_account_view);
        createAccountview.setVisibility(View.GONE);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG,getString(R.string.FirebaseAuthWithGoogleLog) + acct.getId());
        name = acct.getDisplayName();
        photoUrl = acct.getPhotoUrl().toString();
        email = acct.getEmail();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, getString(R.string.signInWithCredentialSuccessLog));
                            checkFirebaseDatabase();
                            //startActivity(new Intent(AuthActivity.this,MainActivity.class));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(AuthActivity.this, getString(R.string.FirebaseAuthFailed),
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }
    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, getString(R.string.signInWithCredentialSuccessLog));
                            checkFirebaseDatabase();
                            //startActivity(new Intent(AuthActivity.this,MainActivity.class));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            LoginManager.getInstance().logOut();
                            Toast.makeText(AuthActivity.this, getString(R.string.FirebaseAuthFailed), Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }
    public void create_new_account(View view) {
        EditText new_email = (EditText) findViewById(R.id.new_email);
        EditText new_username = (EditText) findViewById(R.id.new_username);
        EditText new_password = (EditText) findViewById(R.id.new_password);
        name = new_username.getText().toString();
        email = new_email.getText().toString();
        password = new_password.getText().toString();
        if(email.length() == 0 || password.length() < 0 || name.length() <0 ){
            Toast.makeText(AuthActivity.this,getString(R.string.Fill_Email_Password_Name), Toast.LENGTH_SHORT).show();
        }else{
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        mProgressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(AuthActivity.this,getString(R.string.FirebaseAuthFailed), Toast.LENGTH_SHORT).show();
                        }else {
                            LinearLayout signinview= (LinearLayout) findViewById(R.id.signin_view);
                            signinview.setVisibility(View.VISIBLE);
                            LinearLayout createAccountview= (LinearLayout) findViewById(R.id.create_account_view);
                            createAccountview.setVisibility(View.GONE);
                            Toast.makeText(AuthActivity.this, getString(R.string.FirebaseAuthSucced), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(1000);
        slide.setSlideEdge(Gravity.BOTTOM);
        getWindow().setExitTransition(slide);

        Slide slide2 = new Slide();
        slide2.setDuration(1000);
        slide2.setSlideEdge(Gravity.TOP);
        getWindow().setReenterTransition(slide2);
    }
    private void checkFirebaseDatabase(){
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        user_info = new UserInfo(
                currentUser.getUid()
                ,name
                ,email
                ,""
                , photoUrl
                ,gender
                ,birthday
                ,""
                ,"");
        mDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.Firebase_database_user_path));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(currentUser.getUid()))
                    mDatabase.child(currentUser.getUid()).setValue(user_info);
                else {
                    user_info = dataSnapshot.child(currentUser.getUid()).getValue(UserInfo.class);
                    UserInfoHolder.setInput(user_info);
                }
                mProgressDialog.dismiss();
                startActivity(new Intent(AuthActivity.this,MainActivity.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError){
                mProgressDialog.dismiss();
                Log.d(TAG, getString(R.string.FirebaseLogOnCancelld) + databaseError.getMessage());
            }
        };
        mDatabase.addListenerForSingleValueEvent(valueEventListener);
    }

}
