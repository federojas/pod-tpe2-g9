package ar.edu.itba.pod.models;

public class Query2Reading {
    private Long year;
    private String day;
    private Long readings;


    public Query2Reading(Long year, String day, Long readings) {
        this.year = year;
        this.day = day;
        this.readings = readings;
    }

    public Long getYear() {
        return year;
    }

    public String getDay() {
        return day;
    }

    public Long getReadings() {
        return readings;
    }
}
