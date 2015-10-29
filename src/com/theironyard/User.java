package com.theironyard;

import java.util.ArrayList;

/**
 * Created by alhanger on 10/29/15.
 */
public class User {
    String username;
    String password;
    int concertNum;
    ArrayList<Concert> concerts;

    public User(String username, String password, int concertNum, ArrayList<Concert> concerts) {
        this.username = username;
        this.password = password;
        this.concertNum = concertNum;
        this.concerts = concerts;
    }
}
