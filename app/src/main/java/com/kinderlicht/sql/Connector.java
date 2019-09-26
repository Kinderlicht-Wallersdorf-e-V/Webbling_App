package com.kinderlicht.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kinderlicht.json.Member;

import java.util.ArrayList;

public class Connector extends SQLiteOpenHelper {

    private static final String TAG = "Connector";

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "temp_data_database";

    private static final String TABLE_MEMBERS = "member";
    private static final String COL_MEMBERS_ID = "ID";
    private static final String COL_MEMBERS_WEBLID = "webl_id";
    private static final String COL_MEMBERS_FNAME = "f_name";
    private static final String COL_MEMBERS_SNAME = "s_name";
    private static final String COL_MEMBERS_BIRTHDAY = "birthday";
    private static final String COL_MEMBERS_EMAIL = "email";
    private static final String COL_MEMBERS_AGE = "age";


    private static final String CMD_CREATE_TABLE_MEMBERS = "CREATE TABLE " + TABLE_MEMBERS + "(" +
            COL_MEMBERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_MEMBERS_WEBLID + " INTEGER UNIQUE, " +
            COL_MEMBERS_FNAME + " TEXT, " +
            COL_MEMBERS_SNAME + " TEXT, " +
            COL_MEMBERS_EMAIL + " TEXT, " +
            COL_MEMBERS_BIRTHDAY + " DATETIME" + ")";

    public Connector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD_CREATE_TABLE_MEMBERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        onCreate(db);
    }

    public void triggerTempDelete() {
        SQLiteDatabase db = this.getWritableDatabase();
        String statement = "DELETE FROM " + TABLE_MEMBERS + " WHERE " + COL_MEMBERS_ID + " > -1";
        db.execSQL(statement);
    }


    public boolean addMemberData(Member member) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MEMBERS_WEBLID, member.getWebl_id());
        contentValues.put(COL_MEMBERS_FNAME, member.getF_name());
        contentValues.put(COL_MEMBERS_SNAME, member.getS_name());
        contentValues.put(COL_MEMBERS_EMAIL, member.getEmail());
        contentValues.put(COL_MEMBERS_BIRTHDAY, member.getBirthday().toString());

        Log.d(TAG, "addData: Adding " + member.toString() + " to " + TABLE_MEMBERS);

        long result = db.insert(TABLE_MEMBERS, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     *
     * @return
     */
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MEMBERS;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getDataCount() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(" + COL_MEMBERS_ID + ") FROM " + TABLE_MEMBERS;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public ArrayList<Member> getBirthdayList(int months, String filter) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " +
                COL_MEMBERS_WEBLID + ", " +
                COL_MEMBERS_ID + ", " +
                COL_MEMBERS_FNAME + ", " +
                COL_MEMBERS_SNAME + ", " +
                COL_MEMBERS_EMAIL + ", " +
                COL_MEMBERS_BIRTHDAY + ", " +
                "CASE WHEN " + "strftime('%m', 'now') - strftime('%m', " + COL_MEMBERS_BIRTHDAY + ") < 0 " +
                "THEN " +
                "date(strftime('%Y', 'now')||strftime('-%m-%d', " + COL_MEMBERS_BIRTHDAY + ")) " +
                "ELSE " +
                "date(strftime('%Y', 'now', '+1 years')||strftime('-%m-%d', " + COL_MEMBERS_BIRTHDAY + ")) " +
                "END AS next_birthday " +
                "FROM " + TABLE_MEMBERS + " " +
                "WHERE next_birthday BETWEEN date('now') AND date('now', '+" + (months + 1) + " month', 'start of month', '-1 day') " +
                "AND " + COL_MEMBERS_SNAME + " || " + COL_MEMBERS_FNAME + " LIKE '%" + filter + "%' " +
                "ORDER BY next_birthday ASC";
        System.out.println(query);
        Cursor data = db.rawQuery(query, null);

        ArrayList<Member> list = new ArrayList<Member>();

        if (data.getCount() >= 1) {
            while (data.moveToNext()) {
                list.add(new Member(
                        data.getInt(data.getColumnIndex(COL_MEMBERS_WEBLID)),
                        data.getString(data.getColumnIndex(COL_MEMBERS_FNAME)),
                        data.getString(data.getColumnIndex(COL_MEMBERS_SNAME)),
                        data.getString(data.getColumnIndex(COL_MEMBERS_EMAIL)),
                        data.getString(data.getColumnIndex(COL_MEMBERS_BIRTHDAY))
                ));
            }
        }

        return list;
    }

    /**
     * Returns the member with the passed id
     *
     * @param id
     * @return curser
     */
    public Cursor getMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + "*" + " FROM " + TABLE_MEMBERS +
                " WHERE " + COL_MEMBERS_ID + " = " + id;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the name field
     *
     * @param new_F_Name: new family name that should be set
     * @param new_S_Name: new sur name that should be set
     * @param id
     */
    public void updateName(String new_F_Name, String new_S_Name, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_MEMBERS + " SET " + COL_MEMBERS_FNAME +
                " = '" + new_F_Name + "', " + COL_MEMBERS_SNAME + " = '" + new_S_Name + "'" +
                " WHERE " + COL_MEMBERS_ID + " = " + id + "";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting f_name to " + new_F_Name + " s_name to " + new_S_Name);
        db.execSQL(query);
    }

    /**
     * Delete from database
     *
     * @param id
     * @param name
     */
    public void deleteName(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_MEMBERS + " WHERE "
                + COL_MEMBERS_ID + " = " + id + "";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }

}
