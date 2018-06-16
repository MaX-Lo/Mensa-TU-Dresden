package de.mensa.max.mensatu_dresden;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
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
        return dateFormat.format(date);
    }

    /**
     * Get all dates from the current date to the next sunday
     *
     * @return all dates from now to the next sunday
     */
    static List<String> getNextNDays(int n) {
        List<String> dates = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);

        // 1 = Sunday therefore we have to stop on 2 = Monday
        for (int i=0; i < n; i++) {
            dates.add(dateFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }

        return dates;
    }

    static List<String> getNextNWeekdays(int n) {
        Calendar cal = Calendar.getInstance();
        String[] Weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", };
        List<String> nextNWeekdays = new LinkedList<>();
        for (int i=0; i < n; i++) {
            nextNWeekdays.add(Weekdays[cal.get(Calendar.DAY_OF_WEEK) - 1]);
            cal.add(Calendar.DATE, 1);
        }
        return nextNWeekdays;
    };

}
