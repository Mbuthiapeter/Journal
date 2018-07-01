package com.friki.mbuthia.journalapp.JournalViewModels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.friki.mbuthia.journalapp.database.JournalDatabase;

public class AddEntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final JournalDatabase mDb;
    private final int mEntryId;

    public AddEntryViewModelFactory(JournalDatabase database, int entryId) {
        mDb = database;
        mEntryId = entryId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class <T> modelClass) {
        return (T) new AddEntryViewModel(mDb, mEntryId);
    }
}
