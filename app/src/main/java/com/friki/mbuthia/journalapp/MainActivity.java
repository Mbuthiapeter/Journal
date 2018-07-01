
/**
 * Created by Mbuthia Peter on 25/06/2018.
 * As part of completion of 7 days of code challenge
 * In pursuit of the Google ALC nano degree program.
 * ALC With Google 3.0
 */
package com.friki.mbuthia.journalapp;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.friki.mbuthia.journalapp.authPack.RegisterActivity;
import com.friki.mbuthia.journalapp.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;




public class MainActivity extends AppCompatActivity{

    // Views and widgets fields
    TextView mCreateUser;
    EditText mEmailAddress;
    EditText mPassword;

    private static final int SIGN_IN_CODE = 572;
    private static final String TAG = "MainActivity";

    private SignInButton mGoogleButton;
    ProgressBar mProgressBar;

    //Firebase Autentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();

        //Assign instances
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    startActivity(new Intent(MainActivity.this, JournalListActivity.class));
                }else {

                }
            }
        };

        configureSignIn();

        setOnclickListeners();


    }

    private void setOnclickListeners() {
        //Set Onclick Listener for the create user text view
        mCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        //Set Onclick Listener for google button
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils utils = new Utils(MainActivity.this);
                if (utils.isNetworkAvailable()){
                    signIn();
                }else {
                    Toast.makeText(MainActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setViews() {
        mCreateUser = (TextView)findViewById(R.id.tvCreateUser);
        mEmailAddress = (EditText)findViewById(R.id.etEmail);
        mPassword = (EditText)findViewById(R.id.etPassword);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mGoogleButton = (SignInButton) findViewById(R.id.login_with_google);
    }


    // Perform login operation with email and password
    public void signinWithEmail(View view) {
        String userEmailString = mEmailAddress.getText().toString().trim();
        String userPassword = mPassword.getText().toString().trim();
        mProgressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPassword)){
            mAuth.signInWithEmailAndPassword(userEmailString, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){

                        startActivity(new Intent(MainActivity.this, JournalListActivity.class));
                    }else{
                        Toast.makeText(MainActivity.this,"User login failed",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    // This handles the result of clicking the signIn button
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    /**
     * This method configures Google SignIn
     */
    public void configureSignIn(){
// request the user’s basic profile like name and email
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this,"You have an error in connection",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    // This method is called when the signIn button is clicked on the layout
    // It prompts the user to select a Google account.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_CODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null){
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
