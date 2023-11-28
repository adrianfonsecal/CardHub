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
