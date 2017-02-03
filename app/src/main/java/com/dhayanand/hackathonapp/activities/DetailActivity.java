package com.dhayanand.hackathonapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dhayanand.hackathonapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dhayanand.hackathonapp.activities.DetailActivityFragment.HACKATHON_INFO;


/**
 * Created by Dhayanand on 1/15/2017.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_CAT = MainActivity.class.getSimpleName();
    public static final String EVENT_INFO = "eventSites";

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle arguments = new Bundle();
        arguments.putString(HACKATHON_INFO, getIntent().getStringExtra(DetailActivityFragment.HACKATHON_INFO));

        DetailActivityFragment fragment = new DetailActivityFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.hackathon_detail_container, fragment)
                .commit();

    }


}
