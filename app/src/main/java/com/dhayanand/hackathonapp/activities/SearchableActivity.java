package com.dhayanand.hackathonapp.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.adapters.HackathonAdapter;
import com.dhayanand.hackathonapp.data.Hackathon;
import com.dhayanand.hackathonapp.data.HackathonContract;
import com.dhayanand.hackathonapp.utils.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchableActivity extends AppCompatActivity implements  HackathonAdapter.Callback{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private RecyclerView mRecyclerView;
    private HackathonAdapter adapter;

    private String mSelectionClause = HackathonContract.HackathonEntry.COLUMN_NAME + " Like ? OR " + HackathonContract.HackathonEntry.COLUMN_CATEGORY + " Like ?";
    private String[] mSelectionArgs = new String[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_search_id);
        setupRecyclerView();

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String query = uri.getLastPathSegment().toLowerCase();

            Intent detailIntent = new Intent(this, DetailActivity.class);
            detailIntent.putExtra(DetailActivityFragment.HACKATHON_INFO, query.trim()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(detailIntent);
        }
    }

    private void doMySearch(String query) {
        query = "%"+query.toLowerCase()+"%";
        mSelectionArgs[0] = query;
        mSelectionArgs[1] = query;

        Cursor cursor = getContentResolver().query(
                HackathonContract.HackathonEntry.CONTENT_URI,
                Utilities.PROJECTION_COLUMNS,
                mSelectionClause,
                mSelectionArgs,
                null
        );
        adapter.clear();
        adapter.addAll(Utilities.extractContentValues(cursor));

    }

    private void setupRecyclerView() {
        this.setupRecyclerView(null);
    }

    private void setupRecyclerView(ArrayList<Hackathon> hackathons) {

        if (hackathons == null)
            hackathons = new ArrayList<Hackathon>();
        adapter = new HackathonAdapter(this, hackathons);
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

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(Hackathon hackathon) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivityFragment.HACKATHON_INFO, hackathon.getId()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
