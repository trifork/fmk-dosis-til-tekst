package dk.medicinkortet.dosisstructuretext;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Represent a local time object, timezones or summertime are not handled.
 * Seconds are only included if they are provided
 */
public class LocalTime {
    private int hour;
    private int minute;
    private Integer second;

    public LocalTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.second = null;
    }

    public LocalTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Construct a new LocalTime object from a date.
     * Note! uses Europe/Copenhagen timezone
     * @param time time
     */
    public LocalTime(Date time) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Copenhagen"));
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        int secs = calendar.get(Calendar.SECOND);
        if (secs > 0) {
            second = secs;
        } else {
            second = null;
        }
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public boolean hasSeconds() {
        return second != null;
    }

    @Override
    public String toString() {
        if (hasSeconds()) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", hour, minute);
        }
    }
}
