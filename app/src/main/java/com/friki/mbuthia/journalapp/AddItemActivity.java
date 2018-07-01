package com.friki.mbuthia.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.friki.mbuthia.journalapp.JournalViewModels.AddEntryViewModel;
import com.friki.mbuthia.journalapp.JournalViewModels.AddEntryViewModelFactory;
import com.friki.mbuthia.journalapp.database.JournalDatabase;
import com.friki.mbuthia.journalapp.database.JournalEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class AddItemActivity extends AppCompatActivity {
    // Extra for the entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";
    // Extra for the entry ID to be received after rotation
    public static final String INSTANCE_ENTRY_ID = "instanceEntryId";
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_ENTRY_ID = -1;
    // Constant for logging
    private static final String TAG = AddItemActivity.class.getSimpleName();

    // Field for views
    EditText mEditText;
    Button mButton;

    private int mEntryId = DEFAULT_ENTRY_ID;

    // Member variable for the Database
    private JournalDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        setViews();

        // get the database instance
        mDb = JournalDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)) {
            mEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENTRY_ID)) {
            mButton.setText(R.string.update_button);

                if (mEntryId == DEFAULT_ENTRY_ID) {

                mEntryId = intent.getIntExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);

                AddEntryViewModelFactory factory = new AddEntryViewModelFactory(mDb, mEntryId);
                final AddEntryViewModel viewModel = ViewModelProviders.of(this, factory).get(AddEntryViewModel.class);

                viewModel.getEntry().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getEntry().removeObserver(this);
                        populateUI(journalEntry);
                    }
                });
            }
        }
    }

    private void setViews() {
        mEditText = (EditText)findViewById(R.id.etJournalEntry);
        mButton = (Button)findViewById(R.id.btnSubmit);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournalItem();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_ENTRY_ID, mEntryId);
        super.onSaveInstanceState(outState);
    }

    // This method will be called to populate the UI when in update status
    private void populateUI(JournalEntry journalEntry) {
        if (journalEntry == null){
            return;
        }
        mEditText.setText(journalEntry.getEntry());
    }

    // Receives the onclick listener when the save button id pressed
    //It retrieves the user inputs and saves to the database
    public void saveJournalItem() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String journalString = mEditText.getText().toString();
        Date date = new Date();

        final JournalEntry entry = new JournalEntry(userId,journalString,date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mEntryId == DEFAULT_ENTRY_ID) {
                    // insert new entry
                    mDb.journalDao().insertEntry(entry);
                } else {
                    //update entry
                    entry.setId(mEntryId);
                    mDb.journalDao().updateEntry(entry);
                }
                finish();
            }
        });
    }
}
