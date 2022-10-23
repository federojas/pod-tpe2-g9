package ar.edu.itba.pod.models;

public class YearCount {
    private Long readingsInWeekends;
    private Long readingsInWorkweeks;
    private Long readingsTotal;

    public YearCount() {
        readingsInWeekends = 0L;
        readingsInWorkweeks = 0L;
        readingsTotal = 0L;
    }

    public Long getReadingsInWeekends() {
        return readingsInWeekends;
    }

    public void setReadingsInWeekends(Long readingsInWeekends) {
        this.readingsInWeekends = readingsInWeekends;
    }

    public Long getReadingsInWorkweeks() {
        return readingsInWorkweeks;
    }

    public void setReadingsInWorkweeks(Long readingsInWorkweeks) {
        this.readingsInWorkweeks = readingsInWorkweeks;
    }

    public Long getReadingsTotal() {
        return readingsTotal;
    }

    public void setReadingsTotal(Long readingsTotal) {
        this.readingsTotal = readingsTotal;
    }
}
