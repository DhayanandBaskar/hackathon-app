package com.dhayanand.hackathonapp.activities;

import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.adapters.FragmentAdapter;
import com.dhayanand.hackathonapp.adapters.HackathonAdapter;
import com.dhayanand.hackathonapp.data.Hackathon;
import com.dhayanand.hackathonapp.data.HackathonContract;
import com.dhayanand.hackathonapp.sync.HackathonSyncAdapter;
import com.dhayanand.hackathonapp.utils.Utilities;
import com.dhayanand.hackathonapp.widget.DetailWidgetProvider;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dhayanand.hackathonapp.sync.HackathonSyncAdapter.ACTION_DATA_UPDATED;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HackathonAdapter.Callback {

    private static final String LOG_CAT = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final int FRAGMENT_ALL = 1;
    public static final int FRAGMENT_HIRING = 2;
    public static final int FRAGMENT_HACKATHON = 3;
    public static final int FRAGMENT_BOOKMARK = 4;

    public static final String DEFAUTL_FRAGMENT_ID = "20";

    public static final String BOOKMARK_DB_VALUE = "FAV";
    public static final String NOT_BOOKMARK_DB_VALUE = "NFAV";

    public static final String BROADCAST_RECEIVER_FILTER_ACTION_1 = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String BROADCAST_RECEIVER_FILTER_ACTION_2 = "android.net.wifi.WIFI_STATE_CHANGED";

    public static final String SHARED_PREFERENCE_NOTIFICATION = "notification";

    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final String PAGER_STATE = "state";

    private static final int RC_SIGN_IN = 123;

    private static boolean registerReceiver = false;
    private static boolean isReceiverRegistered = false;
    private static boolean userSignedIn = false;


    private BroadcastReceiver receiver;
    Snackbar mSnackbar;

    private static ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private String mSelectionClause = HackathonContract.HackathonEntry.COLUMN_NAME + " Like ? OR " + HackathonContract.HackathonEntry.COLUMN_CATEGORY + " Like ?";
    private String[] mSelectionArgs = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        checkAndFetchHackathons();

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(savedInstanceState);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in

                    View headerLayout = navigationView.getHeaderView(0);

                    TextView userName = (TextView) headerLayout.findViewById(R.id.txvName);
                    TextView emailId = (TextView) headerLayout.findViewById(R.id.txvEmail);
                    ImageView userPic = (ImageView) headerLayout.findViewById(R.id.nav_hearder_img);

                    userName.setText(user.getDisplayName());
                    emailId.setText(user.getEmail());
                    Picasso.with(getBaseContext()).load(user.getPhotoUrl()).into(userPic);

                    userSignedIn = true;
                    checkAndFetchHackathons();
                    updateWidget();

                } else {
                    //user signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                                    .setTheme(R.style.GreenTheme)
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };

        if (userSignedIn)
            HackathonSyncAdapter.initializeSyncAdapter(this);

        setupNavigationView();
        updateWidget();
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        MenuItem switchItem = navigationView.getMenu().findItem(R.id.item_notification);
        CompoundButton switchView = (CompoundButton) MenuItemCompat.getActionView(switchItem);
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".my_pref_file", Context.MODE_PRIVATE);
        int notifi = sharedPreferences.getInt(SHARED_PREFERENCE_NOTIFICATION, 0);

        if(notifi == 1) {
            switchView.setChecked(true);
        } else {
            switchView.setChecked(false);
        }

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".my_pref_file", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (sharedPreferences.getInt(SHARED_PREFERENCE_NOTIFICATION, 1) == 1) {
                    editor.putInt(SHARED_PREFERENCE_NOTIFICATION, 0).apply();
                } else {
                    editor.putInt(SHARED_PREFERENCE_NOTIFICATION, 1).apply();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void setupViewPager(Bundle savedInstanceState) {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) setupViewPager(mViewPager);

        mViewPager.setSaveEnabled(true);
        mViewPager.setOffscreenPageLimit(2);

        tabs.setupWithViewPager(mViewPager);

        if (savedInstanceState != null) {
            mViewPager.setCurrentItem(savedInstanceState.getInt(PAGER_STATE));
        }
    }

    private void checkAndFetchHackathons() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .main_content);
        Cursor cursor = getContentResolver().query(
                HackathonContract.HackathonEntry.CONTENT_URI,
                Utilities.PROJECTION_COLUMNS,
                null,
                null,
                null
        );

        if (!cursor.moveToFirst()) {
            if (isNetworkConnected()) {
                if (userSignedIn)
                    HackathonSyncAdapter.syncImmediately(this);

                if (mSnackbar != null) {
                    mSnackbar.dismiss();
                }
                registerReceiver = false;
                setupViewPager(new Bundle());
            } else {
                mSnackbar = Snackbar
                        .make(coordinatorLayout, R.string.switch_on_internet_message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.connect_to_internet_snackbar, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                            }
                        });

                mSnackbar.show();
                registerReceiver = true;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            if (receiver != null)
                unregisterReceiver(receiver);
        }
        if (mFirebaseAuth != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (registerReceiver) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BROADCAST_RECEIVER_FILTER_ACTION_1);
            filter.addAction(BROADCAST_RECEIVER_FILTER_ACTION_2);

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    checkAndFetchHackathons();
                }
            };
            registerReceiver(receiver, filter);
            isReceiverRegistered = true;
        }

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FRAGMENT_ALL, getResources().getString(R.string.tab_all));
        adapter.addFragment(FRAGMENT_HIRING, getResources().getString(R.string.tab_hiring));
        adapter.addFragment(FRAGMENT_HACKATHON, getResources().getString(R.string.tab_hackathon));
        adapter.addFragment(FRAGMENT_BOOKMARK, getResources().getString(R.string.tab_bookmark));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int state = mViewPager.getCurrentItem();
        outState.putInt(PAGER_STATE, state);
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, SearchableActivity.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        final CursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.suggestion_item,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2},
                new int[]{R.id.textview_suggestion, R.id.textview_suggestion_catagory},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchView.setSuggestionsAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor c = getSuggestions(newText);
                adapter.changeCursor(c);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    private Cursor getSuggestions(String query) {
        query = "%" + query.toLowerCase() + "%";
        mSelectionArgs[0] = query;
        mSelectionArgs[1] = query;

        Cursor cursor = getContentResolver().query(
                HackathonContract.HackathonEntry.CONTENT_URI,
                Utilities.PROJECTION_COLUMNS_SUGGESTION,
                mSelectionClause,
                mSelectionArgs,
                null
        );

        return cursor;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        return isConnected;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_logout:
                if(isNetworkConnected()) {
                    userSignedIn = false;
                    getContentResolver().delete(HackathonContract.HackathonEntry.CONTENT_URI, null, null);

                    AppWidgetManager mgr = AppWidgetManager.getInstance(this);
                    int[] ids = mgr.getAppWidgetIds(new ComponentName(this, DetailWidgetProvider.class));

                    for (int id : ids) {
                        mgr.notifyAppWidgetViewDataChanged(id, R.id.widget_list);
                    }

                    AuthUI.getInstance().signOut(this);
                } else {
                    final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                            .main_content);
                    mSnackbar = Snackbar
                            .make(coordinatorLayout, R.string.switch_on_internet_message, Snackbar.LENGTH_SHORT);
                    mSnackbar.show();
                    onBackPressed();
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            hideDrawer();
        else
            super.onBackPressed();

    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void updateWidget() {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(this.getPackageName());
        this.sendBroadcast(dataUpdatedIntent);

        AppWidgetManager mgr = AppWidgetManager.getInstance(this);
        int[] ids = mgr.getAppWidgetIds(new ComponentName(this, DetailWidgetProvider.class));

        for (int id : ids) {
            mgr.notifyAppWidgetViewDataChanged(id, R.id.widget_list);
        }
    }

    @Override
    public void onItemSelected(Hackathon hackathon) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivityFragment.HACKATHON_INFO, hackathon.getId()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
