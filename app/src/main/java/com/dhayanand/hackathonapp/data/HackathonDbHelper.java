package com.dhayanand.hackathonapp.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dhayanand.hackathonapp.data.HackathonContract.HackathonEntry;

import static com.dhayanand.hackathonapp.data.HackathonContract.HackathonEntry.TABLE_NAME;

/**
 * Created by Dhayanand on 1/14/2017.
 */

public class HackathonDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 26;
    public static final String DATABASE_NAME = "hackathon.db";

    public HackathonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                HackathonEntry._ID + " INTEGER PRIMARY KEY," +
                HackathonEntry.COLUMN_EVENT_ID + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_NAME + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_SUGGESTION_PRIMARY + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_SUGGESTION_SECONDARY + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_SUGGESTION_INTENT_DATA_ID + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_IMAGE + " TEXT UNIQUE NOT NULL," +
                HackathonEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_EXPERIENCE + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_WEBSITE + " TEXT NOT NULL," +
                HackathonEntry.COLUMN_BOOKMARK + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void reset() throws SQLException {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
