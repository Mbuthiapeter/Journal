package com.friki.mbuthia.journalapp.JournalViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.friki.mbuthia.journalapp.database.JournalDatabase;
import com.friki.mbuthia.journalapp.database.JournalEntry;

public class AddEntryViewModel extends ViewModel {
    private LiveData<JournalEntry> entry;

    public AddEntryViewModel(JournalDatabase database, int entryId) {
        entry = database.journalDao().loadEntryById(entryId);
    }

    public LiveData<JournalEntry> getEntry() {
        return entry;
    }
}
