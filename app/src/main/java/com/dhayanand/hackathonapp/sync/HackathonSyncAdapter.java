package com.dhayanand.hackathonapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.activities.MainActivity;
import com.dhayanand.hackathonapp.data.Hackathon;
import com.dhayanand.hackathonapp.data.HackathonContract;
import com.dhayanand.hackathonapp.retrofitmodel.TheHackathonAppDb;
import com.dhayanand.hackathonapp.retrofitmodel.model.Hackathons;
import com.dhayanand.hackathonapp.retrofitmodel.model.Raw;
import com.dhayanand.hackathonapp.utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static com.dhayanand.hackathonapp.activities.MainActivity.NOT_BOOKMARK_DB_VALUE;

public class HackathonSyncAdapter extends AbstractThreadedSyncAdapter implements Callback<Raw> {

    public static final String ACTION_DATA_UPDATED =
            "com.dhayanand.hackathonapp.ACTION_DATA_UPDATED";

    public static final String SHARED_PREFERENCE_NOTIFICATION = "notification";

    public final String LOG_TAG = HackathonSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 720;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private static final String NOTIFICATION_TITLE = "New Hackathon event";
    private static final String NOTIFICATION_CONTENT = "Check out the New Hackathon event";


    public HackathonSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        TheHackathonAppDb.getApiClient().getHackathonList().enqueue(this);
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        HackathonSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onResponse(Response<Raw> response, Retrofit retrofit) {


        Log.i(LOG_TAG, "[response] " + response.raw() + Arrays.toString(response.body().getHackathons()));
        Hackathons[] hackathons = response.body().getHackathons();

        List<Hackathon> hacks = extractAutoInformation(hackathons);
        ContentValues[] contentVals = new ContentValues[hacks.size()];

        for (int i = 0; i < hacks.size(); i++) {
            ContentValues val = Utilities.getContentValuesFromHackathonInfo(hacks.get(i));
            contentVals[i] = val;
        }

        getContext().getContentResolver().delete(HackathonContract.HackathonEntry.CONTENT_URI, null, null);
        getContext().getContentResolver().bulkInsert(HackathonContract.HackathonEntry.CONTENT_URI, contentVals);

        notifyUserAboutNewData();
        updateWidgets();
    }

    private void notifyUserAboutNewData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getPackageName() + ".my_pref_file", Context.MODE_PRIVATE);
        int notifi = sharedPreferences.getInt(SHARED_PREFERENCE_NOTIFICATION, 0);

        if (notifi == 1) {
            String lastNotificationKey = getContext().getString(R.string.pref_last_notification);
            long lastSync = sharedPreferences.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {

                Intent resultIntent = new Intent(getContext(), MainActivity.class);

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(NOTIFICATION_TITLE)
                                .setContentText(NOTIFICATION_CONTENT)
                                .setContentIntent(resultPendingIntent);

                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, mBuilder.build());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.commit();
            }
        }
    }


    private List<Hackathon> extractAutoInformation(Hackathons[] hackathons) {
        List<Hackathon> hacks = new ArrayList<>();
        for (int i = 0; i < hackathons.length; i++) {
            Hackathons res = hackathons[i];
            hacks.add(new Hackathon(
                    res.getId(),
                    res.getName(),
                    res.getImage(),
                    res.getCategory(),
                    res.getDescription(),
                    res.getExperience(),
                    NOT_BOOKMARK_DB_VALUE,
                    res.getWebsite()));
        }

        return hacks;
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    @Override
    public void onFailure(Throwable t) {
        onFailedToConnect();

    }

    public void onFailedToConnect() {

    }
}