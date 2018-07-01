
/**
 * Created by Mbuthia Peter on 25/06/2018.
 * As part of completion of 7 days of code challenge
 * In pursuit of the Google ALC nano degree program.
 * ALC With Google 3.0
 */

package com.friki.mbuthia.journalapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "journal")
public class JournalEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userId;
    private String entry;
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @Ignore
    public JournalEntry(String userId, String entry, Date createdAt) {
        this.userId = userId;
        this.entry = entry;
        this.createdAt = createdAt;
    }

    @Ignore
    public JournalEntry(int id,String userId, String entry) {
        this.id = id;
        this.userId = userId;
        this.entry = entry;
    }

    public JournalEntry(int id, String userId, String entry, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.entry = entry;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
