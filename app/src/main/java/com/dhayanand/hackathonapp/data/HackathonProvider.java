package com.dhayanand.hackathonapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.dhayanand.hackathonapp.R;

/**
 * Created by Dhayanand on 1/14/2017.
 */

public class HackathonProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private HackathonDbHelper mOpenHelper;
    static final int HACKATHON = 100;
    static final int HACKATHON_BY_ID = 101;

    @Override
    public boolean onCreate() {
        mOpenHelper = new HackathonDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case HACKATHON:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HackathonContract.HackathonEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case HACKATHON_BY_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HackathonContract.HackathonEntry.TABLE_NAME,
                        projection,
                        HackathonContract.HackathonEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder

                );
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_url) + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case HACKATHON: {
                long _id = db.insert(HackathonContract.HackathonEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = HackathonContract.HackathonEntry.buildHackathonUri(_id);
                } else {
                    throw new android.database.SQLException( getContext().getString(R.string.failed_to_insert_row) + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_url) + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HACKATHON:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HackathonContract.HackathonEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case HACKATHON:
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(HackathonContract.HackathonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_url) + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case HACKATHON:
                rowsUpdated = mOpenHelper.getWritableDatabase().update(HackathonContract.HackathonEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_url) + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (HACKATHON):
                return HackathonContract.HackathonEntry.CONTENT_TYPE;
            case (HACKATHON_BY_ID):
                return HackathonContract.HackathonEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_url) + uri);

        }
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HackathonContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, HackathonContract.PATH_HACKATHON, HACKATHON);
        matcher.addURI(authority, HackathonContract.PATH_HACKATHON + "/*", HACKATHON_BY_ID);
        return matcher;
    }
}
