/**
 * Created by Mbuthia Peter on 25/06/2018. As part of completion of 7 days of code challenge In
 * pursuit of the Google ALC nano degree program. ALC With Google 3.0
 */

package com.friki.mbuthia.journalapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.friki.mbuthia.journalapp.utils.DateConverter;

@Database(entities = {JournalEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class JournalDatabase extends RoomDatabase {
    private static final String LOG_TAG = JournalDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static JournalDatabase sInstance;

    //This method grants the access to the database object using a singleton pattern
    public static JournalDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        JournalDatabase.class, JournalDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract JournalDao journalDao();
}
