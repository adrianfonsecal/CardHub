package com.example.cardhubapp.model.date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatesComparator {
    public static boolean compareDates(String firstDate, String secondDate) {
        LocalDate date1 = LocalDate.parse(firstDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate date2 = LocalDate.parse(secondDate, DateTimeFormatter.ISO_LOCAL_DATE);

        if (date1.isBefore(date2) || date1.isEqual(date2)) {
            return false;
        } else{
            return true;
        }
    }

}