package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;

public class TimetableController extends Controller {

    public void writeToWriter(String text) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(new FileOutputStream("courseCalPerformance.txt", true));
        writer.println(text);
        writer.close();
    }

    public Result defaultResponseMain(){

        try {
            Content html = views.html.timetableMain.render();

            return ok(html);
        }catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

    public Result defaultResponse(String faculty){
        long startMobileServerTime = System.currentTimeMillis();
        int id = 0;
        try {
            String showBoth = "";
            Http.Session session = Http.Context.current().session();
            if (session.get("showBoth") == null)
            {
                session.put("showBoth", "false");
                showBoth = "false";
            } else
            {
                showBoth = session.get("showBoth");
            }

            Content html = views.html.timetable.render(faculty, showBoth);
            System.out.println("Trying to test !" + faculty);
            id ++;
            saveInDatabase(id,faculty);
            long MobileServerTime = System.currentTimeMillis() - startMobileServerTime;
            System.out.println("mobileServerTime " + MobileServerTime);
            writeToWriter("mobileServerTime" + MobileServerTime);
            return ok(html);
        }catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

    public Result saveTimetableMain(String faculty){
        long startServerTime = System.currentTimeMillis();
        int id = 0;
        try {
            String showBoth = "";
            Http.Session session = Http.Context.current().session();
            String facultyFromSession = session.get("faculty");
            if (faculty.equals(facultyFromSession))
            {
                session.put("showBoth", "false");
                showBoth = session.get("showBoth");
            } else
            {
                session.put("showBoth", "true");
                showBoth = session.get("showBoth");
            }
            session.put("faculty", faculty);
            Content html = views.html.timetable.render(faculty, showBoth);
            System.out.println("Trying to test !" + faculty);
            id ++;
            saveInDatabase(id,faculty);
            long serverTime = System.currentTimeMillis() - startServerTime;
            System.out.println("serverTime " + serverTime);
            writeToWriter("serverTime" + serverTime);
            return ok(html);
        }catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

    public Result saveTimetable(String faculty){
        long startServerTime = System.currentTimeMillis();
    int id = 0;
        try {
            Http.Session session = Http.Context.current().session();
            String facultyFromSession = session.get("faculty");
            if (faculty.equals(facultyFromSession))
            {
                session.put("showBoth", "false");
            } else
            {
                session.put("showBoth", "true");
            }
            session.put("faculty", faculty);
            Content html = views.html.timetableSearch.render(faculty);
            System.out.println("Trying to test !" + faculty);
            id ++;
            saveInDatabase(id,faculty);
            long serverTime = System.currentTimeMillis() - startServerTime;
            System.out.println("serverTime " + serverTime);
            writeToWriter("serverTime" + serverTime);
            return ok(html);
        }catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

    public static void saveInDatabase(int id, String faculty){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists timeTable;");
            stat.executeUpdate("create table timeTable (id, faculty);");
            PreparedStatement prep = conn.prepareStatement(
                    "insert into TimeTable values (?, ?);");

            prep.setString(1, String.valueOf(id));
            prep.setString(2, faculty);
            prep.addBatch();

            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);

            ResultSet rs = stat.executeQuery("select * from timeTable;");
            while (rs.next()) {
                System.out.println("id = " + rs.getString("id"));
                System.out.println("faculty = " + rs.getString("faculty"));
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
