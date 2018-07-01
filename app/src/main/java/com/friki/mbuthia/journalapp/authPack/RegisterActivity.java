
/**
 * Created by Mbuthia Peter on 25/06/2018.
 * As part of completion of 7 days of code challenge
 * In pursuit of the Google ALC nano degree program.
 * ALC With Google 3.0
 */

package com.friki.mbuthia.journalapp.authPack;

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

import com.friki.mbuthia.journalapp.JournalListActivity;
import com.friki.mbuthia.journalapp.MainActivity;
import com.friki.mbuthia.journalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    //Views and widgets fields
    EditText mEmailAddress;
    EditText mPassword;
    TextView mErrorMsg;
    ProgressBar mProgressBar;

    //Firebase Autentication fields
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    // Constant for logging
    private static final String TAG = RegisterActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setViews();

        //Assign instances
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    startActivity(new Intent(RegisterActivity.this, JournalListActivity.class));
                }else {

                }

            }
        };
    }

    private void setViews() {
        mEmailAddress = (EditText)findViewById(R.id.etEmail);
        mPassword = (EditText)findViewById(R.id.etPassword);
        mErrorMsg = (TextView)findViewById(R.id.tvErrorMsg);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    public void registerUser(View view) {
        String emailString = mEmailAddress.getText().toString().trim();
        String passwordString = mPassword.getText().toString().trim();

        if (!hasError(emailString,passwordString)){
            mProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, R.string.account_created , Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }else {
                        Toast.makeText(RegisterActivity.this, R.string.account_failuer, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        } else {
            mErrorMsg.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasError(String email, String password) {
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
            mErrorMsg.append("Email or Password is empty \n\n\n");
            return true;
        }
        if (password.length() < 4){
            mErrorMsg.append("Password is too short \n\n\n");
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
