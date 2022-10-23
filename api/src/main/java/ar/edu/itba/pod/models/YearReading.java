package ar.edu.itba.pod.models;

public class YearReading {
    private Long readings;
    private boolean isWeekend;

    public YearReading(Long readings) {
        this.readings = readings;
    }

    public void isWeekend(boolean weekend) {
        isWeekend = weekend;
    }

    public Long getReadings() {
        return readings;
    }

    public boolean isWeekend() {
        return isWeekend;
    }
}
