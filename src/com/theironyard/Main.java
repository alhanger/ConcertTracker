package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, password VARCHAR, concertNum INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS concerts " +
                "(id IDENTITY, user_id INT, band VARCHAR, date VARCHAR, venue VARCHAR, location VARCHAR, rating VARCHAR)");
    }
    
    public static void insertUser(Connection conn, String username, String password, int concertNum) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setInt(3, concertNum);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String username) throws SQLException {
        User user = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            user = new User();
            user.id = results.getInt("id");
            user.username = results.getString("username");
            user.password = results.getString("password");
            user.concertNum = results.getInt("concertNum");
        }
        return user;
    }

    public static ArrayList<User> selectUsersList(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            User user = new User();
            user.id = results.getInt("id");
            user.username = results.getString("username");
            user.password = results.getString("password");
            user.concertNum = results.getInt("concertNum");
            users.add(user);
        }
        return users;
    }

    public static void insertConcert(Connection conn, int userId, String band, String date, String venue, String location, String rating) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO concerts VALUES (NULL, ?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, band);
        stmt.setString(3, date);
        stmt.setString(4, venue);
        stmt.setString(5, location);
        stmt.setString(6, rating);
        stmt.execute();
    }

    public static Concert selectConcert(Connection conn, int id) throws SQLException {
        Concert concert = null;
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM concerts INNER JOIN users " +
                "ON concerts.user_id = users.id " +
                "WHERE concerts.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            concert = new Concert();
            concert.id = results.getInt("user_id");
            concert.band = results.getString("band");
            concert.date = results.getString("date");
            concert.venue = results.getString("venue");
            concert.location = results.getString("location");
            concert.rating = results.getString("rating");
        }
        return concert;
    }

    public static ArrayList<Concert> selectConcertsLists(Connection conn, int userId) throws SQLException {
        ArrayList<Concert> concerts = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM concerts " +
                "INNER JOIN users ON concerts.user_id = users.id " +
                "WHERE users.id = ?");
        stmt.setInt(1, userId);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            Concert concert = new Concert();
            concert.band = results.getString("concerts.band");
            concert.date = results.getString("concerts.date");
            concert.venue = results.getString("concerts.venue");
            concert.location = results.getString("concerts.location");
            concert.rating = results.getString("concerts.rating");
            concert.id = results.getInt("concerts.id");
            concerts.add(concert);
        }
        return concerts;
    }

    public static void deleteConcert(Connection conn, int id) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM concerts WHERE concerts.id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public static void main(String[] args) throws SQLException {

        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    ArrayList<User> temp = selectUsersList(conn);

                    HashMap m = new HashMap();
                    m.put("concerts", temp);
                    m.put("user", selectUser(conn, username));
                    return new ModelAndView(m, "user.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/concerts",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    User temp = selectUser(conn, username);

                    HashMap m = new HashMap();
                    m.put("user", temp);
                    m.put("concerts", selectConcertsLists(conn, temp.id));
                    return new ModelAndView(m, "concerts.html");

                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/add-concert",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    User temp = selectUser(conn, username);

                    String band = request.queryParams("bandName");
                    String date = request.queryParams("concertDate");
                    String venue = request.queryParams("concertVenue");
                    String location = request.queryParams("location");
                    String rating = request.queryParams("rating");
                    int id = temp.id;

                    insertConcert(conn, id, band, date, venue, location, rating);

//                    Concert concert = new Concert(band, date, venue, location, rating, id);
//                    users.get(username).concerts.add(concert);
//                    users.get(username).concertNum++;

                    response.redirect("/concerts");
                    return "";
                })
        );
        Spark.post(
                "/delete-concert",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");
                    String dateId = request.queryParams("id");

                    try {
                        int dateIdNum = Integer.valueOf(dateId);
                        deleteConcert(conn, dateIdNum);
                    } catch (Exception e) {

                    }

                    response.redirect("/concerts");
                    return "";
                })
        );
//        Spark.post(
//                "/edit-concert",
//                ((request, response) -> {
//                    Session session = request.session();
//                    String username = session.attribute("username");
//                    String dateId = request.queryParams("id");
//                    int dateIdNum = Integer.valueOf(dateId);
//
//                    Concert temp = users.get(username).concerts.get(dateIdNum);
//
//                    HashMap m = new HashMap();
//                    m.put("user", users.get(username));
//                    m.put("concert", temp);
//
//                    return new ModelAndView(m, "edit-concert.html");
//                }),
//                new MustacheTemplateEngine()
//        );
//        Spark.post(
//                "/edit",
//                ((request, response) -> {
//                    Session session = request.session();
//                    String username = session.attribute("username");
//
//
//                    try{
//                        String dateId = request.queryParams("id");
//                        int dateIdNum = Integer.valueOf(dateId);
//                        Concert temp = users.get(username).concerts.get(dateIdNum);
//
//
//                        String band = request.queryParams("bandName");
//                        String date = request.queryParams("concertDate");
//                        String venue = request.queryParams("concertVenue");
//                        String location = request.queryParams("location");
//                        String rating = request.queryParams("rating");
//
//                        if (band.isEmpty()) {
//                            band = temp.band;
//                        }
//                        if (date.isEmpty()) {
//                            date = temp.date;
//                        }
//                        if (venue.isEmpty()){
//                            venue = temp.venue;
//                        }
//                        if (location.isEmpty()) {
//                            location = temp.location;
//                        }
//
//                        temp.band = band;
//                        temp.date = date;
//                        temp.venue = venue;
//                        temp.location = location;
//                        temp.rating = rating;
//                    } catch (Exception e) {
//
//                    }
//
//
//                    response.redirect("/concerts");
//                    return "";
//                })
//        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    Session session = request.session();

                    String username = request.queryParams("username");
                    String password = request.queryParams("password");

                    session.attribute("username", username);

                    User temp = selectUser(conn, username);

                    if (temp == null) {
                        insertUser(conn, username, password, 0);
                        response.redirect("/concerts");
                    }
                    else if (password.equals(temp.password) && username.equals(temp.username)) {
                        response.redirect("/concerts");
                    }
                    else {
                        return "There was an error";
                    }

                    return "";
                })
        );
        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
    }
}
