package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
	    HashMap<String, User> users = new HashMap<>();

        addTestUsers(users);

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    ArrayList<User> temp = new ArrayList<User>(users.values());

                    HashMap m = new HashMap();
                    m.put("concerts", temp);
                    m.put("user", users.get(username));
                    return new ModelAndView(m, "concerts.html");
                }),
                new MustacheTemplateEngine()
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
                        response.redirect("/");
                    }
                    else if (password.equals(users.get(username).password) && username.equals(users.get(username).username)) {
                        response.redirect("/");
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

    static void addTestUsers(HashMap<String, User> users) {
        users.put("Alex", new User("Alex", "hanger", 45, new ArrayList<Concert>()));
        users.put("Anna", new User("Anna", "williams", 20, new ArrayList<Concert>()));
        users.put("Geoffrey", new User("Geoffrey", "dyer", 35, new ArrayList<Concert>()));

        ArrayList<Concert> temp = users.get("Alex").concerts;
        temp.add(new Concert("Phish", "10/16/10", "North Charleston Colesium", "Charleston, SC"));

        ArrayList<Concert> temp2 = users.get("Anna").concerts;
        temp2.add(new Concert("Phish", "12/31/15", "American Airlines Arena", "Miami, FL"));

        ArrayList<Concert> temp3 = users.get("Geoffrey").concerts;
        temp3.add(new Concert("Phish", "08/01/2014", "The Wharf at Orange Beach", "Orange Beach, AL"));
    }
}
