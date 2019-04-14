package com.example.json;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Member {

    private String name;
    private String email;
    private LocalDate birthday;

    public Member(String name, String email, String birthday) {
        setName(name);
        setEmail(email);
        setBirthday(birthday);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.birthday = LocalDate.parse(birthday, formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long daysToNextBirthday() {
        String formatted = String.format("%04d-%02d-%02d", LocalDate.now().getYear(), birthday.getMonthValue(),
                birthday.getDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate thisyear = LocalDate.parse(formatted, formatter);
        long days = ChronoUnit.DAYS.between(LocalDate.now(), thisyear);
        if (days < 0) {
            formatted = String.format("%04d-%02d-%02d", LocalDate.now().getYear() + 1, birthday.getMonthValue(),
                    birthday.getDayOfMonth());
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            thisyear = LocalDate.parse(formatted, formatter);
            days = ChronoUnit.DAYS.between(LocalDate.now(), thisyear);
        }
        return days;
    }

    public String toString() {
        return name + " hat am " + birthday + " Geburtstag. Email an: " + email + "";
    }

}
