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

    public Concert(String band, String date, String venue, String location) {
        this.band = band;
        this.date = date;
        this.venue = venue;
        this.location = location;
    }
}
