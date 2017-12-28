package de.mensa.max.mensatu_dresden;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class DateHelper {

    /**
     * Get the current date as String
     *
     * @return current Date as "yyyy-MM-dd"
     */
    static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
    static List<String> getWeekDatesTillSunday() {
        List<String> dates = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        dates.add(dateFormat.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        // 1 = Sunday therefore we have to stop on 2 = Monday
        while (cal.get(Calendar.DAY_OF_WEEK) != 2) {
            dates.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }

        return dates;
    }

}
