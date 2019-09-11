package com.kinderlicht.json;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Member {

    private String name;
    private String email;
    private LocalDate birthday;
    private int age;

    public Member(String name, String email, String birthday) {
        setName(name);
        setEmail(email);
        setBirthday(birthday);
        setAge((int)ChronoUnit.YEARS.between(this.birthday, LocalDate.now()));
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

    public String getBirthdayString(){
        String out = birthday.toString();
        String[] date = out.split("-");
        out = date[2] + "." + date[1] + "." + date[0];
        return out;
    }

    public void setBirthday(String birthday) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.birthday = LocalDate.parse(birthday, formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAgeOnNextBirthday(){
        return age + 1;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
