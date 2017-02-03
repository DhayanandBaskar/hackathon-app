package com.dhayanand.hackathonapp.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.dhayanand.hackathonapp.data.Hackathon;
import com.dhayanand.hackathonapp.data.HackathonContract;

import java.util.ArrayList;
import java.util.List;


public class Utilities {

    public static final int COL_EVENT_ID_INDEX = 0;
    public static final int COL_NAME_INDEX = 1;
    public static final int COL_IMAGE_INDEX = 2;
    public static final int COL_CATEGORY_INDEX = 3;
    public static final int COL_DESCRIPTION_INDEX = 4;
    public static final int COL_EXPERIENCE_INDEX = 5;
    public static final int COL_BOOKMARK_INDEX = 6;
    public static final int COL_WEBSITE_INDEX = 7;

    public static final String[] PROJECTION_COLUMNS = {

            HackathonContract.HackathonEntry.COLUMN_EVENT_ID,
            HackathonContract.HackathonEntry.COLUMN_NAME,
            HackathonContract.HackathonEntry.COLUMN_IMAGE,
            HackathonContract.HackathonEntry.COLUMN_CATEGORY,
            HackathonContract.HackathonEntry.COLUMN_DESCRIPTION,
            HackathonContract.HackathonEntry.COLUMN_EXPERIENCE,
            HackathonContract.HackathonEntry.COLUMN_BOOKMARK,
            HackathonContract.HackathonEntry.COLUMN_WEBSITE

    };

    public static final String[] PROJECTION_COLUMNS_SUGGESTION = {

            HackathonContract.HackathonEntry._ID,
            HackathonContract.HackathonEntry.COLUMN_SUGGESTION_INTENT_DATA_ID,
            HackathonContract.HackathonEntry.COLUMN_SUGGESTION_PRIMARY,
            HackathonContract.HackathonEntry.COLUMN_SUGGESTION_SECONDARY,
    };

    public static ContentValues getContentValuesFromHackathonInfo(Hackathon hackathon) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_EVENT_ID, hackathon.getId());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_NAME, hackathon.getName());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_SUGGESTION_PRIMARY, hackathon.getName());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_IMAGE, hackathon.getImage());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_CATEGORY, hackathon.getCategory());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_SUGGESTION_SECONDARY, hackathon.getCategory());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_SUGGESTION_INTENT_DATA_ID, hackathon.getId());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_DESCRIPTION, hackathon.getDescription());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_EXPERIENCE, hackathon.getExperience());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_BOOKMARK, hackathon.getBookmark());
        contentValues.put(HackathonContract.HackathonEntry.COLUMN_WEBSITE, hackathon.getWebsite());
        return contentValues;
    }

    public static List<Hackathon> extractContentValues(Cursor cursor) {
        List<Hackathon> hackathons = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                hackathons.add(new Hackathon(
                        cursor.getString(COL_EVENT_ID_INDEX),
                        cursor.getString(COL_NAME_INDEX),
                        cursor.getString(COL_IMAGE_INDEX),
                        cursor.getString(COL_CATEGORY_INDEX),
                        cursor.getString(COL_DESCRIPTION_INDEX),
                        cursor.getString(COL_EXPERIENCE_INDEX),
                        cursor.getString(COL_BOOKMARK_INDEX),
                        cursor.getString(COL_WEBSITE_INDEX)
                ));
            } while (cursor.moveToNext());
        }

        return hackathons;
    }

    public static Hackathon extractSingleObjContentValues(Cursor cursor) {
        Hackathon hackathon = null;
        if (cursor.moveToFirst()) {

            hackathon = new Hackathon(
                    cursor.getString(COL_EVENT_ID_INDEX),
                    cursor.getString(COL_NAME_INDEX),
                    cursor.getString(COL_IMAGE_INDEX),
                    cursor.getString(COL_CATEGORY_INDEX),
                    cursor.getString(COL_DESCRIPTION_INDEX),
                    cursor.getString(COL_EXPERIENCE_INDEX),
                    cursor.getString(COL_BOOKMARK_INDEX),
                    cursor.getString(COL_WEBSITE_INDEX)
            );
        }

        return hackathon;
    }

}
