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

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username = session.attribute("username");

                    HashMap m = new HashMap();
                    m.put("concerts", users.get(username).concerts);
                    return new ModelAndView(m, "concerts.html");
                }),
                new MustacheTemplateEngine()
        );
        Spark.post(
                "create-account",
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
    }
}
