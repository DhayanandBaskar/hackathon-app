package com.dhayanand.hackathonapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dhayanand on 1/14/2017.
 */

public class Hackathon implements Parcelable {
    private String id;
    private String name;
    private String image;
    private String category;
    private String description;
    private String experience;
    private String bookmark;
    private String website;

    public Hackathon(String id, String name, String image, String category, String description, String experience, String bookmark, String website) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.category = category;
        this.description = description;
        this.experience = experience;
        this.bookmark = bookmark;
        this.website = website;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return "Hackathon{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", experience='" + experience + '\'' +
                ", bookmark='" + bookmark + '\'' +
                ", website='" + website + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.id, this.name, this.image, this.category, this.description, this.experience, this.bookmark, this.website});
    }

    public static final Parcelable.Creator<Hackathon> CREATOR
            = new Parcelable.Creator<Hackathon>() {
        public Hackathon createFromParcel(Parcel in) {
            return new Hackathon(in);
        }

        @Override
        public Hackathon[] newArray(int size) {
            return new Hackathon[0];
        }
    };

    public Hackathon(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        int count = 0;
        this.id = data[count++];
        this.name = data[count++];
        this.image = data[count++];
        this.description = data[count++];
        this.experience = data[count++];
        this.bookmark = data[count++];
        this.website = data[count++];
    }

}