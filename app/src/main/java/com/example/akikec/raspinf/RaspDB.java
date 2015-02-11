package com.example.akikec.raspinf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RaspDB extends SQLiteOpenHelper  {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MyRefs.RASP_TABLE_NAME +" (" + MyRefs.UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MyRefs.DAY + " VARCHAR(30), "
                    + MyRefs.TIME +" VARCHAR(30), "
                    + MyRefs.PREDMET + " VARCHAR(255), "
                    + MyRefs.GROUP + " VARCHAR(30), "
                    + MyRefs.COURSE + " VARCHAR(1));";


    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + MyRefs.RASP_TABLE_NAME;


    public RaspDB(Context context) {

        super(context, MyRefs.DATABASE_NAME, null, MyRefs.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }

}
