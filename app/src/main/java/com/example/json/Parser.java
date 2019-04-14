package com.example.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;


public class Parser {

    /**
     * Takes a json string and returns a list of members sorted by upcoming birthdays
     * @param json Json string
     * @return list of users sorted by days to next birthday
     */
    public ArrayList<Member> createMembers(String json) {
        ArrayList<Member> user = new ArrayList<>();
        try {
            JSONArray jObject = new JSONArray(json);
            for (int i = 0; i < jObject.length(); i++) {
                JSONObject object = jObject.getJSONObject(i);
                JSONObject properties = (JSONObject) object.get("properties");
                user.add(new Member(properties.getString("Vorname") + " " + properties.getString("Name"), properties.getString("E-Mail"), properties.getString("Geburtstag")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Comparator<Member> birthday = new Comparator<Member>() {
            @Override
            public int compare(Member member, Member t1) {
                return (int)(member.daysToNextBirthday() - t1.daysToNextBirthday());
            }
        };

        Collections.sort(user, birthday);
        return user;
    }
}