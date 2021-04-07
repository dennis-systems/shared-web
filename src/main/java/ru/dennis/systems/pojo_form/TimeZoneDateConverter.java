package ru.dennis.systems.pojo_form;

import ru.dennis.systems.pojo_view.DefaultDataConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeZoneDateConverter implements DefaultDataConverter<Date, String> {
    @Override
    public Date convert(Date date, String inuse){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setTimeZone(TimeZone.getTimeZone("CEST"));
        return cal.getTime();
    }
}
