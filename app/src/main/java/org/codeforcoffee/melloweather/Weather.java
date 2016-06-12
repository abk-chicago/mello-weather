package org.codeforcoffee.melloweather;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by codeforcoffee on 6/11/16.
 */
public class Weather {

    public final String DAY_OF_WEEK;
    public final String MIN_TEMP;
    public final String MAX_TEMP;
    public final String HUMIDITY;
    public final String DESCRIPTION;
    public final String ICON_URL;

    private static String convertTimeStampToDay(long timeStamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp * 1000);
        TimeZone tz = TimeZone.getDefault();
        cal.add(Calendar.MILLISECOND, tz.getOffset(cal.getTimeInMillis()));

        SimpleDateFormat df = new SimpleDateFormat("EEEE");
        return df.format(cal.getTime());
    }

    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity, String description, String iconName) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        this.DAY_OF_WEEK = convertTimeStampToDay(timeStamp);
        this.MIN_TEMP = nf.format(minTemp) + "\u00B0F";
        this.MAX_TEMP = nf.format(maxTemp) + "\u00B0F";
        this.HUMIDITY = nf.getPercentInstance().format(humidity / 100.0);
        this.DESCRIPTION = description;
        this.ICON_URL = "http://openweathermap.org/img/w/" + iconName + ".png";
    }

}
