package models;

public class SensorReading {
    private Sensor sensor;
    private Long year;
    private String month;
    private Integer mDate;
    private String day;
    private Long hourlyCounts;

    public SensorReading(Sensor sensor, Long year, String month, Integer mDate,
                         String day, Long hourlyCounts) {
        this.year = year;
        this.month = month;
        this.mDate = mDate;
        this.day = day;
        this.hourlyCounts = hourlyCounts;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public Long getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public Integer getmDate() {
        return mDate;
    }

    public String getDay() {
        return day;
    }

    public Long getHourlyCounts() {
        return hourlyCounts;
    }
}

