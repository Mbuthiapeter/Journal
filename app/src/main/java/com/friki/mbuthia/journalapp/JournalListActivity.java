
/**
 * Created by Mbuthia Peter on 25/06/2018.
 * As part of completion of 7 days of code challenge
 * In pursuit of the Google ALC nano degree program.
 * ALC With Google 3.0
 */
package com.friki.mbuthia.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.friki.mbuthia.journalapp.JournalViewModels.MainViewModel;
import com.friki.mbuthia.journalapp.database.JournalDatabase;
import com.friki.mbuthia.journalapp.database.JournalEntry;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

// This Activity displays the Journal entries from the database
public class JournalListActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener{

    private FloatingActionButton mFab;
    // Constant for logging
    private static final String TAG = JournalListActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private JournalAdapter mAdapter;

    private JournalDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.journalListView);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new JournalAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mDb = JournalDatabase.getInstance(getApplicationContext());
        setupViewModel();

        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JournalListActivity.this,AddItemActivity.class));
            }
        });


    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getEntries().observe(this, new Observer<List<JournalEntry>>() {
            @Override
            public void onChanged(@Nullable List<JournalEntry> journalEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setJournalEntries(journalEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch ViewItemActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(JournalListActivity.this, ViewItemActivity.class);
        intent.putExtra(ViewItemActivity.EXTRA_ENTRY_ID, itemId);
        startActivity(intent);
    }


}
