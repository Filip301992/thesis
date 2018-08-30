package appLogic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Java class corresponding to stop/departures
 */
public class Stop {
    private final String dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
    private String departure;
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

    public String getDeparture() {
        return departure;
    }


    public String getDepartureCalendarHour() {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(departure));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int calHours = cal.get(Calendar.HOUR_OF_DAY);
        String hours = String.valueOf(calHours);
        if (calHours < 10) {
            hours = "0" + hours;
        }


        return hours;
    }

    public String getDepartureCalendarMinutes() {
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(sdf.parse(departure));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int calMinutes = cal.get(Calendar.MINUTE);
        String minutes = String.valueOf(calMinutes);
        if (calMinutes < 10) {
            minutes = "0" + minutes;
        }


        return minutes;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }
}
