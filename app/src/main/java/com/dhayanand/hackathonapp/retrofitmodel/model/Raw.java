package com.dhayanand.hackathonapp.retrofitmodel.model;

/**
 * Created by Dhayanand on 1/14/2017.
 */

public class Raw {

    Hackathons[] hackathons;
    String version;

    public Hackathons[] getHackathons() {
        return hackathons;
    }

    public void setHackathons(Hackathons[] hackathons) {
        this.hackathons = hackathons;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
