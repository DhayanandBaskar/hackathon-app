package com.dhayanand.hackathonapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.data.Hackathon;
import com.dhayanand.hackathonapp.data.HackathonContract;
import com.dhayanand.hackathonapp.utils.Utilities;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_CAT = MainActivity.class.getSimpleName();

    private ShareActionProvider mShareActionProvider;

    private static final int HACKATHON_LOADER = 0;
    private static final int WEBSITE_STAT_INDEX = 12;

    public static final String EXPERIENCE = "Experience ";
    public static final String SHARE_VIA = "Share via";
    public static final String SHARE_INTENT_TYPE = "text/plain";

    @BindView(R.id.detail_name)
    TextView mName;

    @BindView(R.id.bookmark)
    FloatingActionButton mBookmark;

    @BindView(R.id.description)
    TextView mDescription;

    @BindView(R.id.detail_category)
    TextView mCategory;

    @BindView(R.id.detail_site)
    TextView mSite;

    @BindView(R.id.detail_exp)
    TextView mExp;


    @BindView(R.id.detail_image)
    ImageView mImageDetail;

    public static final String HACKATHON_INFO = "hackathon";


    private Hackathon mHackathon;

    private String mSelectionClause = HackathonContract.HackathonEntry.COLUMN_EVENT_ID + " = ?";
    private String[] mSelectionArgs = new String[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        setHasOptionsMenu(true);

        return rootView;
    }

    private void fillBasicViews() {

        if (mHackathon != null) {
            mName.setText(mHackathon.getName());
            Picasso.with(getContext()).load(mHackathon.getImage()).into(mImageDetail);
            mExp.setText(EXPERIENCE + mHackathon.getExperience());
            mDescription.setText(mHackathon.getDescription());
            mCategory.setText(mHackathon.getCategory());
            mSite.setText(mHackathon.getWebsite().substring(WEBSITE_STAT_INDEX));

            if(!mHackathon.getBookmark().isEmpty()) {
                if (mHackathon.getBookmark().equals(MainActivity.NOT_BOOKMARK_DB_VALUE))
                    mBookmark.setImageResource(R.drawable.ic_bookmark_border_white_48dp);
                else
                    mBookmark.setImageResource(R.drawable.ic_bookmark_white_48dp);
            }
        }

        mBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectionArgs[0] = mHackathon.getId();
                ContentValues contentValue = new ContentValues();
                if (mHackathon.getBookmark().equals(MainActivity.NOT_BOOKMARK_DB_VALUE)) {
                    contentValue.put(HackathonContract.HackathonEntry.COLUMN_BOOKMARK, MainActivity.BOOKMARK_DB_VALUE);
                    mHackathon.setBookmark(MainActivity.BOOKMARK_DB_VALUE);
                } else {
                    contentValue.put(HackathonContract.HackathonEntry.COLUMN_BOOKMARK, MainActivity.NOT_BOOKMARK_DB_VALUE);
                    mHackathon.setBookmark(MainActivity.NOT_BOOKMARK_DB_VALUE);
                }

                getActivity().getContentResolver().update(
                        HackathonContract.HackathonEntry.CONTENT_URI,
                        contentValue,
                        mSelectionClause,
                        mSelectionArgs);

                if (mHackathon.getBookmark().equals(MainActivity.NOT_BOOKMARK_DB_VALUE))
                    mBookmark.setImageResource(R.drawable.ic_bookmark_border_white_48dp);
                else
                    mBookmark.setImageResource(R.drawable.ic_bookmark_white_48dp);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(HACKATHON_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void setShareIntent() {

        String shareText = mHackathon.getName() + ", " + mHackathon.getExperience() + ", " + mHackathon.getDescription();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType(SHARE_INTENT_TYPE);
        startActivity(Intent.createChooser(shareIntent, SHARE_VIA));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        mSelectionArgs[0] = getArguments().getString(HACKATHON_INFO);

        return new CursorLoader(getActivity(),
                HackathonContract.HackathonEntry.CONTENT_URI,
                Utilities.PROJECTION_COLUMNS,
                mSelectionClause,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mHackathon = Utilities.extractSingleObjContentValues(data);
        fillBasicViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mHackathon = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_share:
                setShareIntent();
                return true;
        }

        return false;
    }

}
