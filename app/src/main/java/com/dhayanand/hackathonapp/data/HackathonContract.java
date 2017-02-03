package com.dhayanand.hackathonapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dhayanand on 1/14/2017.
 */

public class HackathonContract {

    public static final String CONTENT_AUTHORITY = "com.dhayanand.hackathonapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HACKATHON = "hackathon";

    public static final class HackathonEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_HACKATHON).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HACKATHON;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HACKATHON;

        public static final String TABLE_NAME = "hackathon";

        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SUGGESTION_PRIMARY = "suggest_text_1";
        public static final String COLUMN_SUGGESTION_SECONDARY = "suggest_text_2";
        public static final String COLUMN_SUGGESTION_INTENT_DATA_ID = "suggest_intent_data_id";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_EXPERIENCE = "experience";
        public static final String COLUMN_WEBSITE = "website";
        public static final String COLUMN_BOOKMARK = "bookmark";

        public static Uri buildHackathonUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
