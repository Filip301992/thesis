package controllers;

import appLogic.BusInfo;
import appLogic.BusInfoList;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.api.Application;
import play.api.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;


import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Properties;

@Singleton
public class MainController extends Controller {
    private final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    private String universitaStop = "Lugano-Universita";
    private String ponteMadonnettaStop = "Lugano-Ponte%20Madonnetta";
    private String corsoElvezia = "Corso%20Elvezia";
    private Date firstDeparture = null;
    private String endStation = "";
    private int id = 0;
    private int sizeOfQueryResults = 10;
    private static String busStop = "", busNumber = "", bBstation = "", color = "";
    @Inject
    WSClient ws;

    @Inject
    Provider<Application> app;


    public void writeToWriter(String text) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(new FileOutputStream("busDepPerformance.txt", true));
        writer.println(text);
        writer.close();
    }

    // 1. this data is sent via URL call, example:
    // http://localhost:9000/test?bNumber=5&bBstation=Lugano-Universita
    // busNumber = 5
    // bStation = Lugano-Universita
    public Result processRequest() throws FileNotFoundException, UnsupportedEncodingException {
        try {

            // 2. first try to get departure time
            List<BusInfo> busListUniversita = getTransportData(universitaStop, bBstation);
            List<BusInfo> busListPonteMadonnetta = getTransportData(ponteMadonnettaStop, bBstation);
            List<BusInfo> busListCorsoElvezia = getTransportData(corsoElvezia, bBstation);

            // TODO implement some logic to send warning signal if less than specific time is remaining before bus leaves
            Content html = views.html.busView.render(color, busStop, busNumber, simpleDateFormat.format(firstDeparture), bBstation, endStation, busListCorsoElvezia, busListPonteMadonnetta, busListUniversita);
            //System.out.println("Trying to test!");

            return ok(html);
        } catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

    public Result processRequestTable() {
        try {

            // 2. first try to get departure time
            List<BusInfo> busListUniversita = getTransportData(universitaStop, bBstation);
            List<BusInfo> busListPonteMadonnetta = getTransportData(ponteMadonnettaStop, bBstation);
            List<BusInfo> busListCorsoElvezia = getTransportData(corsoElvezia, bBstation);

            // TODO implement some logic to send warning signal if less than specific time is remaining before bus leaves
            Content html = views.html.busViewTable.render(color, busStop, busNumber, simpleDateFormat.format(firstDeparture), bBstation, endStation, busListCorsoElvezia, busListPonteMadonnetta, busListUniversita);
            //System.out.println("Trying to test!");

            return ok(html);
        } catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

    public Result showBusTime() {
        try {

            // 2. first try to get departure time
            List<BusInfo> busListUniversita = getTransportData(universitaStop, bBstation);
            List<BusInfo> busListPonteMadonnetta = getTransportData(ponteMadonnettaStop, bBstation);
            List<BusInfo> busListCorsoElvezia = getTransportData(corsoElvezia, bBstation);

            // TODO implement some logic to send warning signal if less than specific time is remaining before bus leaves
            //Content html = views.html.busView.render(color,busStop, busNumber, simpleDateFormat.format(firstDeparture), bBstation, endStation,busListCorsoElvezia, busListPonteMadonnetta,busListUniversita);
            //System.out.println("Trying to test!");

            return redirect("/search");//ok(html)
        } catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

    // TODO: implement logic if specific bus is not found within first 10 results, increase number of results and search again
    private List<BusInfo> getTransportData(String stop, String busStation) {
        List<BusInfo> busList = new ArrayList<>();
        try {

            busStation = busStation.replace(",", "-");

            // 3. SOAP call:
            long startTimeStamp = (System.currentTimeMillis());
            URL url = new URL("http://transport.opendata.ch/v1/stationboard?station=" + stop + "&fields[]=stationboard/number&fields[]=stationboard/stop/departure&fields[]=stationboard/to&limit=10");
            // NOTE: query gets next 10 incoming buses -> could optimize query to get specific bus data!
            //
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            if (conn.getResponseCode() != 200) {
                System.out.println("Try to call following URL:");
                System.out.println(url);
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            long apiTime = (System.currentTimeMillis() - startTimeStamp);
            System.out.println("apiTime " + apiTime);
            writeToWriter("Api " + apiTime);


            long startServerTime = System.currentTimeMillis();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            // mapper for Jakson JSON
            ObjectMapper mapper = new ObjectMapper();

            String output;
            //4. whole response will be sent in ONE line
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                BusInfoList busInfoList;
                System.out.println(output);
                // 5. Map incoming result to responding class
                busInfoList = mapper.readValue(output, BusInfoList.class);


                System.out.println("Detected a number of incoming buses: " + busInfoList.getsStaionboard().size());

                // 6. For each departure process data
                for (BusInfo busInfo : busInfoList.getsStaionboard()) {
                    busList.add(busInfo);
                    // 7. only process data if it is the bus we are looking for
//                    if (busInfo.getNumber().equals(busNumber)) {
                    System.out.println("---------------------- " + busInfo.getNumber());
                    System.out.println("---------------------- " + busList.size());

                    String departure = busInfo.getStop().getDeparture();

                    // NOTE: take care as Date formating is only done
                    String date = departure.substring(0, 10);
                    String time = departure.substring(11, 19);
                    String dateTime = date + " " + time;

                    System.out.println("Will try to parse date: " + dateTime);

                    if (firstDeparture == null) {
                        firstDeparture = simpleDateFormat.parse(dateTime);
                        endStation = busInfo.getTo();
                    } else {
                        // TODO: check if this is working properly
                        Date tmp = simpleDateFormat.parse(dateTime);
                        if (tmp.after(firstDeparture)) {
                            firstDeparture = tmp;
                            endStation = busInfo.getTo();
                            System.out.println("Successfully swapped date time!: " + firstDeparture);
                            //break;
                        }
                    }

                    System.out.println("Successfully found date time!: " + firstDeparture);

//                        break; // no need to conduct search no more
                    //}
//                    else {
//                        System.out.println("Not configured to process bus number: " + busInfo.getNumber());
//                    }
                }

                System.out.println("First bus comes at: " + firstDeparture);

            }
            long serverTime = System.currentTimeMillis() - startServerTime;
            System.out.println("serverTime " + serverTime);
            writeToWriter("Server " + serverTime);
            conn.disconnect();

        } catch (Exception e) {

            e.printStackTrace();

        }


        return busList;
    }

    public static void saveInDatabase(int id, String busStop, String busNumber, String bBstation, String color) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:test.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists busTable;");
            stat.executeUpdate("create table busTable (id, busStop,busNumber,bBstation,color);");
            PreparedStatement prep = conn.prepareStatement(
                    "insert into busTable values (?, ?,?,?,?);");

            prep.setString(1, String.valueOf(id));
            prep.setString(2, busStop);
            prep.setString(3, busNumber);
            prep.setString(4, bBstation);
            prep.setString(5, color);
            prep.addBatch();

            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);

            ResultSet rs = stat.executeQuery("select * from busTable;");
            while (rs.next()) {
                System.out.println(" ");

            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Result saveBusTime(String busStop, String busNumber, String bBstation, String color) {
        long startMobileServerTime = System.currentTimeMillis();
        System.out.println("----------  " + busStop + "==================");
        System.out.println("----------  " + color + "==================");
        try {

            this.busStop = busStop;
            this.busNumber = busNumber;
            this.bBstation = bBstation;
            this.color = color;

            saveInDatabase(id, busStop, busNumber, bBstation, color);
            id++;
            long MobileServerTime = System.currentTimeMillis() - startMobileServerTime;
            System.out.println("mobileServerTime " + MobileServerTime);
            writeToWriter("mobileServerTime " + MobileServerTime);
            return redirect("/search");//ok(html)

        } catch (Exception e) {
            System.out.println("Failed. Reason: " + e.getMessage());
        }
        return ok("There was an error while trying to compose HTML page.");
    }

}
