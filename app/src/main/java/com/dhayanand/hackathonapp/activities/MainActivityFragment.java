package com.dhayanand.hackathonapp.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.adapters.HackathonAdapter;
import com.dhayanand.hackathonapp.data.Hackathon;
import com.dhayanand.hackathonapp.data.HackathonContract;
import com.dhayanand.hackathonapp.utils.Utilities;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Dhayanand on 9/24/2016.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_CAT = MainActivityFragment.class.getSimpleName();
    public static final String FRAGMENT_KEY = "fragment";

    private static final String SORT_KEY_ALL = "ALL";
    private static final String SORT_KEY_BOOKMARK = "FAV";
    private static final String SORT_KEY_HIRING = "HIRING";
    private static final String SORT_KEY_HACKATHON = "HACKATHON";

    private static final int HACKATHON_LOADER_ALL = 0;
    private static final int HACKATHON_LOADER_BY_CATEGORY_HIRE = 1;
    private static final int HACKATHON_LOADER_BY_CATEGORY_HACK = 2;
    private static final int HACKATHON_LOADER_BOOKMARK = 3;


    private String mSortBy = SORT_KEY_ALL;
    private RecyclerView mRecyclerView;
    private HackathonAdapter adapter;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

    int mScrollPosition;


    private String mSelectionClause = HackathonContract.HackathonEntry.COLUMN_CATEGORY + " = ?";
    private String[] mSelectionArgs = new String[1];

    private String mSelectionClauseBookMark = HackathonContract.HackathonEntry.COLUMN_BOOKMARK + " = ?";
    private String[] mSelectionArgsFav = new String[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_main, container, false);
        setupRecyclerView();

        mRecyclerView.setAdapter(adapter);
        return mRecyclerView;
    }

    private void setupRecyclerView() {
        this.setupRecyclerView(null);
    }

    private void setupRecyclerView(ArrayList<Hackathon> hackathons) {
        setSorter();
        if (hackathons == null)
            hackathons = new ArrayList<Hackathon>();
        adapter = new HackathonAdapter(getActivity(), hackathons);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        mRecyclerView.setItemAnimator(animator);
    }


    private void setSorter() {
        Bundle bundle = getArguments();

        switch (bundle.getInt(FRAGMENT_KEY)) {
            case MainActivity.FRAGMENT_HIRING:
                mSortBy = SORT_KEY_HIRING;
                break;
            case MainActivity.FRAGMENT_HACKATHON:
                mSortBy = SORT_KEY_HACKATHON;
                break;
            case MainActivity.FRAGMENT_BOOKMARK:
                mSortBy = SORT_KEY_BOOKMARK;
                break;
            default:
                mSortBy = SORT_KEY_ALL;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(HACKATHON_LOADER_ALL, null, this);
        getLoaderManager().initLoader(HACKATHON_LOADER_BY_CATEGORY_HIRE, null, this);
        getLoaderManager().initLoader(HACKATHON_LOADER_BY_CATEGORY_HACK, null, this);
        getLoaderManager().initLoader(HACKATHON_LOADER_BOOKMARK, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSelectionArgs[0] = mSortBy;

        String mSelectionClauseFav = HackathonContract.HackathonEntry.COLUMN_BOOKMARK + " = ?";
        mSelectionArgsFav[0] = MainActivity.BOOKMARK_DB_VALUE;

        if (SORT_KEY_HIRING.equals(mSortBy)) {

            return new CursorLoader(getActivity(),
                    HackathonContract.HackathonEntry.CONTENT_URI,
                    Utilities.PROJECTION_COLUMNS,
                    mSelectionClause,
                    mSelectionArgs,
                    null
            );

        } else if (SORT_KEY_HACKATHON.equals(mSortBy)) {

            return new CursorLoader(getActivity(),
                    HackathonContract.HackathonEntry.CONTENT_URI,
                    Utilities.PROJECTION_COLUMNS,
                    mSelectionClause,
                    mSelectionArgs,
                    null
            );

        } else if (SORT_KEY_BOOKMARK.equals(mSortBy)) {

            return new CursorLoader(getActivity(),
                    HackathonContract.HackathonEntry.CONTENT_URI,
                    Utilities.PROJECTION_COLUMNS,
                    mSelectionClauseFav,
                    mSelectionArgsFav,
                    null
            );

        } else if (SORT_KEY_ALL.equals(mSortBy)) {
            return new CursorLoader(getActivity(),
                    HackathonContract.HackathonEntry.CONTENT_URI,
                    Utilities.PROJECTION_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.clear();
        adapter.addAll(Utilities.extractContentValues(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.clear();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
