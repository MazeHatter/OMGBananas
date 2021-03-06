package com.monadpad.omgbananas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SavedDataOpenHelper extends SQLiteOpenHelper {

    SavedDataOpenHelper(Context context) {
        super(context, "OMG_BANANAS", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE saves (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tags TEXT, data TEXT, time INTEGER, omg_id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        if (oldVersion == 1) {

            Log.d("MGH", "UPGRADE! converting table!");

            db.execSQL("CREATE TABLE saves (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tags TEXT, data TEXT, time INTEGER)");

            Cursor cursor = db.rawQuery("SELECT * FROM bananas", null);
            ContentValues data;

            int tagsColumn = cursor.getColumnIndex("TAGS");
            int dataColumn = cursor.getColumnIndex("DATA");


            while (cursor.moveToNext()) {
                data = new ContentValues();
                data.put("tags", cursor.getString(tagsColumn));
                data.put("data", cursor.getString(dataColumn));
                data.put("time", System.currentTimeMillis()/1000);
                db.insert("saves", null, data);

            }
            cursor.close();

            db.execSQL("DROP TABLE bananas");

        }

        if (oldVersion == 4) {
            db.execSQL("ALTER TABLE saves ADD COLUMN omg_id INTEGER");
        }
    }

    public Cursor getSavedCursor() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM saves ORDER BY time DESC", null);
        //db.close();

        return cursor;

    }

    public String getLastSaved() {
        String ret = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM saves ORDER BY time DESC LIMIT 1", null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            ret = cursor.getString(cursor.getColumnIndex("data"));
        }
        cursor.close();
        db.close();
        return ret;
    }

}