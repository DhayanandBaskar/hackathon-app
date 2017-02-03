package com.dhayanand.hackathonapp.adapters;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.activities.MainActivity;
import com.dhayanand.hackathonapp.data.Hackathon;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.choiceMode;

/**
 * Created by Dhayanand on 1/15/2017.
 */

public class HackathonAdapter extends RecyclerView.Adapter<HackathonViewHolder> {
    private static final String LOG_CAT = HackathonAdapter.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<Hackathon> mHackathon;
    private static final int WEBSITE_STAT_INDEX = 12;
    final private ItemChoiceManager mICM;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Hackathon hackathon);
    }


    public HackathonAdapter(Activity activity, ArrayList<Hackathon> list) {
        this.mActivity = activity;
        this.mHackathon = list;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    public Hackathon getItem(int position) {
        return mHackathon.get(position);
    }

    public ArrayList<Hackathon> getItems() {
        return mHackathon;
    }

    @Override
    public HackathonViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mActivity)
                .inflate(R.layout.activity_main_list_item, parent, false);

        return new HackathonViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final HackathonViewHolder holder, final int i) {
        holder.mBoundHackathon = mHackathon.get(i);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Callback) mActivity)
                        .onItemSelected(holder.mBoundHackathon);
            }
        });

        holder.name.setText(holder.mBoundHackathon.getName());
        holder.category.setText(holder.mBoundHackathon.getCategory());
        holder.website.setText(holder.mBoundHackathon.getWebsite().substring(WEBSITE_STAT_INDEX));
        Picasso.with(mActivity).load(holder.mBoundHackathon.getImage()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (mHackathon == null)
            return 0;
        return mHackathon.size();
    }


    public void add(Hackathon websites) {
        this.mHackathon.add(websites);
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.mHackathon.clear();
        this.notifyDataSetChanged();
    }

    public void addAll(List<Hackathon> websites) {
        if (!this.mHackathon.isEmpty())
            return;
        this.mHackathon.addAll(websites);
        this.notifyDataSetChanged();
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }
}
