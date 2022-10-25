package ar.edu.itba.pod.models;

import java.io.Serializable;

public class SensorReading implements Serializable {
    private final String sensorName;
    private final Long readings;
    private final Long year;
    private final String month;
    private final String weekDay;
    private final Integer day;
    private final Integer time;

    private SensorReading(String sensorName, Long readings, Long year,
                         String month, Integer day, Integer time, String weekDay) {
        this.sensorName = sensorName;
        this.readings = readings;
        this.year = year;
        this.month = month;
        this.day = day;
        this.time = time;
        this.weekDay = weekDay;
    }

    public static class SensorReadingBuilder {
        private String sensorName;
        private final Long readings;
        private Long year;
        private String month;
        private Integer day;
        private Integer time;
        private String weekDay;

        public SensorReadingBuilder(Long readings) {
            this.readings = readings;
        }

        public SensorReadingBuilder sensorName(String sensorName) {
            this.sensorName = sensorName;
            return this;
        }

        public SensorReadingBuilder year(Long year) {
            this.year = year;
            return this;
        }

        public SensorReadingBuilder month(String month) {
            this.month = month;
            return this;
        }

        public SensorReadingBuilder weekDay(String weekDay) {
            this.weekDay = weekDay;
            return this;
        }

        public SensorReadingBuilder day(Integer day) {
            this.day = day;
            return this;
        }

        public SensorReadingBuilder time(Integer time) {
            this.time = time;
            return this;
        }

        public SensorReading build() {
            return new SensorReading(sensorName,readings,year,month,day,time,weekDay);
        }
    }

    public String getSensorName() {
        return sensorName;
    }

    public Long getReadings() {
        return readings;
    }

    public Long getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getTime() {
        return time;
    }

    public String getWeekDay() {
        return weekDay;
    }
}
