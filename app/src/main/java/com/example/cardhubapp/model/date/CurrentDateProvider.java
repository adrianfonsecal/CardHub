package com.example.cardhubapp.model.date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CurrentDateProvider {

    public static LocalDate getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate;
    }
}
