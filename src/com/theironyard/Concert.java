package com.theironyard;

import java.util.ArrayList;

/**
 * Created by alhanger on 10/29/15.
 */
public class Concert {
    String venue;
    String date;
    String band;
    String location;
    String rating;
    int id;

    public Concert() {

    }

    public Concert(String band, String date, String venue, String location,String rating, int id) {
        this.band = band;
        this.date = date;
        this.venue = venue;
        this.location = location;
        this.rating = rating;
        this.id = id;
    }

    public String getVenue() {
        return venue;
    }

    public String getDate() {
        return date;
    }

    public String getBand() {
        return band;
    }

    public String getLocation() {
        return location;
    }

    public String getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }
}
