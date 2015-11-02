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

    public User() {

    }

    public User(String username, String password, int concertNum, ArrayList<Concert> concerts) {
        this.username = username;
        this.password = password;
        this.concertNum = concertNum;
        this.concerts = concerts;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getConcertNum() {
        return concertNum;
    }

    public ArrayList<Concert> getConcerts() {
        return concerts;
    }
}
