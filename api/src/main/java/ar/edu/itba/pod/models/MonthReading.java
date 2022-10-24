package ar.edu.itba.pod.models;

import java.io.Serializable;

public class MonthReading implements Serializable {
    private final String month;
    private Long readings;

    public MonthReading(String month, Long readings) {
        this.month = month;
        this.readings = readings;
    }

    public MonthReading() {
        this.month = "";
        this.readings = 0L;
    }

    private enum daysInMonths {
        JANUARY(31),
        FEBRUARY(28),
        MARCH(31),
        APRIL(30),
        MAY(31),
        JUNE(30),
        JULY(31),
        AUGUST(31),
        SEPTEMBER(30),
        OCTOBER(31),
        NOVEMBER(30),
        DECEMBER(31);

        private final int days;

        daysInMonths(int days) {
            this.days = days;
        }

        public int getDays() {
            return days;
        }
    }

    public String getMonth() {
        return month;
    }

    public Long getReadings() {
        return readings;
    }

    public void setReadings(Long readings) {
        this.readings = readings;
    }

    public Double getMonthAverage() {
        return (double) readings / daysInMonths.valueOf(month.toUpperCase()).getDays();
    }
}
