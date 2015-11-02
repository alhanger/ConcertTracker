package com.theironyard;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {

        parseUsers();

        addTestUsers(users);

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    ArrayList<User> temp = new ArrayList(users.values());

                    HashMap m = new HashMap();
                    m.put("concerts", temp);
                    m.put("user", users.get(username));
                    return new ModelAndView(m, "user.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.get(
                "/concerts",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    User temp = users.get(username);

                    HashMap m = new HashMap();
                    m.put("user", users.get(username));
                    m.put("concerts", temp.concerts);
                    return new ModelAndView(m, "concerts.html");

                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/add-concert",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    String band = request.queryParams("bandName");
                    String date = request.queryParams("concertDate");
                    String venue = request.queryParams("concertVenue");
                    String location = request.queryParams("location");
                    String rating = request.queryParams("rating");

                    int id = users.get(username).concerts.size();

                    Concert concert = new Concert(band, date, venue, location, rating, id);
                    users.get(username).concerts.add(concert);
                    users.get(username).concertNum++;
                    writeToJson();

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
                    int dateIdNum = Integer.valueOf(dateId);

                    try {
                        Concert tempShow = users.get(username).concerts.get(dateIdNum);
                        users.get(username).concerts.remove(tempShow);
                        users.get(username).concertNum--;
                    } catch (Exception e) {

                    }

                    writeToJson();

                    response.redirect("/concerts");
                    return "";
                })
        );
        Spark.post(
                "/login",
                ((request, response) -> {
                    Session session = request.session();

                    String username = request.queryParams("username");
                    String password = request.queryParams("password");

                    session.attribute("username", username);

                    if (users.get(username) == null) {
                        User user = new User(username, password, 0, new ArrayList<Concert>());
                        users.put(username, user);
                        response.redirect("/concerts");
                    }
                    else if (password.equals(users.get(username).password) && username.equals(users.get(username).username)) {
                        response.redirect("/concerts");
                    }
                    else {
                        return "There was an error";
                    }
                    writeToJson();

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

    static void addTestUsers(HashMap<String, User> users) {
        users.put("Alex", new User("Alex", "hanger", 45, new ArrayList<Concert>()));
        users.put("Anna", new User("Anna", "williams", 20, new ArrayList<Concert>()));
        users.put("Geoffrey", new User("Geoffrey", "dyer", 35, new ArrayList<Concert>()));

        ArrayList<Concert> temp = users.get("Alex").concerts;
        temp.add(new Concert("Phish", "10/16/10", "North Charleston Colesium", "Charleston, SC", "Excellent", 0));

        ArrayList<Concert> temp2 = users.get("Anna").concerts;
        temp2.add(new Concert("Phish", "12/31/15", "American Airlines Arena", "Miami, FL", "Great", 0));

        ArrayList<Concert> temp3 = users.get("Geoffrey").concerts;
        temp3.add(new Concert("Phish", "08/01/2014", "The Wharf at Orange Beach", "Orange Beach, AL", "Great", 0));
    }

    //file reader
    static String readFile(String fileName) {
        File f = new File(fileName);
        try {
            FileReader fr = new FileReader(f);
            int fileSize = (int) f.length();
            char[] fileContent = new char[fileSize];
            fr.read(fileContent);
            return new String(fileContent);
        } catch (Exception e) {
            return null;
        }
    }

    //file writer
    static void writeFile(String fileName, String fileContent) {
        File f = new File(fileName);
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(fileContent);
            fw.close();
        } catch (Exception e) {

        }
    }

    //write file to JSON format
    static void writeToJson() {
        JsonSerializer serializer = new JsonSerializer();
        Users thing = new Users();
        thing.users = users;
        String output = serializer.include("*").serialize(thing);

        writeFile("users.json", output);
    }


    static void parseUsers() {
        String content = readFile("users.json");
        if (content != null) {
            JsonParser parser = new JsonParser();
            users = parser.parse(content, Users.class).users;
        }
    }
}
