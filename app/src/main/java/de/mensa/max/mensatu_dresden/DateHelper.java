package de.mensa.max.mensatu_dresden;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class DateHelper {

    private final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Get the current date as String
     *
     * @return current Date as "yyyy-MM-dd"
     */
    static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        Date date = Calendar.getInstance().getTime();
        String currentDate = dateFormat.format(date);
        System.out.println(currentDate);
        return currentDate;
    }

    /**
     * Get all dates from the current date to the next sunday
     *
     * @return all dates from now to the next sunday
     */
    static List<String> getNextSevenDays() {
        List<String> dates = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

        dates.add(dateFormat.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        // 1 = Sunday therefore we have to stop on 2 = Monday
        for (int i=0; i < 7; i++) {
            dates.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }

        return dates;
    }

}
