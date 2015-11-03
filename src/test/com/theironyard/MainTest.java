package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Created by alhanger on 11/3/15.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.execute("DROP TABLE concerts");
        conn.close();
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alex", "", 1);
        User user = Main.selectUser(conn, "Alex");
        endConnection(conn);

        assertTrue(user != null);
    }

    @Test
    public void testConcert() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Alex", "", 1);
        Main.insertConcert(conn, 1, "Phish", "10/16/10", "North Charleston Coliseum", "Charleston, SC", "Excellent");
        Concert concert = Main.selectConcert(conn, 1);
        endConnection(conn);

        assertTrue(concert != null);
    }
}