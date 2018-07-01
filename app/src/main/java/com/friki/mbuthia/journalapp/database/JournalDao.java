
/**
 * Created by Mbuthia Peter on 25/06/2018. As part of completion of 7 days of code challenge In
 * pursuit of the Google ALC nano degree program. ALC With Google 3.0
 */

package com.friki.mbuthia.journalapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface JournalDao {

    @Query("SELECT * FROM journal ORDER BY created_at")
    LiveData<List<JournalEntry>> loadAllEntries();

    @Insert
    void insertEntry(JournalEntry journalEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(JournalEntry journalEntry);

    @Delete
    void deleteEntry(JournalEntry journalEntry);

    @Query("SELECT * FROM journal WHERE id = :id")
    LiveData<JournalEntry> loadEntryById(int id);

}
