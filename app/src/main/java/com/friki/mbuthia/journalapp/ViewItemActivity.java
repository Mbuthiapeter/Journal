package com.friki.mbuthia.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.friki.mbuthia.journalapp.JournalViewModels.AddEntryViewModel;
import com.friki.mbuthia.journalapp.JournalViewModels.AddEntryViewModelFactory;
import com.friki.mbuthia.journalapp.database.JournalDatabase;
import com.friki.mbuthia.journalapp.database.JournalEntry;
import com.friki.mbuthia.journalapp.databinding.ActivityViewItemBinding;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ViewItemActivity extends AppCompatActivity {
    // Extra for the entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";
    // Extra for the entry ID to be received after rotation
    public static final String INSTANCE_ENTRY_ID = "instanceEntryId";
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_ENTRY_ID = -1;
    // Constant for date format
    private static final String DATE_FORMAT = "EEE, d MMM yyyy";
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // Class variables for the List that holds task data and the Context
    private List<JournalEntry> mJournalEntries;
    private JournalEntry entryItem;

    private int mEntryId;

    //Binding Instance
    ActivityViewItemBinding mBinding;

    // Member variable for the Database
    private JournalDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        //Binding data to views
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_item);

        // get the database instance
        mDb = JournalDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)) {
            mEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENTRY_ID)){
            mEntryId = intent.getIntExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        AddEntryViewModelFactory factory = new AddEntryViewModelFactory(mDb, mEntryId);
        final AddEntryViewModel viewModel = ViewModelProviders.of(this, factory).get(AddEntryViewModel.class);
        viewModel.getEntry().observe(this, new Observer<JournalEntry>() {
            @Override
            public void onChanged(@Nullable JournalEntry journalEntry) {
                viewModel.getEntry().removeObserver(this);
                displayJournalItem(journalEntry);
            }
        });

        TextView mItem = (TextView)findViewById(R.id.tvJournalEntry);
        mItem.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_edit:
                // Launch AddTaskActivity adding the itemId as an extra in the intent
                Intent intent = new Intent(this, AddItemActivity.class);
                intent.putExtra(AddItemActivity.EXTRA_ENTRY_ID, mEntryId);
                startActivity(intent);
                return true;

            case R.id.item_delete:
                AddEntryViewModelFactory factory = new AddEntryViewModelFactory(mDb, mEntryId);
                final AddEntryViewModel viewModel = ViewModelProviders.of(this, factory).get(AddEntryViewModel.class);
                viewModel.getEntry().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getEntry().removeObserver(this);
                        deleteItem(journalEntry);
                    }
                });

        }

        return super.onOptionsItemSelected(item);
    }

    private void displayJournalItem(JournalEntry journalEntry) {
        if (journalEntry == null){
            return;
        }
        mBinding.tvJournalDate.setText(dateFormat.format(journalEntry.getCreatedAt()));
        mBinding.tvJournalEntry.setText(journalEntry.getEntry());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_ENTRY_ID, mEntryId);
        super.onSaveInstanceState(outState);
    }

    private void deleteItem(final JournalEntry item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.journalDao().deleteEntry(item);
                    }
                });
                startActivity(new Intent(ViewItemActivity.this,JournalListActivity.class));
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    public List<JournalEntry> getJournalEntries() {
        return mJournalEntries;
    }

    /**
     * The method updates the list of journalEntries
     * and notifies the adapter to use the new values on it when data changes
     */
    public void setJournalEntries(List<JournalEntry> journalEntries) {
        mJournalEntries = journalEntries;
    }
}
