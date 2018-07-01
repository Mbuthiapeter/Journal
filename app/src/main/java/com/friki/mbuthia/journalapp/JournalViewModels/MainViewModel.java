package com.friki.mbuthia.journalapp.JournalViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.friki.mbuthia.journalapp.database.JournalDatabase;
import com.friki.mbuthia.journalapp.database.JournalEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<JournalEntry>> entries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        JournalDatabase database = JournalDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the entries from the DataBase");
        entries = database.journalDao().loadAllEntries();
    }

    public LiveData<List<JournalEntry>> getEntries() {
        return entries;
    }
}
