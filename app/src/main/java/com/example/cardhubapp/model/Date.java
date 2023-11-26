package com.example.cardhubapp.model;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Date {
    private int year;
    private int month;
    private int day;
    public Date(String date){
        convertStringToDate(date);
        
    }

    private void convertStringToDate(String date) {
//        try {
//            // Paso 1: Convertir la fecha de cadena a un objeto Date
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date convertedDate = sdf.parse(date);
//
//            // Paso 2: Convertir el objeto Date a un objeto Calendar
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//
//            // Paso 3: Obtener el mes (ten en cuenta que los meses en Calendar son base 0)
//            int mes = calendar.get(Calendar.MONTH) + 1;  // Sumar 1 para ajustar al formato convencional (enero es 1)
//
//            // Imprimir el mes
//            System.out.println("El mes es: " + mes);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        String formattedCurrentDate = formatDateToYYYYMMDD(currentDate);
        return formattedCurrentDate;
    }

    private static String formatDateToYYYYMMDD(LocalDate currentDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }



}
