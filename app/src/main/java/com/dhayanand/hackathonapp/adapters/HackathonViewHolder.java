package com.dhayanand.hackathonapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhayanand.hackathonapp.R;
import com.dhayanand.hackathonapp.data.Hackathon;

/**
 * Created by Dhayanand on 1/18/2017.
 */

public class HackathonViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public ImageView mImageView;
    public TextView name;
    public TextView category;
    public TextView website;

    public Hackathon mBoundHackathon;

    public HackathonViewHolder(View view) {
        super(view);
        mView = view;
        mImageView = (ImageView) view.findViewById(R.id.img_row);
        name = (TextView) itemView.findViewById(R.id.name);
        category = (TextView) itemView.findViewById(R.id.category);
        website = (TextView) itemView.findViewById(R.id.website);
    }
}
