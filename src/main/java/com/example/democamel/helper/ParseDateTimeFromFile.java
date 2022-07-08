package com.example.democamel.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParseDateTimeFromFile {

    public LocalDateTime parse(String dateString) {
        String[] timeCreatedString = dateString.split(" ");
        String date = timeCreatedString[0];
        String time = timeCreatedString[1].replace("-", ":");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date + " " + time, formatter);
    }
}
