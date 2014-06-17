package com.monadpad.omgbananas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class OMGHelper {

    private static String mSubmitUrl = "omg";
    //private static String mHomeUrl = "http://10.0.2.2:8888/";
    private static String mHomeUrl = "http://openmusicgallery.appspot.com/";

    private Context mContext;

    private Type mType;
    private String mData;

    public enum Type {
        DRUMBEAT, BASSLINE, MELODY, CHORDPROGRESSION, SECTION
    }

    public OMGHelper(Context context, Type type, String data) {
        mContext =  context;
        mType = type;
        mData = data;

    }

    public void submitWithTags(String tags) {

        ContentValues data = new ContentValues();
        data.put("tags", tags);
        data.put("data", mData);
        data.put("time", System.currentTimeMillis()/1000);


        SQLiteDatabase db = new SavedDataOpenHelper(mContext).getWritableDatabase();
        db.insert("saves", null, data);
        db.close();

        new SaveToOMG().execute(mHomeUrl + mSubmitUrl, mType.toString(), tags, mData);

    }

}
