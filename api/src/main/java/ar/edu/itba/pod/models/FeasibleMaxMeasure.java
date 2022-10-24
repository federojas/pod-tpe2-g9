package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.Calendar;

public class FeasibleMaxMeasure implements Serializable, Comparable<FeasibleMaxMeasure> {
    private final Long readings;
    private final Calendar date;

    public FeasibleMaxMeasure(Long count, Long year, String month, Integer day, Integer time) {
        this.readings = count;
        date = Calendar.getInstance();
        date.set(Calendar.YEAR, year.intValue());
        date.set(Calendar.MONTH, monthToInt(month));
        date.set(Calendar.DAY_OF_MONTH, day);
        date.set(Calendar.HOUR_OF_DAY, time);
    }

    public FeasibleMaxMeasure() {
        readings = 0L;
        date = Calendar.getInstance();
    }

    public Long getCount() {
        return readings;
    }

    public Calendar getDate() {
        return date;
    }

    @Override
    public int compareTo(FeasibleMaxMeasure feasibleMaxMeasure) {
       return this.getCount().compareTo(feasibleMaxMeasure.getCount());
    }

    private int monthToInt(String month) {
        switch (month.toLowerCase()) {
            case "january":
                return Calendar.JANUARY;
            case "february":
                return Calendar.FEBRUARY;
            case "march":
                return Calendar.MARCH;
            case "april":
                return Calendar.APRIL;
            case "may":
                return Calendar.MAY;
            case "june":
                return Calendar.JUNE;
            case "july":
                return Calendar.JULY;
            case "august":
                return Calendar.AUGUST;
            case "september":
                return Calendar.SEPTEMBER;
            case "october":
                return Calendar.OCTOBER;
            case "november":
                return Calendar.NOVEMBER;
            case "december":
                return Calendar.DECEMBER;
            default:
                return -1;
        }
    }
}
