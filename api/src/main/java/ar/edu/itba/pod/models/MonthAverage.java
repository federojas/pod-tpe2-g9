package ar.edu.itba.pod.models;

public class MonthAverage implements Comparable<MonthAverage> {

    private final String month;
    private Double average;

    public MonthAverage(String month, Double average) {
        this.month = month;
        this.average = average;
    }

    public String getMonth() {
        return month;
    }

    public Double getAverage() {
        return average;
    }

    @Override
    public int compareTo(MonthAverage o) {
        return this.average.compareTo(o.average);
    }
}
