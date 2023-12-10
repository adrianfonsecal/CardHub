package com.example.cardhubapp.model.date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateService {

    public static LocalDate getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate;
    }
    public static String addOneMonthToFormattedDate(String inputDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(inputDate, formatter);
        LocalDate nextMonth = date.plusMonths(1);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return nextMonth.format(outputFormatter);
    }

    public static boolean compareDates(String date1, String date2) {
        LocalDate firstDate = LocalDate.parse(date1, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate secondDate = LocalDate.parse(date2, DateTimeFormatter.ISO_LOCAL_DATE);

        if (firstDate.isBefore(secondDate) || firstDate.isEqual(secondDate)) {
            return false;
        } else{
            return true;
        }
    }
}
