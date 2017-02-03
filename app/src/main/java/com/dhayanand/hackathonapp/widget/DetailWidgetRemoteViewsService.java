package com.dhayanand.hackathonapp.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.activities.DetailActivityFragment;
import com.dhayanand.hackathonapp.data.Hackathon;
import com.dhayanand.hackathonapp.data.HackathonContract;
import com.dhayanand.hackathonapp.utils.Utilities;
import com.facebook.internal.Utility;
import com.squareup.picasso.Picasso;


import java.util.concurrent.ExecutionException;

/**
 * Created by Dhayanand on 1/20/2017.
 */

public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(HackathonContract.HackathonEntry.CONTENT_URI,
                        Utilities.PROJECTION_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                String widName = data.getString(Utilities.COL_NAME_INDEX);
                String widCategory = data.getString(Utilities.COL_CATEGORY_INDEX);
                String widImgUrl = data.getString(Utilities.COL_IMAGE_INDEX);

                Bitmap image = null;

                    try {
                        image = Glide.with(DetailWidgetRemoteViewsService.this)
                                .load(widImgUrl)
                                .asBitmap()
                                .error(101)
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LOG_TAG, "Error retrieving large icon from " + widName, e);
                    }

                views.setImageViewBitmap(R.id.widget_icon, image);
                views.setTextViewText(R.id.widget_name, widName);
                views.setTextViewText(R.id.widget_category, widCategory);

                final Intent fillInIntent = new Intent();

                fillInIntent.putExtra(DetailActivityFragment.HACKATHON_INFO, data.getString(Utilities.COL_EVENT_ID_INDEX));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(Utilities.COL_EVENT_ID_INDEX);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
